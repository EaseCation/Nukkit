package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 28.01.2016.
 */
public class BlockHugeMushroomBrown extends BlockHugeMushroom {

    BlockHugeMushroomBrown() {

    }

    @Override
    public String getDescriptionId() {
        return "tile.brown_mushroom_block.cap.name";
    }

    @Override
    public String getName() {
        return "Brown Mushroom Block";
    }

    @Override
    public int getId() {
        return BROWN_MUSHROOM_BLOCK;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    protected int getSmallMushroomId() {
        return BROWN_MUSHROOM;
    }
}
