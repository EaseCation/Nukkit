package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperGolemStatueExposedWaxed extends BlockCopperGolemStatueWaxed {
    public BlockCopperGolemStatueExposedWaxed() {
        this(0);
    }

    public BlockCopperGolemStatueExposedWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Golem Statue";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_COPPER_GOLEM_STATUE;
    }
}
