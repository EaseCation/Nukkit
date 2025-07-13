package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockChestCopperWeatheredWaxed extends BlockChestCopperWaxed {
    public BlockChestCopperWeatheredWaxed() {
        this(0);
    }

    public BlockChestCopperWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_CHEST;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Chest";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER_CHEST;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_CHEST;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_CHEST;
    }
}
