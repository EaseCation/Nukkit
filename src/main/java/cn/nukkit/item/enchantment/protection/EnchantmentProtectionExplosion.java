package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionExplosion extends EnchantmentProtection {

    public EnchantmentProtectionExplosion() {
        super(BLAST_PROTECTION, EnchantmentNames.BLAST_PROTECTION, "explosion", Rarity.RARE, TYPE.EXPLOSION);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 12;
    }

    @Override
    public double getTypeModifier() {
        return 2;
    }

    @Override
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || cause != DamageCause.ENTITY_EXPLOSION && cause != DamageCause.BLOCK_EXPLOSION && cause != DamageCause.FIREWORKS) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
