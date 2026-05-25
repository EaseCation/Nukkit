package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.blockentity.*;
import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.loot.Lootable;
import cn.nukkit.math.BlockFace;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

public class DropperBehavior extends DefaultDispenseBehavior {
    private static final Item AIR = Items.air();

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        return drop(block, face, item).left();
    }

    @Override
    public ObjectBooleanPair<Item> drop(BlockDispenser block, BlockFace face, Item item) {
        Boolean interactionResult = pushItem(block, face, item);
        if (interactionResult != null) {
            if (!interactionResult) {
                return ObjectBooleanPair.of(AIR, false);
            }
            return ObjectBooleanPair.of(null, false);
        }

        return ObjectBooleanPair.of(super.dispense(block, face, item), true);
    }

    static Boolean pushItem(BlockDispenser block, BlockFace facing, Item item) {
        Block side = block.level.getBlock(block.getSideVec(facing));

        if (side.is(Block.COMPOSTER)) {
            if (facing != BlockFace.DOWN || side.getDamage() >= 0x7 || item.getCompostableChance() <= 0) {
                return false;
            }

            side.onActivate(item, facing.getOpposite(), null);

            item.pop();
            return true;
        }

        if (side.getBlockEntityType() == 0) {
            return null;
        }
        BlockEntity blockEntity = block.level.getBlockEntity(side);
        if (blockEntity == null) {
            return null;
        }

        if (blockEntity instanceof Lootable lootable) {
            lootable.unpackLootTable();
        }

        if (blockEntity instanceof HopperInteractable container) {
            return container.push(item);
        }

        if (!(blockEntity instanceof InventoryHolder holder)) {
            return null;
        }

        if (blockEntity instanceof BlockEntityFurnace furnace) {
            FurnaceInventory inventory = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            if (facing == BlockFace.DOWN) {
                Item smelting = inventory.getSmelting();
                if (smelting.isNull()) {
                    inventory.setSmelting(item.split(1));
                    return true;
                }

                if (smelting.is(item.getId(), item.getDamage()) && smelting.getCount() < smelting.getMaxStackSize()) {
                    smelting.grow(1);
                    inventory.setSmelting(smelting);
                    item.pop();
                    return true;
                }

                return false;
            }

            if (item.getFuelTime() > 0) {
                Item fuel = inventory.getFuel();
                if (fuel.isNull()) {
                    inventory.setFuel(item.split(1));
                    return true;
                }

                if (fuel.is(item.getId(), item.getDamage()) && fuel.getCount() < fuel.getMaxStackSize()) {
                    fuel.grow(1);
                    inventory.setFuel(fuel);
                    item.pop();
                    return true;
                }

                return false;
            }

            return false;
        }

        if (blockEntity instanceof BlockEntityBrewingStand brewing) {
            BrewingInventory inventory = brewing.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            if (facing == BlockFace.DOWN) {
                if (!item.isBrewingIngredient()) {
                    return false;
                }

                Item ingredient = inventory.getIngredient();
                if (ingredient.isNull()) {
                    inventory.setIngredient(item.split(1));
                    return true;
                }

                if (ingredient.is(item.getId(), item.getDamage()) && ingredient.getCount() < ingredient.getMaxStackSize()) {
                    ingredient.grow(1);
                    inventory.setIngredient(ingredient);
                    item.pop();
                    return true;
                }

                return false;
            }

            if (item.is(Item.BLAZE_POWDER)) {
                Item fuel = inventory.getFuel();
                if (fuel.isNull()) {
                    inventory.setFuel(item.split(1));
                    return true;
                }

                if (fuel.is(item.getId(), item.getDamage()) && fuel.getCount() < fuel.getMaxStackSize()) {
                    fuel.grow(1);
                    inventory.setFuel(fuel);
                    item.pop();
                    return true;
                }

                return false;
            }

            if (item.isPotion() || item.is(Item.GLASS_BOTTLE)) {
                for (int slot = 1; slot <= 3; slot++) {
                    Item potion = inventory.getItem(slot);
                    if (potion.isNull()) {
                        inventory.setItem(slot, item.split(1));
                        return true;
                    }
                }
                return false;
            }

            return false;
        }

        Inventory inventory = holder.getInventory();
        if (inventory.isFull()) {
            return false;
        }

        Item itemToAdd = item.clone(1);
        if (!inventory.canAddItem(itemToAdd)) {
            return false;
        }

        if (inventory.addItem(itemToAdd).length != 0) {
            return false;
        }

        item.pop();
        return true;
    }
}
