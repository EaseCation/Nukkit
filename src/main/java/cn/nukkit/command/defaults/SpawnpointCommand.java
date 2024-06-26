package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "%commands.spawnpoint.description", "%nukkit.command.spawnpoint.usage");
        this.setPermission("nukkit.command.spawnpoint");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets = parser.parseTargetPlayersOrSelf();
            Position pos = parser.parsePositionOrSelf();

            List<String> success = new ObjectArrayList<>(targets.size());

            targets.forEach(target -> {
                target.setSpawn(pos);

                success.add(target.getName());
            });

            if (success.size() == 1) {
                broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.single", success.get(0), pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));
            } else {
                broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.multiple.specific", String.join(", ", success), pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
