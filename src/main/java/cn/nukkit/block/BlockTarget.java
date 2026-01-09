package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTarget extends BlockSolid {
    BlockTarget() {

    }

    @Override
    public int getId() {
        return TARGET;
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }
}
