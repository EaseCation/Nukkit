package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentBowInfinity extends EnchantmentBow {
    public EnchantmentBowInfinity() {
        super(Enchantment.INFINITY, EnchantmentNames.INFINITY, "arrowInfinite", Rarity.VERY_RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != MENDING;
    }
}
