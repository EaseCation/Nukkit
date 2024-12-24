package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockHugeMushroom extends BlockSolid {
    public static final int ALL_INSIDE = 0;
    public static final int NORTH_WEST = 1;
    public static final int NORTH = 2;
    public static final int NORTH_EAST = 3;
    public static final int WEST = 4;
    public static final int CENTER = 5;
    public static final int EAST = 6;
    public static final int SOUTH_WEST = 7;
    public static final int SOUTH = 8;
    public static final int SOUTH_EAST = 9;
    public static final int STEM = 10;
    public static final int ALL_OUTSIDE = 14;
    public static final int ALL_STEM = 15;

    protected BlockHugeMushroom(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        int count = ThreadLocalRandom.current().nextInt(-7, 3);
        if (count <= 0) {
            return new Item[0];
        }

        return new Item[]{
                Item.get(getSmallMushroomId()),
        };
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), ALL_OUTSIDE);
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected abstract int getSmallMushroomId();
}
