package cn.nukkit.item.enchantment.mace;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentMaceWindBurst extends EnchantmentMace {
    public EnchantmentMaceWindBurst() {
        super(WIND_BURST, EnchantmentNames.WIND_BURST, "heavy_weapon.windburst", Rarity.RARE);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 9 * level + 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public void doAttack(Entity attacker, Entity entity) {
        if (level <= 0) {
            return;
        }

        float fallDistance = attacker.fallDistance;
        if (fallDistance <= 0) {
            return;
        }

        float knockbackScaling = (getLevel() + 1) * 0.25f;
        //TODO: WindBurstUtility::burst
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isLootable() {
        return false;
    }
}
