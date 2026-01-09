package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMudPacked extends BlockSolid {
    BlockMudPacked() {

    }

    @Override
    public int getId() {
        return PACKED_MUD;
    }

    @Override
    public String getName() {
        return "Packed Mud";
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
