package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodWeathered extends BlockLightningRod {
    public BlockLightningRodWeathered() {
        this(0);
    }

    public BlockLightningRodWeathered(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Weathered Lightning Rod";
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
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_LIGHTNING_ROD;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_LIGHTNING_ROD;
    }
}
