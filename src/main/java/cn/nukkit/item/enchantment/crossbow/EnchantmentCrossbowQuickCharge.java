package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentCrossbowQuickCharge extends EnchantmentCrossbow {

    public EnchantmentCrossbowQuickCharge() {
        super(Enchantment.QUICK_CHARGE, EnchantmentNames.QUICK_CHARGE, "crossbowQuickCharge", Rarity.UNCOMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 12 + 20 * (level - 1);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50 + this.getMinEnchantAbility(level);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
