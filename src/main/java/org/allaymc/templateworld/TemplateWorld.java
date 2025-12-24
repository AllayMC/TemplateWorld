package org.allaymc.templateworld;

import lombok.SneakyThrows;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.world.World;

import java.nio.file.Path;
import java.util.UUID;

public class TemplateWorld extends Plugin {

    protected static final Path TEMPLATE_PATH = Path.of("templates");
    protected static final Path TMP_WORLD_PATH = Path.of("worlds/.tmp");

    /**
     * Creates a temporary runtime-only world based on the specified template.
     * The template is loaded using LEVELDB format.
     *
     * @param templateName the name of the template to be used for creating the world
     * @return the created temporary world
     */
    public static World createTmpWorld(String templateName) {
        return createTmpWorld(templateName, "LEVELDB", "LEVELDB");
    }

    /**
     * Creates a temporary runtime-only world using the specified template, template format,
     * and temporary world format. The world is loaded and marked as runtime-only.
     *
     * @param templateName the name of the template to be used for creating the world
     * @param templateFormat the format of the template (e.g., LEVELDB)
     * @param tmpWorldFormat the format of the temporary world (e.g., LEVELDB)
     * @return the created temporary world instance
     */
    public static World createTmpWorld(String templateName, String templateFormat, String tmpWorldFormat) {
        var tmpWorldName = UUID.randomUUID().toString();
        var voidGeneratorFactory = Registries.WORLD_GENERATOR_FACTORIES.get("VOID");
        var worldPool = Server.getInstance().getWorldPool();
        worldPool.loadWorld(
                tmpWorldName, new TemplateWorldStorage(templateName, templateFormat, tmpWorldName, tmpWorldFormat),
                voidGeneratorFactory.apply(""),
                voidGeneratorFactory.apply(""),
                voidGeneratorFactory.apply("")
        );

        var world = worldPool.getWorld(tmpWorldName);
        world.setRuntimeOnly(true);

        return world;
    }

    @SneakyThrows
    @Override
    public void onLoad() {
        TemplateWorldStorage.init(TEMPLATE_PATH, TMP_WORLD_PATH);
        Registries.COMMANDS.register(new TemplateWorldCommand());
    }

    @Override
    public void onDisable() {
        TemplateWorldStorage.close();
    }
}