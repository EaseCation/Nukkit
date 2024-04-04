package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionFall extends EnchantmentProtection {

    public EnchantmentProtectionFall() {
        super(FEATHER_FALLING, EnchantmentNames.FEATHER_FALLING, "fall", Rarity.UNCOMMON, TYPE.FALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 10;
    }

    @Override
    public int getTypeModifier() {
        return 3;
    }

    @Override
    public int getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || cause != DamageCause.FALL && cause != DamageCause.STALAGMITE) {
            return 0;
        }

        return getLevel() * getTypeModifier();
    }
}
