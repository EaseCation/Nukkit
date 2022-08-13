package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSlabStone3 extends BlockSlab {

    public static final int END_STONE_BRICK = 0;
    public static final int SMOOTH_RED_SANDSTONE = 1;
    public static final int POLISHED_ANDESITE = 2;
    public static final int ANDESITE = 3;
    public static final int DIORITE = 4;
    public static final int POLISHED_DIORITE = 5;
    public static final int GRANITE = 6;
    public static final int POLISHED_GRANITE = 7;

    private static final String[] NAMES = new String[]{
            "End Stone Brick Slab",
            "Smooth Red Sandstone Slab",
            "Polished Andesite Slab",
            "Andesite Slab",
            "Diorite Slab",
            "Polished Diorite Slab",
            "Granite Slab",
            "Polished Granite Slab",
    };

    public BlockSlabStone3() {
        this(0);
    }

    public BlockSlabStone3(int meta) {
        super(meta, DOUBLE_STONE_SLAB3);
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
    }

    @Override
    public String getName() {
        int meta = getDamage();
        return ((meta & TOP_SLOT_BIT) == TOP_SLOT_BIT ? "Upper " : "") + NAMES[meta & TYPE_MASK];
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
                    toItem(true)
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & TYPE_MASK) {
            default:
            case END_STONE_BRICK:
                return BlockColor.SAND_BLOCK_COLOR;
            case SMOOTH_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case POLISHED_ANDESITE:
            case ANDESITE:
                return BlockColor.STONE_BLOCK_COLOR;
            case DIORITE:
            case POLISHED_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case GRANITE:
            case POLISHED_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }
}
