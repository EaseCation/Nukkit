package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

import java.util.concurrent.ThreadLocalRandom;

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
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
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
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            return new Item[]{
                    Item.get(Item.GOLD_NUGGET, 2, 7),
            };
        }
        return new Item[0];
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(2);
    }
}
