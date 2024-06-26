package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentCrossbowPiercing extends EnchantmentCrossbow {

    public EnchantmentCrossbowPiercing() {
        super(Enchantment.PIERCING, EnchantmentNames.PIERCING, "crossbowPiercing", Rarity.COMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50 + this.getMinEnchantAbility(level);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != MULTISHOT;
    }
}
