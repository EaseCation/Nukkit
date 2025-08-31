package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/26 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockOreQuartz extends BlockSolid {

    public BlockOreQuartz() {
    }

    @Override
    public String getName() {
        return "Quartz Ore";
    }

    @Override
    public int getId() {
        return QUARTZ_ORE;
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
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            int count = 1;
            int fortune = item.getId() != Item.ENCHANTED_BOOK ? item.getValidEnchantmentLevel(Enchantment.FORTUNE) : 0;
            if (fortune > 0) {
                int i = ThreadLocalRandom.current().nextInt(fortune + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = i + 1;
            }

            return new Item[]{
                    Item.get(Item.QUARTZ, 0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
