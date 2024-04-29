package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.EnchantmentNames;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDamageAll extends EnchantmentDamage {

    public EnchantmentDamageAll() {
        super(SHARPNESS, EnchantmentNames.SHARPNESS, "all", Rarity.COMMON, EnchantmentType.SWORD, TYPE.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public int getMaxEnchantableLevel() {
        return 4;
    }

    @Override
    public float getDamageBonus(Entity attacker, Entity entity) {
        return this.getLevel() * 1.25f;
    }
}
