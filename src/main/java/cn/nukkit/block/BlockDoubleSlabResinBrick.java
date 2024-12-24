package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabResinBrick extends BlockDoubleSlab {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabResinBrick() {
        this(0);
    }

    public BlockDoubleSlabResinBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESIN_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Resin Brick Slab";
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
                    Item.get(getItemId(getSlabBlockId()), this.getSlabType(), 2),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getSlabBlockId() {
        return RESIN_BRICK_SLAB;
    }
}
