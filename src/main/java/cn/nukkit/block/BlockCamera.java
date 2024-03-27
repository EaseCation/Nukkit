package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCamera extends BlockSolid {

    public BlockCamera() {
    }

    @Override
    public int getId() {
        return BLOCK_CAMERA;
    }

    @Override
    public String getName() {
        return "Camera";
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
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
