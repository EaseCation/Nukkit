package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public class BlockOreGoldNether extends BlockSolid {
    public BlockOreGoldNether() {
    }

    @Override
    public int getId() {
        return NETHER_GOLD_ORE;
    }

    @Override
    public String getName() {
        return "Nether Gold Ore";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
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
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            return new Item[]{
                    Item.get(Item.GOLD_NUGGET, 2, 7),
            };
        }
        return new Item[0];
    }
}
