package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBeeNest extends BlockBeehive {

    public BlockBeeNest() {
        this(0);
    }

    public BlockBeeNest(int meta) {
        super(meta);
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
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 13.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
