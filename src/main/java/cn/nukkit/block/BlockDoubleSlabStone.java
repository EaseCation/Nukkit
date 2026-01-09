package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockDoubleSlabStone extends BlockDoubleSlab {
    public static final int[] STONE_BLOCK_DOUBLE_SLABS = {
            SMOOTH_STONE_DOUBLE_SLAB,
            SANDSTONE_DOUBLE_SLAB,
            PETRIFIED_OAK_DOUBLE_SLAB,
            COBBLESTONE_DOUBLE_SLAB,
            BRICK_DOUBLE_SLAB,
            STONE_BRICK_DOUBLE_SLAB,
            QUARTZ_DOUBLE_SLAB,
            NETHER_BRICK_SLAB
    };
    public static final int[] STONE_BLOCK_DOUBLE_SLABS2 = {
            RED_SANDSTONE_DOUBLE_SLAB,
            PURPUR_DOUBLE_SLAB,
            PRISMARINE_DOUBLE_SLAB,
            DARK_PRISMARINE_DOUBLE_SLAB,
            PRISMARINE_BRICK_DOUBLE_SLAB,
            MOSSY_COBBLESTONE_DOUBLE_SLAB,
            SMOOTH_SANDSTONE_DOUBLE_SLAB,
            RED_NETHER_BRICK_DOUBLE_SLAB,
    };
    public static final int[] STONE_BLOCK_DOUBLE_SLABS3 = {
            END_STONE_BRICK_DOUBLE_SLAB,
            SMOOTH_RED_SANDSTONE_DOUBLE_SLAB,
            POLISHED_ANDESITE_DOUBLE_SLAB,
            ANDESITE_DOUBLE_SLAB,
            DIORITE_DOUBLE_SLAB,
            POLISHED_DIORITE_DOUBLE_SLAB,
            GRANITE_DOUBLE_SLAB,
            POLISHED_GRANITE_DOUBLE_SLAB,
    };
    public static final int[] STONE_BLOCK_DOUBLE_SLABS4 = {
            MOSSY_STONE_BRICK_DOUBLE_SLAB,
            SMOOTH_QUARTZ_DOUBLE_SLAB,
            NORMAL_STONE_DOUBLE_SLAB,
            CUT_SANDSTONE_DOUBLE_SLAB,
            CUT_RED_SANDSTONE_DOUBLE_SLAB,
    };

    public static final int TYPE_SMOOTH_STONE = 0;
    public static final int TYPE_SANDSTONE = 1;
    public static final int TYPE_WOOD = 2;
    public static final int TYPE_COBBLESTONE = 3;
    public static final int TYPE_BRICK = 4;
    public static final int TYPE_STONE_BRICK = 5;
    public static final int TYPE_QUARTZ = 6;
    public static final int TYPE_NETHER_BRICK = 7;

    public static final int TYPE_RED_SANDSTONE = 0;
    public static final int TYPE_PURPUR = 1;
    public static final int TYPE_PRISMARINE_ROUGH = 2;
    public static final int TYPE_PRISMARINE_DARK = 3;
    public static final int TYPE_PRISMARINE_BRICK = 4;
    public static final int TYPE_MOSSY_COBBLESTONE = 5;
    public static final int TYPE_SMOOTH_SANDSTONE = 6;
    public static final int TYPE_RED_NETHER_BRICK = 7;

    public static final int TYPE_END_STONE_BRICK = 0;
    public static final int TYPE_SMOOTH_RED_SANDSTONE = 1;
    public static final int TYPE_POLISHED_ANDESITE = 2;
    public static final int TYPE_ANDESITE = 3;
    public static final int TYPE_DIORITE = 4;
    public static final int TYPE_POLISHED_DIORITE = 5;
    public static final int TYPE_GRANITE = 6;
    public static final int TYPE_POLISHED_GRANITE = 7;

    public static final int TYPE_MOSSY_STONE_BRICK = 0;
    public static final int TYPE_SMOOTH_QUARTZ = 1;
    public static final int TYPE_STONE = 2;
    public static final int TYPE_CUT_SANDSTONE = 3;
    public static final int TYPE_CUT_RED_SANDSTONE = 4;

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return super.getDrops(item, player);
        }
        return new Item[0];
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}