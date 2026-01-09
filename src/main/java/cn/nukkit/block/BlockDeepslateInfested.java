package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;

public class BlockDeepslateInfested extends BlockDeepslate {
    BlockDeepslateInfested() {

    }

    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 3.75f;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN && item.hasEnchantment(Enchantment.SILK_TOUCH)) {
            return new Item[]{
                    Item.get(ItemBlockID.DEEPSLATE),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
}
