package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreLapis extends BlockSolid {

    public BlockOreLapis() {
    }

    @Override
    public int getId() {
        return LAPIS_ORE;
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
    public String getName() {
        return "Lapis Lazuli Ore";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int count = random.nextInt(4, 10);

            int fortune = item.getId() != Item.ENCHANTED_BOOK ? item.getValidEnchantmentLevel(Enchantment.FORTUNE) : 0;
            if (fortune > 0) {
                count *= random.nextInt(2, 2 + fortune + 1);

                if (count < 0) {
                    return new Item[0];
                }
            }

            return new Item[]{
                    Item.get(Item.DYE, ItemDye.LAPIS_LAZULI, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(2, 6);
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
