package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.Enchantments;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Pub4Game on 23.01.2016.
 */
public class EnchantCommand extends VanillaCommand {

    public EnchantCommand(String name) {
        super(name, "%commands.enchant.description", "%nukkit.command.enchant.usage");
        this.setPermission("nukkit.command.enchant");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.commandParameters.put("byName", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", CommandEnum.ENUM_ENCHANT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets = parser.parseTargetPlayers();
            Enchantment enchantment = parser.parse(arg -> {
                Enchantment result;
                try {
                    result = Enchantment.getEnchantment(Integer.parseInt(arg));
                } catch (NumberFormatException a) {
                    result = Enchantments.getEnchantmentByIdentifier(arg.toLowerCase());
                }
                if (result == null) {
//                    parser.setErrorMessage(new TranslationContainer("%commands.enchant.notFound", arg));
                    throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
                }
                return result;
            });
            int level = parser.parseIntOrDefault(1, 1, Short.MAX_VALUE);

            enchantment.setLevel(level);

            StringJoiner success = new StringJoiner(",");
            StringJoiner noItem = new StringJoiner(",");

            targets.forEach(target -> {
                Item item = target.getInventory().getItemInHand();

                if (item.isNull()) {
                    noItem.add(target.getName());
                    return;
                }

                item.addEnchantment(enchantment);
                target.getInventory().setItemInHand(item);

                success.add(target.getName());
            });

            if (noItem.length() != 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.enchant.noItem", noItem.toString()));
            }
            if (success.length() != 0) {
                Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.enchant.success", success.toString()));
                return true;
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
