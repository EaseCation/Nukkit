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
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2015/11/12 by Pub4Game and milkice.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TeleportCommand extends VanillaCommand {
    private static final int MAX_COORDINATE = 30_000_000;

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

                if (!(sender instanceof Entity entity)) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                    throw CommandExceptions.NO_TARGET;
                }

                parser.back();
                victims = Collections.singletonList(entity);
            }

            destination = parser.parseVector3TargetOrDefault((Vector3) null);
            if (destination == null) {
                if (unresolvedVictim != null) {
                    throw unresolvedVictim;
                }

                if (!(sender instanceof Entity entity)) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                    throw CommandExceptions.NO_TARGET;
                }
                if (victims.size() != 1) {
                    parser.setErrorMessage(new TranslationContainer("%commands.generic.tooManyTargets"));
                    throw CommandExceptions.TOO_MANY_TARGETS;
                }

                destination = victims.getFirst();
                victims = Collections.singletonList(entity);
            }

            float yRot = parser.parseFloatOrDefault(Float.NaN);
            float xRot = parser.parseFloatOrDefault(Float.NaN);

            Level level = destination instanceof Position pos ? pos.level : parser.getTargetLevel();
            boolean isCoordinates = !(destination instanceof Entity);
            Vector3 destPos = new Vector3(Mth.clamp(destination.x, -MAX_COORDINATE, MAX_COORDINATE), Mth.clamp(destination.y, -MAX_COORDINATE, MAX_COORDINATE), Mth.clamp(destination.z, -MAX_COORDINATE, MAX_COORDINATE));
            float yaw = Float.isNaN(yRot) ? yRot : Mth.wrapDegrees(yRot);
            float pitch = Float.isNaN(xRot) ? xRot : Mth.clamp(xRot, -90, 90);

            final Vector3 dest = destination;
            victims.forEach(victim -> {
                victim.teleport(Location.fromObject(destPos, level, Float.isNaN(yaw) ? victim.yaw : yaw, Float.isNaN(pitch) ? victim.pitch : pitch), TeleportCause.COMMAND);

                if (!isCoordinates && victim instanceof Player player) {
                    player.sendMessage(new TranslationContainer("commands.tp.successVictim", ((Entity) dest).getName()));
                }

                broadcastCommandMessage(sender, isCoordinates ?
                        new TranslationContainer("commands.tp.success.coordinates", victim.getName(), Mth.round(destPos.x, 2), Mth.round(destPos.y, 2), Mth.round(destPos.z, 2)) :
                        new TranslationContainer("commands.tp.success", victim.getName(), ((Entity) dest).getName()));
            });
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
