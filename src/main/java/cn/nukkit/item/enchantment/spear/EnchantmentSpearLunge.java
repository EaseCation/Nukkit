package cn.nukkit.item.enchantment.spear;

import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentSpearLunge extends EnchantmentSpear {
    public EnchantmentSpearLunge() {
        super(LUNGE, EnchantmentNames.LUNGE, "lunge", Rarity.RARE);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 20;
    }
}
