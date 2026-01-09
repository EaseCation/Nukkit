package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardLightBlue extends BlockGlassStainedHard {
    BlockGlassStainedHardLightBlue() {

    }

    @Override
    public int getId() {
        return HARD_LIGHT_BLUE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Light Blue Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
