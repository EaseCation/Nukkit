package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardMagenta extends BlockGlassStainedHard {
    BlockGlassStainedHardMagenta() {

    }

    @Override
    public int getId() {
        return HARD_MAGENTA_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Magenta Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
