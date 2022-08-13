package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabStone4 extends BlockDoubleSlab {

    public static final int MOSSY_STONE_BRICK = 0;
    public static final int SMOOTH_QUARTZ = 1;
    public static final int STONE = 2;
    public static final int CUT_SANDSTONE = 3;
    public static final int CUT_RED_SANDSTONE = 4;

    private static final String[] NAMES = new String[]{
            "Double Mossy Stone Brick Slab",
            "Double Smooth Quartz Slab",
            "Double Stone Slab",
            "Double Cut Sandstone Slab",
            "Double Cut Red Sandstone Slab",
            "Double Slab",
            "Double Slab",
            "Double Slab",
    };

    public BlockDoubleSlabStone4() {
        this(0);
    }

    public BlockDoubleSlabStone4(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB4;
    }

    @Override
    public String getName() {
        return NAMES[getDamage() & TYPE_MASK];
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
                    Item.get(getItemId(STONE_SLAB4), getDamage() & TYPE_MASK, 2)
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(STONE_SLAB4), getDamage() & TYPE_MASK);
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & TYPE_MASK) {
            default:
            case MOSSY_STONE_BRICK:
            case STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case SMOOTH_QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case CUT_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case CUT_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
        }
    }
}
