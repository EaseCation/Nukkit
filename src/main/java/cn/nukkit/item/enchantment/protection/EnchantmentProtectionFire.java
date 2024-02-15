package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionFire extends EnchantmentProtection {

    public EnchantmentProtectionFire() {
        super(FIRE_PROTECTION, EnchantmentNames.FIRE_PROTECTION, "fire", Rarity.UNCOMMON, TYPE.FIRE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 12;
    }

    @Override
    public int getTypeModifier() {
        return 2;
    }

    @Override
    public int getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || cause != DamageCause.LAVA && cause != DamageCause.FIRE && cause != DamageCause.FIRE_TICK && cause != DamageCause.MAGMA) {
            return 0;
        }

        return getLevel() * getTypeModifier();
    }
}
