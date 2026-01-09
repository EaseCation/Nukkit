package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabResinBrick extends BlockSlabStone {
    BlockSlabResinBrick() {

    }

    @Override
    public int getId() {
        return RESIN_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Resin Brick Slab" : "Resin Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return RESIN_BRICK_DOUBLE_SLAB;
    }
}
