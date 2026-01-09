package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedLime extends BlockGlassStained {
    BlockGlassStainedLime() {

    }

    @Override
    public int getId() {
        return LIME_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Lime Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
