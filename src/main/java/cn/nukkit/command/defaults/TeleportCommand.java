package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2015/11/12 by Pub4Game and milkice.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
        super(name, "%commands.tp.description", "%nukkit.command.tp.usage");
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("->Player", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.TARGET),
        });
        this.commandParameters.put("Player->Player", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.TARGET)
        });
        this.commandParameters.put("Player->Pos", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        });
        this.commandParameters.put("->Pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> victims;
            Vector3 destination;
            CommandSyntaxException unresolvedVictim;

            try {
                victims = parser.parseTargets();
                unresolvedVictim = null;
            } catch (CommandSyntaxException ex) {
                if (ex == CommandExceptions.END_OF_COMMAND) {
                    throw ex;
                }
                unresolvedVictim = ex;

                if (!(sender instanceof Entity)) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                    throw CommandExceptions.NO_TARGET;
                }

                parser.back();
                victims = Collections.singletonList((Entity) sender);
            }

            destination = parser.parseVector3TargetOrDefault((Vector3) null);
            if (destination == null) {
                if (unresolvedVictim != null) {
                    throw unresolvedVictim;
                }

                if (!(sender instanceof Entity)) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                    throw CommandExceptions.NO_TARGET;
                }
                if (victims.size() != 1) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.tooManyTargets"));
                    throw CommandExceptions.TOO_MANY_TARGETS;
                }

                destination = victims.get(0);
                victims = Collections.singletonList((Entity) sender);
            }

            float yRot = parser.parseFloatOrDefault(Float.NaN) % 180;
            float xRot = parser.parseFloatOrDefault(Float.NaN) % 180;

            Level level = destination instanceof Position ? ((Position) destination).level : parser.getTargetLevel();
            boolean isCoordinates = !(destination instanceof Entity);

            final Vector3 dest = destination;
            victims.forEach(victim -> {
                victim.teleport(Location.fromObject(dest, level, Float.isNaN(yRot) ? victim.yaw : yRot, Float.isNaN(xRot) ? victim.pitch : xRot), TeleportCause.COMMAND);

                if (!isCoordinates && victim instanceof Player) {
                    ((Player) victim).sendMessage(new TranslationContainer("commands.tp.successVictim", ((Entity) dest).getName()));
                }

                broadcastCommandMessage(sender, isCoordinates ?
                        new TranslationContainer("commands.tp.success.coordinates", victim.getName(), NukkitMath.round(dest.x, 2), NukkitMath.round(dest.y, 2), NukkitMath.round(dest.z, 2)) :
                        new TranslationContainer("commands.tp.success", victim.getName(), ((Entity) dest).getName()));
            });
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
