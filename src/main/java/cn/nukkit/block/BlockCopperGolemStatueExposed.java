package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperGolemStatueExposed extends BlockCopperGolemStatue {
    public BlockCopperGolemStatueExposed() {
        this(0);
    }

    public BlockCopperGolemStatueExposed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Exposed Copper Golem Statue";
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
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_GOLEM_STATUE;
    }
}
