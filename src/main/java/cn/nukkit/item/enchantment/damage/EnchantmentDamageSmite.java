package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.enchantment.EnchantmentNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDamageSmite extends EnchantmentDamage {

    public EnchantmentDamageSmite() {
        super(SMITE, EnchantmentNames.SMITE, "undead", Rarity.UNCOMMON, TYPE.SMITE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public float getDamageBonus(Entity entity) {
        if(entity instanceof EntitySmite) {
            return getLevel() * 2.5f;
        }

        return 0;
    }
}
