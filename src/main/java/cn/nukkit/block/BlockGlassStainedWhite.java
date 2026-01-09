package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedWhite extends BlockGlassStained {
    BlockGlassStainedWhite() {

    }

    @Override
    public int getId() {
        return WHITE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "White Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
