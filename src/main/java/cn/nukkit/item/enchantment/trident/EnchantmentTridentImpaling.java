package cn.nukkit.item.enchantment.trident;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentNames;

public class EnchantmentTridentImpaling extends EnchantmentTrident {
    public EnchantmentTridentImpaling() {
        super(Enchantment.IMPALING, EnchantmentNames.IMPALING, "tridentImpaling", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 8 * level - 7;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(Entity attacker, Entity entity) {
        if (entity.isInsideOfWater() || (entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity))) {
            return 2.5f * getLevel();
        }

        return 0;
    }
}
