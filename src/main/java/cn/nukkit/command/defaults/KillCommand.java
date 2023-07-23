package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.lang.TranslationContainer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * Created on 2015/12/08 by Pub4Game.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "%commands.kill.description", "%nukkit.command.kill.usage", "suicide");
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> targets = parser.parseTargetsOrSelf();

            List<String> success = new ObjectArrayList<>(targets.size());

            targets.forEach(target -> {
                if (target instanceof Player && ((Player) target).isCreativeLike()) {
                    return;
                }

                if (target == sender) {
                    if (!sender.hasPermission("nukkit.command.kill.self")) {
                        return;
                    }
                } else if (!sender.hasPermission("nukkit.command.kill.other")) {
                    return;
                }

                EntityDamageEvent ev = new EntityDamageEvent(target, DamageCause.SUICIDE, Short.MAX_VALUE);
                ev.call();
                if (ev.isCancelled()) {
                    return;
                }
                target.setLastDamageCause(ev);
                target.setHealth(0);

                success.add(target.getName());
            });

            if (success.isEmpty()) {
                parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                throw CommandExceptions.NO_TARGET;
            }

            broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", String.join(", ", success)));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
