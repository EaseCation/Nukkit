package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGlassTinted extends BlockTransparent {
    public BlockGlassTinted() {
    }

    @Override
    public int getId() {
        return TINTED_GLASS;
    }

    @Override
    public String getName() {
        return "Tinted Glass";
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean isGlass() {
        return true;
    }
}
