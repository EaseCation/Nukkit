package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentThorns extends Enchantment {
    protected EnchantmentThorns() {
        super(THORNS, EnchantmentNames.THORNS, "thorns", Rarity.RARE, EnchantmentType.ARMOR);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(Item item, Entity attacker, Entity entity, DamageCause cause) {
        if (cause == DamageCause.THORNS) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (shouldHit(random, getLevel())) {
            attacker.attack(new EntityDamageByEntityEvent(entity, attacker, EntityDamageEvent.DamageCause.THORNS, getDamage(random, level)));

            Vector3 pos = attacker.getEyePosition();
            attacker.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_THORNS);

            int sound = item.getEquippingSound();
            if (sound != -1) {
                attacker.level.addLevelSoundEvent(pos, sound);
            }

            item.hurtAndBreak(3);
        } else {
            item.hurtAndBreak(1);
        }
    }

    private static boolean shouldHit(ThreadLocalRandom random, int level) {
        return level > 0 && random.nextFloat() < 0.15 * level;
    }

    private static int getDamage(ThreadLocalRandom random, int level) {
        return level > 10 ? level - 10 : random.nextInt(1, 5);
    }
}
