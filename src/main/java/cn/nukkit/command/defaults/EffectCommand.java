package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.EffectID;
import cn.nukkit.potion.Effects;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * Created by Snake1999 and Pub4Game on 2016/1/23.
 * Package cn.nukkit.command.defaults in project nukkit.
 */
public class EffectCommand extends Command {
    public EffectCommand(String name) {
        super(name, "%nukkit.command.effect.description", "%commands.effect.usage");
        this.setPermission("nukkit.command.effect");
        this.commandParameters.clear();

        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum.ENUM_EFFECT),
                CommandParameter.newType("seconds", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", new CommandEnum("ClearEffects", "clear"))
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> targets = parser.parseTargets();
            Effect effect = parser.parse(arg -> {
                if (arg.equalsIgnoreCase("clear")) {
                    return Effect.getEffect(Effect.NO_EFFECT);
                }

                Effect result;
                try {
                    result = Effect.getEffect(Integer.parseInt(arg));
                } catch (NumberFormatException a) {
                    result = Effects.getEffectByIdentifier(arg.toLowerCase());
                }
                if (result == null) {
                    parser.setErrorMessage(new TranslationContainer("%commands.effect.notFound", arg));
                    throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                }
                return result;
            });
            int durationSec = Math.min(parser.parseIntOrDefault(effect.isInstantaneous() ? 1 : 30, 0), 1000000);
            int amplifier = parser.parseIntOrDefault(0, 0, 255);
            boolean hideParticle = parser.parseBooleanOrDefault(false);

            if (effect.getId() == EffectID.NO_EFFECT) {
                targets.forEach(entity -> {
                    if (entity.getEffects().isEmpty()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.effect.failure.notActive.all", entity.getName()));
                        return;
                    }

                    entity.removeAllEffects();
                    broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success.removed.all", entity.getName()));
                });
                return true;
            }

            effect.setDuration(effect.isInstantaneous() ? durationSec : durationSec * 20).setAmplifier(amplifier);
            if (hideParticle) {
                effect.setVisible(false);
            }

            if (durationSec != 0) {
                targets.forEach(entity -> {
                    entity.addEffect(effect.clone());
                    broadcastCommandMessage(sender, new TranslationContainer("%commands.effect.success", effect.getName(), amplifier, entity.getName(), durationSec));
                });
            } else {
                targets.forEach(entity -> {
                    entity.removeEffect(effect.getId());
                    broadcastCommandMessage(sender, new TranslationContainer("%commands.effect.success", effect.getName(), amplifier, entity.getName(), durationSec));
                });
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
