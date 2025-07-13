package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCopperOxidized extends BlockTrapdoorCopper {
    public BlockTrapdoorCopperOxidized() {
        this(0);
    }

    public BlockTrapdoorCopperOxidized(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Trapdoor";
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
        return WAXED_OXIDIZED_COPPER_TRAPDOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_TRAPDOOR;
    }
}
