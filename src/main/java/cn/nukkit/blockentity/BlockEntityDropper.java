package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDropper extends BlockEntityAbstractContainer {

    protected DropperInventory inventory;

    public BlockEntityDropper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new DropperInventory(this);

        super.initBlockEntity();
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.DROPPER;
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public DropperInventory getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = getDefaultCompound(this, DROPPER);

        if (this.hasName()) {
            nbt.putString("CustomName", this.getName());
        }

        return nbt;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.DROPPER;
    }
}
