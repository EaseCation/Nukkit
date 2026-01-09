package cn.nukkit.block;

/**
 * @author Nukkit Project Team
 */
public class BlockMushroomBrown extends BlockMushroom {

    BlockMushroomBrown() {

    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    public int getId() {
        return BROWN_MUSHROOM;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getType() {
        return 0;
    }
}
