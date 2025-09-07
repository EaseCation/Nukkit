package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.CrafterInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCrafter extends BlockEntityAbstractContainer {
    private CrafterInventory inventory;
    private int disabledSlots;

    public BlockEntityCrafter(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        inventory = new CrafterInventory(this);

        disabledSlots = namedTag.getShort("disabled_slots");

        super.initBlockEntity();
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
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, CRAFTER);

        if (hasName()) {
            nbt.putString("CustomName", getName());
        }

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
}
