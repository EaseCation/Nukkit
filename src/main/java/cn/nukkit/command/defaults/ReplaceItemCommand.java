package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamOption;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandExceptions;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Mth;
import cn.nukkit.utils.TextFormat;

import java.util.List;

public class ReplaceItemCommand extends VanillaCommand {

    public ReplaceItemCommand(String name) {
        super(name, "%nukkit.command.replaceitem.description", "%nukkit.command.replaceitem.usage");
        this.setPermission("nukkit.command.replaceitem");
        this.commandParameters.clear();
        this.commandParameters.put("block", new CommandParameter[]{
                CommandParameter.newEnum("block", new CommandEnum("ReplaceItemBlock", "block")),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slotType", new CommandEnum("BlockEquipmentSlot", "slot.container")),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("item", CommandEnum.ENUM_ITEM)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
        });
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity", new CommandEnum("ReplaceItemEntity", "entity")),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("slotType", new CommandEnum("EntityEquipmentSlot", "slot.weapon.mainhand", "slot.weapon.offhand",
                                "slot.armor.head", "slot.armor.chest", "slot.armor.legs", "slot.armor.feet",
                                "slot.hotbar", "slot.inventory", "slot.enderchest", "slot.saddle", "slot.armor", "slot.chest")),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("item", CommandEnum.ENUM_ITEM)
                        .addOption(CommandParamOption.HAS_SEMANTIC_CONSTRAINT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            String slotType;
            int slotId;
            Item item;
            int amount = 1;

            switch (parser.literal().toLowerCase()) {
                case "block":
                    Position position = parser.parsePosition();
                    BlockEquipmentSlot blockSlotType = parseBlockEquipmentSlot(slotType = parser.literal());
                    slotId = parser.parseInt(0);
                    item = parser.parseItem();
                    if (parser.hasNext()) {
                        amount = parser.parseInt(1);
                    }

                    amount = Mth.clamp(amount, 0, item.getMaxStackSize());
                    item.setCount(amount);
                    Level level = position.getLevel();

                    switch (blockSlotType) {
                        case SLOT_CONTAINER:
                            BlockEntity blockEntity = level.getBlockEntity(position);
                            Inventory inventory;

                            if (blockEntity instanceof InventoryHolder && (inventory = ((InventoryHolder) blockEntity).getInventory()) != null) {
                                int size = inventory.getSize();

                                if (size > slotId) {
                                    inventory.setItem(slotId, item);
                                    sender.sendMessage(String.format("Replaced %1$s slot %2$d with %3$d * %4$s", slotType, slotId, amount, item.getName()));
                                } else {
                                    sender.sendMessage(String.format(TextFormat.RED + "Could not replace slot %1$s, must be a value between %2$d and %3$d.", slotType, 0, size));
                                    return false;
                                }
                            } else {
                                sender.sendMessage(String.format(TextFormat.RED + "Block at Pos(%1$d,%2$d,%3$d) is not a container", position.getFloorX(), position.getFloorY(), position.getFloorZ()));
                                return false;
                            }

                            break;
                    }

                    break;
                case "entity":
                    List<Player> targets = parser.parseTargetPlayers(); //TODO: entities
                    EntityEquipmentSlot entitySlotType = parseEntityEquipmentSlot(slotType = parser.literal());
                    slotId = parser.parseInt(0);
                    item = parser.parseItem();
                    if (parser.hasNext()) {
                        amount = parser.parseInt(1);
                    }

                    amount = Mth.clamp(amount, 0, item.getMaxStackSize());
                    item.setCount(amount);

                    for (Player target : targets) {
                        switch (entitySlotType) {
                            case SLOT_WEAPON_MAINHAND:
                                target.getInventory().setItemInHand(item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_WEAPON_OFFHAND:
                                target.getOffhandInventory().setItem(0, item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_ARMOR_HEAD:
                                target.getInventory().setHelmet(item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_ARMOR_CHEST:
                                target.getInventory().setChestplate(item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_ARMOR_LEGS:
                                target.getInventory().setLeggings(item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_ARMOR_FEET:
                                target.getInventory().setBoots(item);
                                sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                break;
                            case SLOT_HOTBAR:
                                PlayerInventory playerInventory = target.getInventory();
                                int size = playerInventory.getSize();

                                if (slotId >= playerInventory.getHotbarSize() || slotId < 0) {
                                    sender.sendMessage(String.format(TextFormat.RED + "Could not replace slot %1$s, must be a value between %2$d and %3$d.", slotType, 0, size));
                                    return false;
                                } else {
                                    playerInventory.setItem(slotId, item);
                                    sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                }

                                break;
                            case SLOT_INVENTORY:
                                size = InventoryType.CHEST.getDefaultSize();

                                if (slotId >= size || slotId < 0) {
                                    sender.sendMessage(String.format(TextFormat.RED + "Could not replace slot %1$s, must be a value between %2$d and %3$d.", slotType, 0, size));
                                    return false;
                                } else {
                                    target.getInventory().setItem(8 + slotId, item);
                                    sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                }

                                break;
                            case SLOT_ENDERCHEST:
                                PlayerEnderChestInventory enderChestInventory = target.getEnderChestInventory();
                                size = enderChestInventory.getSize();

                                if (slotId >= enderChestInventory.getSize() || slotId < 0) {
                                    sender.sendMessage(String.format(TextFormat.RED + "Could not replace slot %1$s, must be a value between %2$d and %3$d.", slotType, 0, size));
                                    return false;
                                } else {
                                    enderChestInventory.setItem(slotId, item);
                                    sender.sendMessage(String.format("Replaced %1$s slot %2$d of %3$s with %4$d * %5$s", slotType, slotId, target.getName(), amount, item.getName()));
                                }

                                break;
                            case SLOT_SADDLE:
                            case SLOT_ARMOR:
                            case SLOT_CHEST:
                                sender.sendMessage(String.format(TextFormat.RED + "Could not replace %1$s slot %2$d with %3$d * %4$s", slotType, slotId, amount, item.getName()));
                                return false;
                        }
                    }

                    break;
                default:
                    throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }
        return true;
    }

    private static BlockEquipmentSlot parseBlockEquipmentSlot(String arg) throws CommandSyntaxException {
        switch (arg.toLowerCase()) {
            case "slot.container":
                return BlockEquipmentSlot.SLOT_CONTAINER;
        }
        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
    }

    private static EntityEquipmentSlot parseEntityEquipmentSlot(String arg) throws CommandSyntaxException {
        switch (arg.toLowerCase()) {
            case "slot.weapon.mainhand":
                return EntityEquipmentSlot.SLOT_WEAPON_MAINHAND;
            case "slot.weapon.offhand":
                return EntityEquipmentSlot.SLOT_WEAPON_OFFHAND;
            case "slot.armor.head":
                return EntityEquipmentSlot.SLOT_ARMOR_HEAD;
            case "slot.armor.chest":
                return EntityEquipmentSlot.SLOT_ARMOR_CHEST;
            case "slot.armor.legs":
                return EntityEquipmentSlot.SLOT_ARMOR_LEGS;
            case "slot.armor.feet":
                return EntityEquipmentSlot.SLOT_ARMOR_FEET;
            case "slot.hotbar":
                return EntityEquipmentSlot.SLOT_HOTBAR;
            case "slot.inventory":
                return EntityEquipmentSlot.SLOT_INVENTORY;
            case "slot.enderchest":
                return EntityEquipmentSlot.SLOT_ENDERCHEST;
            case "slot.saddle":
                return EntityEquipmentSlot.SLOT_SADDLE;
            case "slot.armor":
                return EntityEquipmentSlot.SLOT_ARMOR;
            case "slot.chest":
                return EntityEquipmentSlot.SLOT_CHEST;
        }
        throw CommandExceptions.COMMAND_SYNTAX_EXCEPTION;
    }

    enum BlockEquipmentSlot {
        SLOT_CONTAINER,
    }

    enum EntityEquipmentSlot {
        SLOT_WEAPON_MAINHAND,
        SLOT_WEAPON_OFFHAND,
        SLOT_ARMOR_HEAD,
        SLOT_ARMOR_CHEST,
        SLOT_ARMOR_LEGS,
        SLOT_ARMOR_FEET,
        SLOT_HOTBAR,
        SLOT_INVENTORY,
        SLOT_ENDERCHEST,
        SLOT_SADDLE,
        SLOT_ARMOR,
        SLOT_CHEST,
    }
}
