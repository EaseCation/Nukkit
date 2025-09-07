package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DispenserInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDispenser extends BlockEntityAbstractContainer {

    protected DispenserInventory inventory;

    public BlockEntityDispenser(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.DISPENSER;
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new DispenserInventory(this);

        super.initBlockEntity();
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public DispenserInventory getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, DISPENSER);

        if (this.hasName()) {
            nbt.putString("CustomName", this.getName());
        }

        return nbt;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.DISPENSER;
    }
}
