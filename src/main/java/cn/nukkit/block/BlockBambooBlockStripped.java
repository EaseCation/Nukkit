package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooBlockStripped extends BlockLogStripped {
    BlockBambooBlockStripped() {

    }

    @Override
    public int getId() {
        return STRIPPED_BAMBOO_BLOCK;
    }

    @Override
    public String getName() {
        return "Block of Stripped Bamboo";
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
    public boolean isLog() {
        return false;
    }
}
