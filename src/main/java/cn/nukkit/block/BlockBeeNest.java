package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBeeNest extends BlockBeehive {

    BlockBeeNest() {

    }

    @Override
    public int getId() {
        return BEE_NEST;
    }

    @Override
    public String getName() {
        return "Bee Nest";
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
