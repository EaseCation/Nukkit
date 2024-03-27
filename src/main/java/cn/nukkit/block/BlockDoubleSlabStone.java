package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoubleSlabStone extends BlockDoubleSlab {
    public static final int SMOOTH_STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOOD = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    private static final String[] NAMES = new String[]{
            "Double Smooth Stone Slab",
            "Double Sandstone Slab",
            "Double Wooden Slab",
            "Double Cobblestone Slab",
            "Double Brick Slab",
            "Double Stone Brick Slab",
            "Double Quartz Slab",
            "Double Nether Brick Slab",
    };

    public BlockDoubleSlabStone() {
        this(0);
    }

    public BlockDoubleSlabStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB;
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
    public String getName() {
        return NAMES[this.getSlabType()];
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(getItemId(getSlabBlockId()), this.getSlabType(), 2)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        switch (this.getSlabType()) {
            default:
            case SMOOTH_STONE:
            case COBBLESTONE:
            case BRICK:
            case STONE_BRICK:
                return BlockColor.STONE_BLOCK_COLOR;
            case SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case WOOD:
                return BlockColor.WOOD_BLOCK_COLOR;
            case QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case NETHER_BRICK:
                return BlockColor.NETHER_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected int getSlabBlockId() {
        return STONE_SLAB;
    }
}