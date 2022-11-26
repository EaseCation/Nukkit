package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 28.01.2016.
 */
public class BlockHugeMushroomRed extends BlockHugeMushroom {

    public BlockHugeMushroomRed() {
        this(0);
    }

    public BlockHugeMushroomRed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Red Mushroom Block";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM_BLOCK;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getSmallMushroomId() {
        return RED_MUSHROOM;
    }
}
