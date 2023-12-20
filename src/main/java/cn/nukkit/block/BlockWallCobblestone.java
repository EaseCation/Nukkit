package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockWallCobblestone extends BlockWall {
    public static final int TYPE_COBBLESTONE = 0;
    public static final int TYPE_MOSSY_COBBLESTONE = 1;
    public static final int TYPE_GRANITE = 2;
    public static final int TYPE_DIORITE = 3;
    public static final int TYPE_ANDESITE = 4;
    public static final int TYPE_SANDSTONE = 5;
    public static final int TYPE_BRICK = 6;
    public static final int TYPE_STONE_BRICK = 7;
    public static final int TYPE_MOSSY_STONE_BRICK = 8;
    public static final int TYPE_NETHER_BRICK = 9;
    public static final int TYPE_END_BRICK = 10;
    public static final int TYPE_PRISMARINE = 11;
    public static final int TYPE_RED_SANDSTONE = 12;
    public static final int TYPE_RED_NETHER_BRICK = 13;

    public static final int TYPE_MASK = 0b1111;
    public static final int POST_BIT = 0b1_0000;
    public static final int NORTH_CONNECTION_TYPE_MASK = 0b11_0_0000;
    public static final int NORTH_CONNECTION_TYPE_OFFSET = 5;
    public static final int EAST_CONNECTION_TYPE_MASK = 0b11_00_0_0000;
    public static final int EAST_CONNECTION_TYPE_OFFSET = 7;
    public static final int SOUTH_CONNECTION_TYPE_MASK = 0b11_00_00_0_0000;
    public static final int SOUTH_CONNECTION_TYPE_OFFSET = 9;
    public static final int WEST_CONNECTION_TYPE_MASK = 0b11_00_00_00_0_0000;
    public static final int WEST_CONNECTION_TYPE_OFFSET = 11;

    private static final String[] NAMES = new String[]{
            "Cobblestone Wall",
            "Mossy Cobblestone Wall",
            "Granite Wall",
            "Diorite Wall",
            "Andesite Wall",
            "Sandstone Wall",
            "Brick Wall",
            "Stone Brick Wall",
            "Mossy Stone Brick Wall",
            "Nether Brick Wall",
            "End Stone Brick Wall",
            "Prismarine Wall",
            "Red Sandstone Wall",
            "Red Nether Brick Wall",
            "Wall",
            "Wall",
    };

    public BlockWallCobblestone() {
        this(0);
    }

    public BlockWallCobblestone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLESTONE_WALL;
    }

    @Override
    public String getName() {
        return NAMES[getWallType()];
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getWallType());
    }

    @Override
    public BlockColor getColor() {
        switch (getWallType()) {
            default:
            case TYPE_COBBLESTONE:
            case TYPE_MOSSY_COBBLESTONE:
            case TYPE_ANDESITE:
            case TYPE_STONE_BRICK:
            case TYPE_MOSSY_STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case TYPE_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case TYPE_SANDSTONE:
            case TYPE_END_BRICK:
                return BlockColor.SAND_BLOCK_COLOR;
            case TYPE_BRICK:
                return BlockColor.RED_BLOCK_COLOR;
            case TYPE_NETHER_BRICK:
            case TYPE_RED_NETHER_BRICK:
                return BlockColor.NETHER_BLOCK_COLOR;
            case TYPE_PRISMARINE:
                return BlockColor.CYAN_BLOCK_COLOR;
            case TYPE_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
        }
    }

    @Override
    public boolean isPost() {
        return (getDamage() & POST_BIT) == POST_BIT;
    }

    @Override
    public void setPost(boolean post) {
        setDamage(post ? getDamage() | POST_BIT : getDamage() & ~POST_BIT);
    }

    @Override
    public int getNorthConnectionType() {
        return getConnectionType(NORTH_CONNECTION_TYPE_OFFSET);
    }

    @Override
    public void setNorthConnectionType(int type) {
        setConnectionType(NORTH_CONNECTION_TYPE_OFFSET, type);
    }

    @Override
    public int getEastConnectionType() {
        return getConnectionType(EAST_CONNECTION_TYPE_OFFSET);
    }

    @Override
    public void setEastConnectionType(int type) {
        setConnectionType(EAST_CONNECTION_TYPE_OFFSET, type);
    }

    @Override
    public int getSouthConnectionType() {
        return getConnectionType(SOUTH_CONNECTION_TYPE_OFFSET);
    }

    @Override
    public void setSouthConnectionType(int type) {
        setConnectionType(SOUTH_CONNECTION_TYPE_OFFSET, type);
    }

    @Override
    public int getWestConnectionType() {
        return getConnectionType(WEST_CONNECTION_TYPE_OFFSET);
    }

    @Override
    public void setWestConnectionType(int type) {
        setConnectionType(WEST_CONNECTION_TYPE_OFFSET, type);
    }

    public int getWallType() {
        return getDamage() & TYPE_MASK;
    }
}
