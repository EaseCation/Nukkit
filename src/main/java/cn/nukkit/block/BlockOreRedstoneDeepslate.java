package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockOreRedstoneDeepslate extends BlockOreRedstone {
    public BlockOreRedstoneDeepslate() {
    }

    @Override
    public int getId() {
        return DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }

    @Override
    protected int getLitBlockId() {
        return LIT_DEEPSLATE_REDSTONE_ORE;
    }
}
