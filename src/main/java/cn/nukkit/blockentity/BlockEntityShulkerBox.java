package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class BlockEntityShulkerBox extends BlockEntityAbstractContainer {

    protected ShulkerBoxInventory inventory;

    public BlockEntityShulkerBox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new ShulkerBoxInventory(this);

        if (!this.namedTag.contains("facing")) {
            this.namedTag.putByte("facing", 0);
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.SHULKER_BOX || blockId == Block.UNDYED_SHULKER_BOX;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public ShulkerBoxInventory getInventory() {
        return this.inventory;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = getDefaultCompound(this, SHULKER_BOX)
                .putByte("facing", this.namedTag.getByte("facing"));

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    public void onBreak() {
        this.inventory.clearAll();
    }
}
