package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCopperExposed extends BlockTrapdoorCopper {
    public BlockTrapdoorCopperExposed() {
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Exposed Copper Trapdoor";
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
        return WAXED_EXPOSED_COPPER_TRAPDOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_TRAPDOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_TRAPDOOR;
    }
}
