package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDriedKelp extends BlockSolid {

    public BlockDriedKelp() {
    }

    @Override
    public String getName() {
        return "Dried Kelp Block";
    }

    @Override
    public int getId() {
        return DRIED_KELP_BLOCK;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
