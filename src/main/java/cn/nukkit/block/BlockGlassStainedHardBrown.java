package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardBrown extends BlockGlassStainedHard {
    BlockGlassStainedHardBrown() {

    }

    @Override
    public int getId() {
        return HARD_BROWN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Brown Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
