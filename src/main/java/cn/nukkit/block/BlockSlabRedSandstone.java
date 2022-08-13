package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabRedSandstone extends BlockSlab {

    public static final int RED_SANDSTONE = 0;
    public static final int PURPUR = 1;
    public static final int PRISMARINE_ROUGH = 2;
    public static final int PRISMARINE_DARK = 3;
    public static final int PRISMARINE_BRICK = 4;
    public static final int MOSSY_COBBLESTONE = 5;
    public static final int SMOOTH_SANDSTONE = 6;
    public static final int RED_NETHER_BRICK = 7;

    private static final String[] NAMES = new String[]{
            "Red Sandstone Slab",
            "Purpur Slab",
            "Prismarine Slab",
            "Dark Prismarine Slab",
            "Prismarine Brick Slab",
            "Mossy Cobblestone Slab",
            "Smooth Sandstone Slab",
            "Red Nether Brick Slab",
    };

    public BlockSlabRedSandstone() {
        this(0);
    }

    public BlockSlabRedSandstone(int meta) {
        super(meta, DOUBLE_STONE_SLAB2);
    }

    @Override
    public int getId() {
        return STONE_SLAB2;
    }

    @Override
    public String getName() {
        return ((this.getDamage() & TOP_SLOT_BIT) == TOP_SLOT_BIT ? "Upper " : "") + NAMES[this.getDamage() & TYPE_MASK];
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
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
    public BlockColor getColor() {
        switch (this.getDamage() & TYPE_MASK) {
            default:
            case RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case PURPUR:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case PRISMARINE_ROUGH:
                return BlockColor.CYAN_BLOCK_COLOR;
            case PRISMARINE_DARK:
            case PRISMARINE_BRICK:
                return BlockColor.DIAMOND_BLOCK_COLOR;
            case MOSSY_COBBLESTONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case SMOOTH_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case RED_NETHER_BRICK:
                return BlockColor.NETHER_BLOCK_COLOR;
        }
    }
}