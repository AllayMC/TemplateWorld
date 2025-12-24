package org.allaymc.templateworld;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;

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
                .exec(context -> {
                    String templateName = context.getResult(1);
                    var world = TemplateWorld.createTmpWorld(templateName);
                    context.addOutput("Created tmp world " + TextFormat.GREEN + world.getName() + TextFormat.RESET + " (template: " + TextFormat.GREEN + templateName + TextFormat.RESET + ")");
                    return context.success();
                });
    }
}
