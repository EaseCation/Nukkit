package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SetWorldSpawnCommand extends VanillaCommand {
    public SetWorldSpawnCommand(String name) {
        super(name, "%commands.setworldspawn.description", "%nukkit.command.setworldspawn.usage");
        this.setPermission("nukkit.command.setworldspawn");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("spawnPoint", true, CommandParamType.POSITION)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Position pos = parser.parsePositionOrSelf();

            pos.level.setSpawnLocation(pos);

            broadcastCommandMessage(sender, new TranslationContainer("commands.setworldspawn.success", pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
