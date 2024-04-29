package cn.nukkit.item.enchantment.mace;

import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentMaceBreach extends EnchantmentMace {
    protected EnchantmentMaceBreach() {
        super(BREACH, EnchantmentNames.BREACH, "heavy_weapon.breach", Rarity.RARE);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return level + 8 * (level + 7);
    }
}
