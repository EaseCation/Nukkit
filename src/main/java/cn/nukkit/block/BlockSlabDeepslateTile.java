package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockSlabDeepslateTile extends BlockSlab {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabDeepslateTile() {
        this(0);
    }

    public BlockSlabDeepslateTile(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DEEPSLATE_TILE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Deepslate Tile Slab" : "Deepslate Tile Slab";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 18;
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
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
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
        return DEEPSLATE_TILE_DOUBLE_SLAB;
    }
}
