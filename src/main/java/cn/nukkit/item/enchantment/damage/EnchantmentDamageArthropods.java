package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentNames;
import cn.nukkit.item.enchantment.EnchantmentType;
import cn.nukkit.potion.Effect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDamageArthropods extends EnchantmentDamage {

    public EnchantmentDamageArthropods() {
        super(BANE_OF_ARTHROPODS, EnchantmentNames.BANE_OF_ARTHROPODS, "arthropods", Rarity.UNCOMMON, EnchantmentType.MELEE_WEAPON, TYPE.SMITE);
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
    public float getDamageBonus(Entity attacker, Entity entity) {
        if (entity instanceof EntityArthropod) {
            return getLevel() * 2.5f;
        }

        return 0;
    }

    @Override
    public void doPostAttack(Item item, Entity attacker, Entity entity, DamageCause cause) {
        if (entity instanceof EntityArthropod) {
            int duration = 20 + ThreadLocalRandom.current().nextInt(10 * this.level);
            entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(duration).setAmplifier(3));
        }
    }
}
