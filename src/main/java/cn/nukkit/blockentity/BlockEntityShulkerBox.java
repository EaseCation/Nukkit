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
    public int getTypeId() {
        return BlockEntityType.SHULKER_BOX;
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
        return blockId == Block.UNDYED_SHULKER_BOX
                || blockId == Block.WHITE_SHULKER_BOX
                || blockId == Block.ORANGE_SHULKER_BOX
                || blockId == Block.MAGENTA_SHULKER_BOX
                || blockId == Block.LIGHT_BLUE_SHULKER_BOX
                || blockId == Block.YELLOW_SHULKER_BOX
                || blockId == Block.LIME_SHULKER_BOX
                || blockId == Block.PINK_SHULKER_BOX
                || blockId == Block.GRAY_SHULKER_BOX
                || blockId == Block.LIGHT_GRAY_SHULKER_BOX
                || blockId == Block.CYAN_SHULKER_BOX
                || blockId == Block.PURPLE_SHULKER_BOX
                || blockId == Block.BLUE_SHULKER_BOX
                || blockId == Block.BROWN_SHULKER_BOX
                || blockId == Block.GREEN_SHULKER_BOX
                || blockId == Block.RED_SHULKER_BOX
                || blockId == Block.BLACK_SHULKER_BOX;
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
            c.putString("CustomName", this.getName());
        }

        return c;
    }

    @Override
    public void onBreak() {
        unpackLootTable();

        this.inventory.clearAll();
    }
}
