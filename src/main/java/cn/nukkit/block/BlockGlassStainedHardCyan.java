package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardCyan extends BlockGlassStainedHard {
    BlockGlassStainedHardCyan() {

    }

    @Override
    public int getId() {
        return HARD_CYAN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Cyan Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
