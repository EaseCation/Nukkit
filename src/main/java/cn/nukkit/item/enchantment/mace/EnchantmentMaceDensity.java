package cn.nukkit.item.enchantment.mace;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentMaceDensity extends EnchantmentMace {
    protected EnchantmentMaceDensity() {
        super(DENSITY, EnchantmentNames.DENSITY, "heavy_weapon.density", Rarity.UNCOMMON);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 8 * level - 3;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 8 * level + 17;
    }

    @Override
    public float getDamageBonus(Entity attacker, Entity entity) {
        float fallDistance = attacker.fallDistance;
        if (fallDistance <= 0) {
            return 0;
        }
        return (getLevel() + 3) * fallDistance;
    }
}
