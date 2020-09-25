package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockNetherReactor extends BlockSolid {

    public BlockNetherReactor() {

    }

    @Override
    public String getName() {
        return "Nether Reactor Core";
    }

    @Override
    public int getId() {
        return NETHER_REACTOR;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.DIAMOND, 0, 3),
                    Item.get(Item.IRON_INGOT, 0, 6)
            };
        }
        return new Item[0];
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
