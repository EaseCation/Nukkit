package cn.nukkit.block;

/**
 * Created by Pub4Game on 03.01.2015.
 */
public class BlockMushroomRed extends BlockMushroom {

    BlockMushroomRed() {

    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM;
    }

    @Override
    protected int getType() {
        return 1;
    }
}
