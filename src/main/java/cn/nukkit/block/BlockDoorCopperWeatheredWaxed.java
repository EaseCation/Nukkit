package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoorCopperWeatheredWaxed extends BlockDoorCopperWaxed {
    public BlockDoorCopperWeatheredWaxed() {
        this(0);
    }

    public BlockDoorCopperWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_DOOR;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Door";
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
        return WAXED_OXIDIZED_COPPER_DOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_DOOR;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_DOOR;
    }
}
