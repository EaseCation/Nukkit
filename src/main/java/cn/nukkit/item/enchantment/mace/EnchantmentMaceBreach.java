package cn.nukkit.item.enchantment.mace;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;
import cn.nukkit.item.enchantment.damage.EnchantmentDamage;

public class EnchantmentMaceBreach extends EnchantmentMace {
    public EnchantmentMaceBreach() {
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

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != DENSITY && !(enchantment instanceof EnchantmentDamage);
    }
}
