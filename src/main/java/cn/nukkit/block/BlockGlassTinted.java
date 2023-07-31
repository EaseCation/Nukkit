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
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
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
