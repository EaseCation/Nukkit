package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoorCopperOxidized extends BlockDoorCopper {
    public BlockDoorCopperOxidized() {
        this(0);
    }

    public BlockDoorCopperOxidized(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_DOOR;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Door";
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
        return WAXED_OXIDIZED_COPPER_DOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_DOOR;
    }
}
