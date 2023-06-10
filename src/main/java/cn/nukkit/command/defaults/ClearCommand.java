package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ClearCommand extends VanillaCommand {

    public ClearCommand(String name) {
        super(name, "%commands.clear.description", "%nukkit.command.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newEnum("item", true, CommandEnum.ENUM_ITEM)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("maxCount", true, CommandParamType.INT),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets;
            int maxCount = -1;

            Item item = null;

            if (parser.hasNext()) {
                targets = parser.parseTargetPlayers();
                if (parser.hasNext()) {
                    item = parser.parseItem();
                    if (parser.hasNext()) {
                        maxCount = parser.parseInt(-1);
                    }
                }
            } else if (sender instanceof Player) {
                targets = ObjectArrayList.of((Player) sender);
            } else {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return true;
            }

            for (Player target : targets) {
                PlayerInventory inventory = target.getInventory();
                PlayerOffhandInventory offhand = target.getOffhandInventory();

                if (item == null) {
                    int count = 0;

                    for (Int2ObjectMap.Entry<Item> entry : inventory.getContents().int2ObjectEntrySet()) {
                        Item slot = entry.getValue();
                        if (!slot.isNull()) {
                            count += slot.getCount();
                            inventory.clear(entry.getIntKey());
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (!slot.isNull()) {
                        count += slot.getCount();
                        offhand.clear(0);
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), count));
                    }
                } else if (maxCount == 0) {
                    int count = 0;

                    for (Item slot : inventory.getContents().values()) {
                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (item.equals(slot, item.hasMeta(), false)) {
                        count += slot.getCount();
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("%1$s has %2$d items that match the criteria", target.getName(), count));
                    }
                } else if (maxCount == -1) {
                    int count = 0;

                    for (Int2ObjectMap.Entry<Item> entry : inventory.getContents().int2ObjectEntrySet()) {
                        Item slot = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                            inventory.clear(entry.getIntKey());
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (item.equals(slot, item.hasMeta(), false)) {
                        count += slot.getCount();
                        offhand.clear(0);
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), count));
                    }
                } else {
                    int remaining = maxCount;

                    for (Int2ObjectMap.Entry<Item> entry : inventory.getContents().int2ObjectEntrySet()) {
                        Item slot = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            int count = slot.getCount();
                            int amount = Math.min(count, remaining);

                            slot.setCount(count - amount);
                            inventory.setItem(entry.getIntKey(), slot);

                            if ((remaining -= amount) <= 0) {
                                break;
                            }
                        }
                    }

                    if (remaining > 0) {
                        Item slot = offhand.getItem(0);
                        if (item.equals(slot, item.hasMeta(), false)) {
                            int count = slot.getCount();
                            int amount = Math.min(count, remaining);

                            slot.setCount(count - amount);
                            inventory.setItem(0, slot);
                            remaining -= amount;
                        }
                    }

                    if (remaining == maxCount) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), maxCount - remaining));
                    }
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }

        return true;
    }
}
