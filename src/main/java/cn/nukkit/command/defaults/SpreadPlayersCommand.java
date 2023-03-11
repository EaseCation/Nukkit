package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpreadPlayersCommand extends VanillaCommand {

    public SpreadPlayersCommand(String name) {
        super(name, "%nukkit.command.spreadplayers.description", "%nukkit.command.spreadplayers.usage");
        this.setPermission("nukkit.command.spreadplayers");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("x", CommandParamType.VALUE),
                CommandParameter.newType("z", CommandParamType.VALUE),
                CommandParameter.newType("spreadDistance", CommandParamType.FLOAT),
                CommandParameter.newType("maxRange", CommandParamType.FLOAT),
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
            Vector2 vec2 = parser.parseVector2();
            float spreadDistance = parser.parseFloat(0); //TODO
            float maxRange = parser.parseFloat(spreadDistance + 1);
            List<Player> targets = parser.parseTargetPlayers();

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (Player target : targets) {
                Vector3 pos = new Vector3(vec2.getX(), 0, vec2.getY());
                pos.x = Math.round(pos.x) + random.nextInt((int) -maxRange, (int) maxRange) + 0.5;
                pos.z = Math.round(pos.z) + random.nextInt((int) -maxRange, (int) maxRange) + 0.5;
                pos.y = target.getLevel().getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) + 1;
                target.teleport(pos);
            }

            sender.sendMessage(String.format("Successfully spread %1$d players around %2$.2f,%3$.2f", targets.size(), vec2.getX(), vec2.getY()));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
