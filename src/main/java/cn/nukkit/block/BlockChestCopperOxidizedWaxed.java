package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockChestCopperOxidizedWaxed extends BlockChestCopperWaxed {
    public BlockChestCopperOxidizedWaxed() {
        this(0);
    }

    public BlockChestCopperOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_CHEST;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Chest";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_CHEST;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_COPPER_CHEST;
    }
}
