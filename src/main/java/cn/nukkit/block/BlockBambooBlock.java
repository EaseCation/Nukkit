package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooBlock extends BlockLog {
    BlockBambooBlock() {

    }

    @Override
    public int getId() {
        return BAMBOO_BLOCK;
    }

    @Override
    public String getName() {
        return "Block of Bamboo";
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_BAMBOO_BLOCK, getDamage());
    }

    @Override
    public boolean isLog() {
        return false;
    }
}
