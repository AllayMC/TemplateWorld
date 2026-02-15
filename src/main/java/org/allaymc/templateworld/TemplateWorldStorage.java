package org.allaymc.templateworld;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.world.World;
import org.allaymc.api.world.WorldData;
import org.allaymc.api.world.chunk.Chunk;
import org.allaymc.api.world.data.DimensionInfo;
import org.allaymc.api.world.storage.WorldStorage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author daoge_cmd
 */
@Slf4j
public class TemplateWorldStorage implements WorldStorage {

    public static final String NAME = "TEMPLATE";

    protected static Path templatePath;
    protected static Path tmpWorldPath;
    protected static Map<String, WorldStorage> templates;

    protected WorldStorage templateStorage;
    protected WorldStorage targetStorage;
    protected Path targetPath;
    protected boolean persistent;

    public TemplateWorldStorage(String templateName, String templateFormat, String tmpWorldName, String tmpWorldFormat) {
        this.templateStorage = getTemplate(templateName, templateFormat);
        this.targetPath = tmpWorldPath.resolve(tmpWorldName);
        this.targetStorage = Registries.WORLD_STORAGE_FACTORIES.get(tmpWorldFormat).apply(targetPath);
        this.persistent = false;
    }

    public TemplateWorldStorage(String templateName, String templateFormat, WorldStorage targetStorage, boolean persistent) {
        this.templateStorage = getTemplate(templateName, templateFormat);
        this.targetStorage = targetStorage;
        this.persistent = persistent;
    }

    @SneakyThrows
    public static void init(Path path, Path tmpPath) {
        templatePath = path;
        tmpWorldPath = tmpPath;
        templates = new HashMap<>();

        if (!Files.exists(templatePath)) {
            Files.createDirectory(templatePath);
        }
        resetTmpWorldPath();
    }

    @SneakyThrows
    private static void resetTmpWorldPath() {
        log.info("Resetting tmp world path {}", tmpWorldPath);
        deletePath(tmpWorldPath);
        Files.createDirectories(tmpWorldPath);
        log.info("Reset tmp world path {}", tmpWorldPath);
    }

    @SneakyThrows
    public static void close() {
        templates.values().forEach(WorldStorage::shutdown);
    }

    private static synchronized WorldStorage getTemplate(String name, String format) {
        if (templates.containsKey(name)) {
            return templates.get(name);
        }

        var path = templatePath.resolve(name);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Template " + name + " not found");
        }

        var template = Registries.WORLD_STORAGE_FACTORIES.get(format).apply(path);
        templates.put(name, template);
        return template;
    }

    @Override
    public void tick(long currentTick) {
        targetStorage.tick(currentTick);
    }

    @Override
    public void setWorld(World world) {
        targetStorage.setWorld(world);
    }

    @Override
    public void shutdown() {
        targetStorage.shutdown();
        if (!persistent && targetPath != null) {
            deletePath(targetPath);
        }
    }

    private static void deletePath(Path path) {
        try {
            if (!Files.exists(path)) return;
            try (var walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        } catch (Exception e) {
            log.error("Failed to clean up tmp world at {}", path, e);
        }
    }

    @Override
    public CompletableFuture<Chunk> readChunk(int chunkX, int chunkZ, DimensionInfo dimensionInfo) {
        if (persistent && targetStorage.containChunk(chunkX, chunkZ, dimensionInfo)) {
            return targetStorage.readChunk(chunkX, chunkZ, dimensionInfo);
        }
        return templateStorage.readChunk(chunkX, chunkZ, dimensionInfo);
    }

    @Override
    public Chunk readChunkSync(int chunkX, int chunkZ, DimensionInfo dimensionInfo) {
        if (persistent && targetStorage.containChunk(chunkX, chunkZ, dimensionInfo)) {
            return targetStorage.readChunkSync(chunkX, chunkZ, dimensionInfo);
        }
        return templateStorage.readChunkSync(chunkX, chunkZ, dimensionInfo);
    }

    @Override
    public CompletableFuture<Void> writeChunk(Chunk chunk) {
        return targetStorage.writeChunk(chunk);
    }

    @Override
    public void writeChunkSync(Chunk chunk) {
        targetStorage.writeChunkSync(chunk);
    }

    @Override
    public CompletableFuture<Map<Long, Entity>> readEntities(int chunkX, int chunkZ, DimensionInfo dimensionInfo) {
        if (persistent && targetStorage.containChunk(chunkX, chunkZ, dimensionInfo)) {
            return targetStorage.readEntities(chunkX, chunkZ, dimensionInfo);
        }
        return templateStorage.readEntities(chunkX, chunkZ, dimensionInfo);
    }

    @Override
    public Map<Long, Entity> readEntitiesSync(int chunkX, int chunkZ, DimensionInfo dimensionInfo) {
        if (persistent && targetStorage.containChunk(chunkX, chunkZ, dimensionInfo)) {
            return targetStorage.readEntitiesSync(chunkX, chunkZ, dimensionInfo);
        }
        return templateStorage.readEntitiesSync(chunkX, chunkZ, dimensionInfo);
    }

    @Override
    public CompletableFuture<Void> writeEntities(int chunkX, int chunkZ, DimensionInfo dimensionInfo, Map<Long, Entity> entities) {
        return targetStorage.writeEntities(chunkX, chunkZ, dimensionInfo, entities);
    }

    @Override
    public void writeEntitiesSync(int chunkX, int chunkZ, DimensionInfo dimensionInfo, Map<Long, Entity> entities) {
        targetStorage.writeEntitiesSync(chunkX, chunkZ, dimensionInfo, entities);
    }

    @Override
    public boolean containChunk(int chunkX, int chunkZ, DimensionInfo dimensionInfo) {
        return templateStorage.containChunk(chunkX, chunkZ, dimensionInfo);
    }

    @Override
    public void writeWorldData(WorldData worldData) {
        targetStorage.writeWorldData(worldData);
    }

    @Override
    public WorldData readWorldData() {
        return templateStorage.readWorldData();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
