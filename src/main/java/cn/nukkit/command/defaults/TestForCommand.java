package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class TestForCommand extends VanillaCommand {

    public TestForCommand(String name) {
        super(name, "%commands.testfor.description", "%nukkit.command.testfor.usage");
        this.setPermission("nukkit.command.testfor");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> targets = parser.parseTargets();

            sender.sendMessage(String.format("Found %1$s", targets.stream()
                    .map(Entity::getName)
                    .collect(Collectors.joining(", "))));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
