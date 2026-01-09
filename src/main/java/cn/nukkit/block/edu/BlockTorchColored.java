package cn.nukkit.block.edu;

import cn.nukkit.block.BlockTorch;

public abstract class BlockTorchColored extends BlockTorch {
    public static final int[] COLORED_TORCHS_RG = {
            COLORED_TORCH_RED,
            COLORED_TORCH_GREEN,
    };
    public static final int[] COLORED_TORCHS_BP = {
            COLORED_TORCH_BLUE,
            COLORED_TORCH_PURPLE,
    };

    @Deprecated
    public static final int COLOR_BIT = 0b1000;
    private static final int COLOR_OFFSET = 3;

    public static final int RED = 0;
    public static final int GREEN = 1 << COLOR_OFFSET;

    public static final int BLUE = 0;
    public static final int PURPLE = 1 << COLOR_OFFSET;

    BlockTorchColored() {

    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
