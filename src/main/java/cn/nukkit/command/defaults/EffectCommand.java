package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.EffectID;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * Created by Snake1999 and Pub4Game on 2016/1/23.
 * Package cn.nukkit.command.defaults in project nukkit.
 */
public class EffectCommand extends VanillaCommand {
    public EffectCommand(String name) {
        super(name, "%commands.effect.description", "%nukkit.command.effect.usage");
        this.setPermission("nukkit.command.effect");
        this.commandParameters.clear();

        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum.ENUM_EFFECT),
                CommandParameter.newType("seconds", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("infinite", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum.ENUM_EFFECT),
                CommandParameter.newEnum("Mode", new CommandEnum("AddInfiniteEffect", "infinite")),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN),
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("Mode", new CommandEnum("ClearEffects", "clear")),
                CommandParameter.newEnum("effect", true, CommandEnum.ENUM_EFFECT),
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
                parser.back();
                return parser.parseEffect();
            });
            Effect clearEffect = effect.getId() == EffectID.NO_EFFECT ? parser.parseEffectOrDefault((Effect) null) : null;
            int durationSec = parser.parseOrDefault(30, arg -> {
                if (arg.equalsIgnoreCase("infinite")) {
                    return -1;
                }
                parser.back();
                return Math.min(parser.parseInt(0), 1000000);
            });
            int amplifier = parser.parseIntOrDefault(0, 0, 255);
            boolean hideParticle = parser.parseBooleanOrDefault(false);

            if (effect.getId() == EffectID.NO_EFFECT) {
                if (clearEffect != null) {
                    targets.forEach(entity -> {
                        if (!entity.removeEffect(clearEffect.getId())) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.effect.failure.notActive", clearEffect.getName(), entity.getName()));
                            return;
                        }

                        broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success.removed", clearEffect.getName(), entity.getName()));
                    });
                    return true;
                }

                targets.forEach(entity -> {
                    if (!entity.removeAllEffects()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.effect.failure.notActive.all", entity.getName()));
                        return;
                    }

                    broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success.removed.all", entity.getName()));
                });
                return true;
            }

            effect.setDuration(effect.isInstantaneous() || durationSec == -1 ? durationSec : durationSec * 20)
                    .setAmplifier(amplifier);
            if (hideParticle) {
                effect.setVisible(false);
            }

            if (durationSec != 0) {
                targets.forEach(entity -> {
                    entity.addEffect(effect.clone());
                    if (durationSec == -1) {
                        broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success.infinite", effect.getName(), amplifier, entity.getName()));
                        return;
                    }
                    broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success", effect.getName(), amplifier, entity.getName(), durationSec));
                });
            } else {
                targets.forEach(entity -> {
                    entity.removeEffect(effect.getId());
                    broadcastCommandMessage(sender, new TranslationContainer("commands.effect.success", effect.getName(), amplifier, entity.getName(), durationSec));
                });
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
