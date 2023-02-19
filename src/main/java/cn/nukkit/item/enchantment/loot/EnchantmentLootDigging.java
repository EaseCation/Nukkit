package cn.nukkit.item.enchantment.loot;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLootDigging extends EnchantmentLoot {
    public EnchantmentLootDigging() {
        super(Enchantment.FORTUNE, "fortune", "lootBonusDigger", Rarity.RARE, EnchantmentType.DIGGER);
    }
}
