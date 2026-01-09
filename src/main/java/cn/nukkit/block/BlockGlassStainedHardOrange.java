package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardOrange extends BlockGlassStainedHard {
    BlockGlassStainedHardOrange() {

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
