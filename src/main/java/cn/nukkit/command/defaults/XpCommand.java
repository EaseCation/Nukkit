package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;

import static cn.nukkit.SharedConstants.*;

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
        if (!COMMAND_POSTFIX_PARAMETER_CRASH_FIXED) {
/* Debug_Log [ERROR SYSTEM]
Assertion failed: We failed to allocate 1686395861239 bytes.
Condition is false: pointer || size == 0
Function: void *__cdecl operator new(unsigned __int64) in .\handheld\src-deps\Core\src\Memory\MemoryOperators.cpp @ 19
*/
            return;
        }
        this.commandParameters.put("level", new CommandParameter[]{
                CommandParameter.newPostfix("amount", "l"),
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
