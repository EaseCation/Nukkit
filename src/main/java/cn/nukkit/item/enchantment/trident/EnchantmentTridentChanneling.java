package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentTridentChanneling extends EnchantmentTrident {
    public EnchantmentTridentChanneling() {
        super(Enchantment.CHANNELING, EnchantmentNames.CHANNELING, "tridentChanneling", Rarity.VERY_RARE);
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != RIPTIDE;
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
