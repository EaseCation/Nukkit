package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;

public class ToggleDownfallCommand extends VanillaCommand {

    public ToggleDownfallCommand(String name) {
        super(name, "%commands.toggledownfall.description", "%nukkit.command.toggledownfall.usage");
        this.setPermission("nukkit.command.weather");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);

        Level level = parser.getTargetLevel();
        level.setRaining(!level.isRaining());

        sender.sendMessage(new TranslationContainer("commands.downfall.success"));

        return true;
    }
}
