package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHoney extends BlockTransparent {

    public BlockHoney() {
    }

    @Override
    public String getName() {
        return "Honey Block";
    }

    @Override
    public int getId() {
        return HONEY_BLOCK;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public float getFrictionFactor() {
        return 0.8f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
