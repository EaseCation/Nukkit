package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabStone extends BlockSlab {
    public static final int TYPE_SMOOTH_STONE = 0;
    public static final int TYPE_SANDSTONE = 1;
    public static final int TYPE_WOOD = 2;
    public static final int TYPE_COBBLESTONE = 3;
    public static final int TYPE_BRICK = 4;
    public static final int TYPE_STONE_BRICK = 5;
    public static final int TYPE_QUARTZ = 6;
    public static final int TYPE_NETHER_BRICK = 7;

    private static final String[] NAMES = new String[]{
            "Smooth Stone Slab",
            "Sandstone Slab",
            "Wooden Slab",
            "Cobblestone Slab",
            "Brick Slab",
            "Stone Brick Slab",
            "Quartz Slab",
            "Nether Brick Slab",
    };

    public BlockSlabStone() {
        this(0);
    }

    public BlockSlabStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_SLAB;
    }

    @Override
    public String getName() {
        return (isTopSlot() ? "Upper " : "") + NAMES[this.getSlabType()];
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
    public BlockColor getColor() {
        switch (this.getSlabType()) {
            default:
            case TYPE_SMOOTH_STONE:
            case TYPE_COBBLESTONE:
            case TYPE_BRICK:
            case TYPE_STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case TYPE_WOOD:
                return BlockColor.WOOD_BLOCK_COLOR;
            case TYPE_QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case TYPE_NETHER_BRICK:
                return BlockColor.NETHER_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DOUBLE_STONE_SLAB;
    }
}