package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodOxidized extends BlockLightningRod {
    public BlockLightningRodOxidized() {
        this(0);
    }

    public BlockLightningRodOxidized(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return OXIDIZED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Oxidized Lightning Rod";
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
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_LIGHTNING_ROD;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_LIGHTNING_ROD;
    }
}
