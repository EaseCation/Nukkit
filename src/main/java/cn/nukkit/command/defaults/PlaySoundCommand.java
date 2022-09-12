package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class PlaySoundCommand extends VanillaCommand {

    public PlaySoundCommand(String name) {
        super(name, "%nukkit.command.playsound.description", "%nukkit.command.playsound.usage");
        this.setPermission("nukkit.command.sound.play");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("sound", CommandParamType.STRING),
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newType("position", true, CommandParamType.POSITION),
                CommandParameter.newType("volume", true, CommandParamType.FLOAT),
                CommandParameter.newType("pitch", true, CommandParamType.FLOAT),
                CommandParameter.newType("minimumVolume", true, CommandParamType.FLOAT),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            String sound = parser.literal();
            List<Player> targets;
            Position position = null;
            double volume = 1;
            double pitch = 1;
            double minimumVolume = 0;

            if (parser.hasNext()) {
                targets = parser.parseTargetPlayers();
                if (parser.hasNext()) {
                    position = parser.parsePosition();
                    if (parser.hasNext()) {
                        volume = parser.parseDouble();
                        if (parser.hasNext()) {
                            pitch = parser.parseDouble();
                            if (parser.hasNext()) {
                                minimumVolume = Math.max(parser.parseDouble(), 0);
                            }
                        }
                    }
                }
            } else if (sender instanceof Player) {
                targets = ObjectArrayList.of((Player) sender);
            } else {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return false;
            }

            if (position == null) {
                if (sender instanceof Position)  {
                    position = (Position) sender;
                } else {
                    position = new Position(0, 0, 0, parser.getTargetLevel());
                }
            }

            if (targets.isEmpty()) {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return false;
            }

            double maxDistance = volume > 1 ? volume * 16 : 16;
            List<String> successes = new ObjectArrayList<>();

            for (Player player : targets) {
                String name = player.getName();
                PlaySoundPacket packet = new PlaySoundPacket();

                if (position.distance(player) > maxDistance) {
                    if (minimumVolume <= 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Player %1$s is too far away to hear the sound", name));
                        break;
                    }

                    packet.volume = (float) minimumVolume;
                    packet.x = player.getFloorX();
                    packet.y = player.getFloorY();
                    packet.z = player.getFloorZ();
                } else {
                    packet.volume = (float) volume;
                    packet.x = position.getFloorX();
                    packet.y = position.getFloorY();
                    packet.z = position.getFloorZ();
                }

                packet.name = sound;
                packet.pitch = (float) pitch;
                player.dataPacket(packet);

                successes.add(name);
            }

            sender.sendMessage(String.format("Played sound '%1$s' to %2$s", sound, String.join(", ", successes)));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
