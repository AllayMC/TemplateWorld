package org.allaymc.templateworld;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;
import org.allaymc.api.world.World;

/**
 * @author daoge_cmd
 */
public class TemplateWorldCommand extends Command {

    public TemplateWorldCommand() {
        super("template", "TemplateWorld command", "templateworld.command");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("create")
                .str("template_name")
                .str("persistent_world_name").optional()
                .exec(context -> {
                    String templateName = context.getResult(1);
                    String persistentWorldName = context.getResult(2);
                    World world;
                    if (!persistentWorldName.isBlank()) {
                        world = TemplateWorld.createPersistentWorld(templateName, persistentWorldName);
                    } else {
                        world = TemplateWorld.createTmpWorld(templateName);
                    }
                    context.addOutput("Created template world " + TextFormat.GREEN + world.getName() + TextFormat.RESET + " (template: " + TextFormat.GREEN + templateName + TextFormat.RESET + ")");
                    return context.success();
                });
    }
}
