package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.network.protocol.StopSoundPacket;

import java.util.List;
import java.util.stream.Collectors;

public class StopSoundCommand extends VanillaCommand {

    public StopSoundCommand(String name) {
        super(name, "%commands.stopsound.description", "%nukkit.command.stopsound.usage");
        this.setPermission("nukkit.command.sound.stop");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("sound", true, CommandParamType.STRING),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets = parser.parseTargetPlayers();
            String sound = "";

            if (parser.hasNext()) {
                sound = parser.literal();
            }

            StopSoundPacket packet = new StopSoundPacket();
            packet.name = sound;
            if (sound.isEmpty()) {
                packet.stopAll = true;
            }

            Server.broadcastPacket(targets, packet);

            sender.sendMessage(String.format(packet.stopAll ? "Stopped all sounds for %2$s" : "Stopped sound '%1$s' for %2$s", sound, targets.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "))));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
