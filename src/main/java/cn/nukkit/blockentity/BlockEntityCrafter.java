package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCrafter;
import cn.nukkit.inventory.CrafterInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCrafter extends BlockEntityAbstractContainer {
    private CrafterInventory inventory;
    private int disabledSlots;

    private int craftingTicksRemaining;

    public BlockEntityCrafter(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        inventory = new CrafterInventory(this);

        disabledSlots = namedTag.getShort("disabled_slots");

        super.initBlockEntity();
        setDisabledSlots(disabledSlots);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.CRAFTER;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putShort("disabled_slots", disabledSlots);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CRAFTER;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = chunkData ? getDefaultCompound(this, CRAFTER) : new CompoundTag();

        nbt.putShort("disabled_slots", disabledSlots);

        if (hasName()) {
            nbt.putString("CustomName", getName());
        }

        nbt.putInt("crafting_ticks_remaining", craftingTicksRemaining);

        return nbt;
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public CrafterInventory getInventory() {
        return inventory;
    }

    public boolean isSlotDisabled(int slot) {
        if (slot < 0 || slot >= getSize()) {
            return false;
        }
        return (disabledSlots & 1 << slot) != 0;
    }

    public boolean setSlotDisabled(int slot, boolean disabled) {
        if (slot < 0 || slot >= getSize()) {
            return false;
        }
        Item item = inventory.getItem(slot);
        if (!item.isNull()) {
            return false;
        }
        if (disabled) {
            disabledSlots |= 1 << slot;
        } else {
            disabledSlots &= ~(1 << slot);
        }
        setDirty();
        return true;
    }

    public int getDisabledSlots() {
        return disabledSlots;
    }

    public void setDisabledSlots(int slotFlags) {
        disabledSlots = slotFlags & 0x1ff;
        for (int slot = 0; slot < getSize(); slot++) {
            if (!inventory.getItem(slot).isNull()) {
                disabledSlots &= ~(1 << slot);
            }
        }
    }

    public int getCraftingTicksRemaining() {
        return craftingTicksRemaining;
    }

    public void setCraftingTicksRemaining(int ticks) {
        craftingTicksRemaining = Math.max(0, ticks);
        lastUpdate = server.getTick();
        if (craftingTicksRemaining > 0) {
            scheduleUpdate();
        }
        spawnToAll();
    }

    @Override
    public boolean onUpdate() {
        int tick = server.getTick();
        int elapsed = tick - lastUpdate;
        if (elapsed <= 0) {
            return craftingTicksRemaining > 0;
        }
        lastUpdate = tick;
        craftingTicksRemaining = Math.max(0, craftingTicksRemaining - elapsed);
        if (craftingTicksRemaining == 0) {
            Block block = getBlock();
            if (block instanceof BlockCrafter crafter && crafter.isCrafting()) {
                crafter.setCrafting(false);
                level.setBlock(crafter, crafter, true, false);
                spawnToAll();
            }
        }
        return craftingTicksRemaining > 0;
    }
}
