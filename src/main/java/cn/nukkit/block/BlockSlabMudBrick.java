package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabMudBrick extends BlockSlabStone {
    BlockSlabMudBrick() {

    }

    @Override
    public int getId() {
        return MUD_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Mud Brick Slab" : "Mud Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return MUD_BRICK_DOUBLE_SLAB;
    }
}
