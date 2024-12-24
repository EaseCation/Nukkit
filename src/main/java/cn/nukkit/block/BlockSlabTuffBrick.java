package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSlabTuffBrick extends BlockSlab {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabTuffBrick() {
        this(0);
    }

    public BlockSlabTuffBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TUFF_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Tuff Brick Slab" : "Tuff Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
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
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getTopSlotBit() {
        return TOP_SLOT_BIT;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return TUFF_BRICK_DOUBLE_SLAB;
    }
}
