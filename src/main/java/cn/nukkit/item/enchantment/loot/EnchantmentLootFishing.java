package cn.nukkit.item.enchantment.loot;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLootFishing extends EnchantmentLoot {
    public EnchantmentLootFishing() {
        super(Enchantment.LUCK_OF_THE_SEA, "luck_of_the_sea", "lootBonusFishing", Rarity.RARE, EnchantmentType.FISHING_ROD);
    }
}
