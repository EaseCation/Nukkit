package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodOxidizedWaxed extends BlockLightningRodWaxed {
    public BlockLightningRodOxidizedWaxed() {
        this(0);
    }

    public BlockLightningRodOxidizedWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Lightning Rod";
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
        return WAXED_WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_LIGHTNING_ROD;
    }
}
