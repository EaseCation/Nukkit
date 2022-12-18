package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreRedstoneGlowingDeepslate extends BlockOreRedstoneGlowing {
    public BlockOreRedstoneGlowingDeepslate() {
    }

    @Override
    public int getId() {
        return LIT_DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    public String getName() {
        return "Glowing Deepslate Redstone Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }

    @Override
    protected int getUnlitBlockId() {
        return DEEPSLATE_REDSTONE_ORE;
    }
}
