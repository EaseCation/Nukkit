package cn.nukkit.block;

import cn.nukkit.Player;
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

    @Deprecated
    public static final int TYPE_MASK = 0b1111;

    BlockWallCobblestone() {

    }

    @Override
    public int getId() {
        return COBBLESTONE_WALL;
    }

    @Override
    public String getName() {
        return "Cobblestone Wall";
    }

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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
