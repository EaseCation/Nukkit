package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBarrel extends BlockEntityAbstractContainer {

    protected BarrelInventory inventory;

    public BlockEntityBarrel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new BarrelInventory(this);

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BARREL;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, BARREL);

        if (this.hasName()) {
            nbt.put("CustomName", this.namedTag.get("CustomName"));
        }

        return nbt;
    }

    @Override
    public BarrelInventory getInventory() {
        return this.inventory;
    }

    @Override
    public int getSize() {
        return 27;
    }
}
