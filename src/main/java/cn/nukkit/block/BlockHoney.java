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
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getFrictionFactor() {
        return 0.8;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
