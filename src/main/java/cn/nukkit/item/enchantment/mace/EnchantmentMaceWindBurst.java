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
    public boolean isLootable() {
        return false;
    }
}
