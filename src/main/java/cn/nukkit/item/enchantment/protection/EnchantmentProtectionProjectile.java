package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionProjectile extends EnchantmentProtection {

    public EnchantmentProtectionProjectile() {
        super(PROJECTILE_PROTECTION, EnchantmentNames.PROJECTILE_PROTECTION, "projectile", Rarity.UNCOMMON, TYPE.PROJECTILE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 3 + (level - 1) * 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    public int getTypeModifier() {
        return 3;
    }

    @Override
    public int getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || (cause != DamageCause.PROJECTILE)) {
            return 0;
        }

        return getLevel() * getTypeModifier();
    }
}
