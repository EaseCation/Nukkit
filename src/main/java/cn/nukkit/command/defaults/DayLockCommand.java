package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;

public class DayLockCommand extends VanillaCommand {

    public DayLockCommand(String name) {
        super(name, "%nukkit.command.daylock.description", "%nukkit.command.daylock.usage", "alwaysday");
        this.setPermission("nukkit.command.gamerule");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("lock", true, CommandEnum.ENUM_BOOLEAN),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            boolean lock = true;

            if (parser.hasNext()) {
                lock = parser.parseBoolean();
            }

            Level level = parser.getTargetLevel();
            GameRules rules = level.getGameRules();

            if (lock) {
                rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                level.stopTime();
                level.setTime(5000);
                sender.sendMessage("Day-Night cycle locked");
            } else {
                rules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                level.startTime();
                sender.sendMessage("Day-Night cycle unlocked");
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }
}
