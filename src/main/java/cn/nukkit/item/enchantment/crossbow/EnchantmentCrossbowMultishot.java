package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentCrossbowMultishot extends EnchantmentCrossbow {

    public EnchantmentCrossbowMultishot() {
        super(Enchantment.MULTISHOT, EnchantmentNames.MULTISHOT, "crossbowMultishot", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50 + this.getMinEnchantAbility(level);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != PIERCING;
    }
}
