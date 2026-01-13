package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardOrange extends BlockGlassStainedHard {
    protected BlockGlassStainedHardOrange() {

    }

    @Override
    public int getId() {
        return HARD_ORANGE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Orange Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
