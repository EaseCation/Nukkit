package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardBlack extends BlockGlassStainedHard {
    BlockGlassStainedHardBlack() {

    }

    @Override
    public int getId() {
        return HARD_BLACK_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Black Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
