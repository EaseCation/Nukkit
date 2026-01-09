package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public abstract class BlockSlabStone extends BlockSlab {
    public static final int[] STONE_BLOCK_SLABS = {
            SMOOTH_STONE_SLAB,
            SANDSTONE_SLAB,
            PETRIFIED_OAK_SLAB,
            COBBLESTONE_SLAB,
            BRICK_SLAB,
            STONE_BRICK_SLAB,
            QUARTZ_SLAB,
            NETHER_BRICK_SLAB
    };
    public static final int[] STONE_BLOCK_SLABS2 = {
            RED_SANDSTONE_SLAB,
            PURPUR_SLAB,
            PRISMARINE_SLAB,
            DARK_PRISMARINE_SLAB,
            PRISMARINE_BRICK_SLAB,
            MOSSY_COBBLESTONE_SLAB,
            SMOOTH_SANDSTONE_SLAB,
            RED_NETHER_BRICK_SLAB,
    };
    public static final int[] STONE_BLOCK_SLABS3 = {
            END_STONE_BRICK_SLAB,
            SMOOTH_RED_SANDSTONE_SLAB,
            POLISHED_ANDESITE_SLAB,
            ANDESITE_SLAB,
            DIORITE_SLAB,
            POLISHED_DIORITE_SLAB,
            GRANITE_SLAB,
            POLISHED_GRANITE_SLAB,
    };
    public static final int[] STONE_BLOCK_SLABS4 = {
            MOSSY_STONE_BRICK_SLAB,
            SMOOTH_QUARTZ_SLAB,
            NORMAL_STONE_SLAB,
            CUT_SANDSTONE_SLAB,
            CUT_RED_SANDSTONE_SLAB,
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
    public Item[] getDrops(Item item, Player player) {
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
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
