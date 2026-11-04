package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.*;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Snake1999 on 2016/1/22.
 * Package cn.nukkit.command.defaults in project nukkit.
 */
public class XpCommand extends VanillaCommand {
    public XpCommand(String name) {
        super(name, "%commands.xp.description", "%nukkit.command.xp.usage");
        this.setPermission("nukkit.command.xp");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("amount", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("level", new CommandParameter[]{
                CommandParameter.newPostfix("amount", CommandPostfixes.L),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Nullable
    @Override
    public CommandDataVersions generateCustomCommandData(Player player) {
        CommandDataVersions versions = super.generateCustomCommandData(player);
        if (versions == null) {
            return null;
        }

        // CommandData.clone() shares its collection fields. Filtering its overloads directly would
        // also change another player's already queued command packet. Keep this recipient's maps
        // independent, while retaining the existing parameter definitions and enum objects.
        CommandData source = versions.versions.getFirst();
        CommandData data = new CommandData();
        data.aliases = source.aliases;
        data.description = source.description;
        data.permission = source.permission;
        data.flags.addAll(source.flags);
        data.chainedSubCommandData.putAll(source.chainedSubCommandData);
        data.overloads.putAll(source.overloads);

        // Bedrock's remote command loader can resolve postfix indices through its hard-enum
        // mapping by mistake. Merely enabling the level overload used to produce XP_F0 /
        // SYMBOL_ERROR and a client allocation failure while typing "/xp ", before execution.
        // Only transports whose command encoder preserves the required enum/postfix relationship
        // may advertise this overload. Older codecs and the base Nukkit codec remain opted out;
        // the server-side l/L parser below is deliberately independent of this UI capability.
        if (!player.supportsCommandPostfixParameters()) {
            data.overloads.remove("level");
        }
        versions.versions.set(0, data);
        return versions;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            MutableBoolean isLevel = new MutableBoolean();
            int amount = parser.parse(arg -> {
                String num;
                if (arg.endsWith("l") || arg.endsWith("L")) {
                    num = arg.substring(0, arg.length() - 1);
                    isLevel.setTrue();
                } else {
                    num = arg;
                }

                try {
                    return Integer.parseInt(num);
                } catch (NumberFormatException e) {
                    throw CommandExceptions.NOT_INT;
                }
            });
            List<Player> targets = parser.parseTargetPlayersOrSelf();

            if (amount < 0 && isLevel.isFalse()) {
                sender.sendMessage(new TranslationContainer("commands.xp.failure.widthdrawXp"));
                return false;
            }

            List<String> success = new ObjectArrayList<>(targets.size());

            targets.forEach(target -> {
                if (isLevel.isTrue()) {
                    int newLevel = Math.min(target.getExperienceLevel() + amount, 24791);
                    if (newLevel < 0) {
                        target.setExperience(0, 0);
                    } else {
                        target.setExperience(target.getExperience(), newLevel, true);
                    }
                } else {
                    target.addExperience(amount);
                }

                success.add(target.getName());
            });

            if (success.isEmpty()) {
                parser.setErrorMessage(new TranslationContainer("%commands.generic.noTargetMatch"));
                throw CommandExceptions.NO_TARGET;
            }

            String names = String.join(", ", success);
            if (isLevel.isFalse() || amount == 0) {
                broadcastCommandMessage(sender, new TranslationContainer("commands.xp.success", amount, names));
            } else if (amount > 0) {
                broadcastCommandMessage(sender, new TranslationContainer("commands.xp.success.levels", amount, names));
            } else {
                broadcastCommandMessage(sender, new TranslationContainer("commands.xp.success.negative.levels", amount, names));
            }
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
