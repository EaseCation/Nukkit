package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GiveCommand extends VanillaCommand {
    public GiveCommand(String name) {
        super(name, "%commands.give.description", "%nukkit.command.give.usage");
        this.setPermission("nukkit.command.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        });
        this.commandParameters.put("toPlayerById", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("itemId", CommandParamType.INT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
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
            Item item = parser.parseItem();
            int count = parser.parseIntOrDefault(1, 1, Short.MAX_VALUE);

            item.setCount(count);

            StringJoiner success = new StringJoiner(",");

            targets.forEach(target -> {
                success.add(target.getName());

                if (item.getId() == Item.AIR) {
                    return;
                }

                Item[] drops = target.getInventory().addItem(item.clone());
                if (drops.length != 0 && target.isSurvivalLike()) {
                    for (Item drop : drops) {
                        target.dropItem(drop);
                    }
                }
            });

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.give.success",
                    item.getName() + " (" + item.getId() + ":" + item.getDamage() + ")",
                    item.getCount(),
                    success.toString()));
            return true;
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }
        return false;
    }
}
