package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHopper;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.Lootable;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class BlockEntityHopper extends BlockEntityAbstractContainer {

    protected HopperInventory inventory;

    public int transferCooldown;

    private AxisAlignedBB pickupArea;

    public BlockEntityHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.HOPPER;
    }

    @Override
    protected void initBlockEntity() {
        if (this.namedTag.contains("TransferCooldown")) {
            this.transferCooldown = this.namedTag.getInt("TransferCooldown");
        } else {
            this.transferCooldown = 8;
        }

        this.inventory = new HopperInventory(this);

        this.pickupArea = new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);

        this.scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.HOPPER;
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onUpdate() {
        if (this.isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        this.transferCooldown--;

        if (this.level.isBlockPowered(getBlock())) {
            return true;
        }

        if (!this.isOnTransferCooldown()) {
            if ((this.level.getBlock(this).getDamage() & BlockHopper.TOGGLE_BIT) == BlockHopper.TOGGLE_BIT) { //is hopper disabled?
                return false;
            }

            boolean changed = pushItems();

            if (pullItems()) {
                changed = true;
            } else {
                changed |= pickupItems();
            }

            if (changed) {
                this.setTransferCooldown(8);
                setDirty();
            }
        }

        return true;
    }

    public boolean pullItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        Block block = this.level.getBlock(this.upVec());

        if (block.is(Block.COMPOSTER)) {
            if (block.getDamage() <= 0x7) {
                return false;
            }

            Item item = Item.get(Item.BONE_MEAL);
            if (!this.inventory.canAddItem(item) || this.inventory.addItem(item).length != 0) {
                return false;
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_BLOCK_COMPOSTER_EMPTY);

            block.setDamage(0);
            level.setBlock(block, block, true);
            return true;
        }

        if (block.getBlockEntityType() == 0) {
            return false;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(block);
        if (blockEntity == null) {
            return false;
        }

        if (blockEntity instanceof Lootable lootable) {
            lootable.unpackLootTable();
        }

        if (blockEntity instanceof HopperInteractable container) {
            return container.pull(this);
        }

        if (!(blockEntity instanceof InventoryHolder holder)) {
            return false;
        }

        if (blockEntity instanceof BlockEntityFurnace furnace) {
            FurnaceInventory inv = furnace.getInventory();

            Item fuel = inv.getFuel();
            if (fuel.is(Item.BUCKET)) {
                Item itemToAdd = fuel.clone(1);
                if (this.inventory.canAddItem(itemToAdd)) {
                    InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (this.inventory.addItem(itemToAdd).length == 0) {
                            fuel.pop();
                            inv.setFuel(fuel);
                            return true;
                        }
                    }
                }
            }

            Item item = inv.getResult();
            if (!item.isNull()) {
                Item itemToAdd = item.clone(1);
                if (!this.inventory.canAddItem(itemToAdd)) {
                    return false;
                }

                InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                Item[] items = this.inventory.addItem(itemToAdd);

                if (items.length == 0) {
                    item.count--;
                    inv.setResult(item);
                    return true;
                }
            }
            return false;
        }
        if (blockEntity instanceof BlockEntityBrewingStand brewing) {
            BrewingInventory inv = brewing.getInventory();
            for (int i = 1; i <= 3; i++) {
                Item item = inv.getItem(i);
                if (item.isNull()) {
                    continue;
                }

                Item itemToAdd = item.clone(1);
                if (!this.inventory.canAddItem(itemToAdd)) {
                    continue;
                }

                InventoryMoveItemEvent event = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                this.server.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    continue;
                }

                if (this.inventory.addItem(itemToAdd).length != 0) {
                    continue;
                }

                item.pop();
                inv.setItem(i, item);
                return true;
            }
            return false;
        }

        Inventory inv = holder.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            Item item = inv.getItem(i);
            if (item.isNull()) {
                continue;
            }

            Item itemToAdd = item.clone(1);
            if (!this.inventory.canAddItem(itemToAdd)) {
                continue;
            }

            InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, this.inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                continue;
            }

            Item[] items = this.inventory.addItem(itemToAdd);
            if (items.length >= 1) {
                continue;
            }

            item.count--;
            inv.setItem(i, item);
            return true;
        }
        return false;
    }

    public boolean pickupItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        boolean pickedUpItem = false;

        for (Entity entity : this.level.getCollidingEntities(this.pickupArea)) {
            if (entity.isClosed() || !(entity instanceof EntityItem)) {
                continue;
            }

            EntityItem itemEntity = (EntityItem) entity;
            Item item = itemEntity.getItem();

            if (item.isNull()) {
                continue;
            }

            int originalCount = item.getCount();

            if (!this.inventory.canAddItem(item)) {
                continue;
            }

            InventoryMoveItemEvent ev = new InventoryMoveItemEvent(null, this.inventory, this, item, InventoryMoveItemEvent.Action.PICKUP);
            this.server.getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                continue;
            }

            Item[] items = this.inventory.addItem(item);

            if (items.length == 0) {
                entity.close();
                pickedUpItem = true;
                continue;
            }

            if (items[0].getCount() != originalCount) {
                pickedUpItem = true;
                item.setCount(items[0].getCount());
            }
        }

        //TODO: check for minecart
        return pickedUpItem;
    }

    public boolean pushItems() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        Block hopper = getBlock();
        BlockFace facing = BlockFace.fromIndex(hopper.getDamage() & BlockHopper.FACING_DIRECTION_MASK);
        if (facing == BlockFace.UP) {
            return false;
        }

        Block block = this.level.getBlock(this.getSideVec(facing));

        if (block.is(Block.COMPOSTER)) {
            if (facing != BlockFace.DOWN || block.getDamage() >= 0x7) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (item.isNull()) {
                    continue;
                }

                if (item.getCompostableChance() <= 0) {
                    continue;
                }

                block.onActivate(item, facing.getOpposite(), null);

                item.pop();
                this.inventory.setItem(i, item);
                return true;
            }
            return false;
        }

        if (block.getBlockEntityType() == 0) {
            return false;
        }
        BlockEntity be = this.level.getBlockEntity(block);
        if (be == null) {
            return false;
        }

        if (be instanceof BlockEntityHopper && facing == BlockFace.DOWN) {
            return false;
        }

        if (be instanceof Lootable lootable) {
            lootable.unpackLootTable();
        }

        if (be instanceof HopperInteractable container) {
            return container.push(this);
        }

        if (!(be instanceof InventoryHolder holder)) {
            return false;
        }

        if (be instanceof BlockEntityFurnace furnace) {
            FurnaceInventory inventory = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (item.isNull()) {
                    continue;
                }

                boolean pushedItem = false;
                if (facing == BlockFace.DOWN) {
                    Item smelting = inventory.getSmelting();
                    if (smelting.isNull()) {
                        Item itemToAdd = item.clone(1);
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            inventory.setSmelting(itemToAdd);
                            item.count--;
                            pushedItem = true;
                        }
                    } else if (smelting.getId() == item.getId() && smelting.getDamage() == item.getDamage() && smelting.count < smelting.getMaxStackSize()) {
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, item.clone(1), InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            smelting.count++;
                            inventory.setSmelting(smelting);
                            item.count--;
                            pushedItem = true;
                        }
                    }
                } else if (item.getFuelTime() > 0) {
                    Item fuel = inventory.getFuel();
                    if (fuel.isNull()) {
                        Item itemToAdd = item.clone(1);
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            inventory.setFuel(itemToAdd);
                            item.count--;
                            pushedItem = true;
                        }
                    } else if (fuel.getId() == item.getId() && fuel.getDamage() == item.getDamage() && fuel.count < fuel.getMaxStackSize()) {
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, item.clone(1), InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            fuel.count++;
                            inventory.setFuel(fuel);
                            item.count--;
                            pushedItem = true;
                        }
                    }
                }

                if (pushedItem) {
                    this.inventory.setItem(i, item);
                    return true;
                }
            }
            return false;
        }
        if (be instanceof BlockEntityBrewingStand brewing) {
            BrewingInventory inventory = brewing.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (item.isNull()) {
                    continue;
                }

                boolean pushedItem = false;
                if (facing == BlockFace.DOWN) {
                    if (!item.isBrewingIngredient()) {
                        continue;
                    }

                    Item ingredient = inventory.getIngredient();
                    if (ingredient.isNull()) {
                        Item itemToAdd = item.clone(1);
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            inventory.setIngredient(itemToAdd);
                            item.pop();
                            pushedItem = true;
                        }
                    } else if (ingredient.getId() == item.getId() && ingredient.getDamage() == item.getDamage() && ingredient.count < ingredient.getMaxStackSize()) {
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, item.clone(1), InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            ingredient.grow(1);
                            inventory.setIngredient(ingredient);
                            item.pop();
                            pushedItem = true;
                        }
                    }
                } else if (item.is(Item.BLAZE_POWDER)) {
                    Item fuel = inventory.getFuel();
                    if (fuel.isNull()) {
                        Item itemToAdd = item.clone(1);
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            inventory.setFuel(itemToAdd);
                            item.pop();
                            pushedItem = true;
                        }
                    } else if (fuel.getId() == item.getId() && fuel.getDamage() == item.getDamage() && fuel.count < fuel.getMaxStackSize()) {
                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, item.clone(1), InventoryMoveItemEvent.Action.SLOT_CHANGE);
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            fuel.grow(1);
                            inventory.setFuel(fuel);
                            item.pop();
                            pushedItem = true;
                        }
                    }
                } else if (item.isPotion() || item.is(Item.GLASS_BOTTLE)) {
                    for (int slot = 1; slot <= 3; slot++) {
                        Item potion = inventory.getItem(slot);
                        if (potion.isNull()) {
                            Item itemToAdd = item.clone(1);
                            InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                            this.server.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                inventory.setItem(slot, itemToAdd);
                                item.pop();
                                pushedItem = true;
                                break;
                            }
                        }
                    }
                }

                if (pushedItem) {
                    this.inventory.setItem(i, item);
                    return true;
                }
            }
            return false;
        }

        Inventory inventory = holder.getInventory();
        if (inventory.isFull()) {
            return false;
        }

        for (int i = 0; i < this.inventory.getSize(); i++) {
            Item item = this.inventory.getItem(i);
            if (item.isNull()) {
                continue;
            }

            Item itemToAdd = item.clone(1);
            if (!inventory.canAddItem(itemToAdd)) {
                continue;
            }

            InventoryMoveItemEvent ev = new InventoryMoveItemEvent(this.inventory, inventory, this, itemToAdd, InventoryMoveItemEvent.Action.SLOT_CHANGE);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                continue;
            }

            Item[] items = inventory.addItem(itemToAdd);
            if (items.length > 0) {
                continue;
            }

            item.count--;
            this.inventory.setItem(i, item);
            return true;
        }
        return false;

        //TODO: check for minecart
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = getDefaultCompound(this, BlockEntity.HOPPER);

        if (this.hasName()) {
            nbt.putString("CustomName", this.getName());
        }

        return nbt;
    }
}
