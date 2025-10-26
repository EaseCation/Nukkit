package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.getOffhandInventory().contains(LazyHolder.ARROW) || player.getInventory().contains(LazyHolder.ARROW) || player.isCreative();
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        Item matched;

        Inventory inventory = player.getOffhandInventory();
        matched = inventory.peek(LazyHolder.ARROW);
        if (matched.isNull()) {
            inventory = player.getInventory();
            matched = inventory.peek(LazyHolder.ARROW);
            if (matched.isNull() && !player.isCreative()) {
                player.getOffhandInventory().sendContents(player);
                inventory.sendContents(player);
                return false;
            }
        }
        matched = matched.clone();
        matched.setCount(1);

        double damage = 2;

        int bowDamage = this.getEnchantmentLevel(Enchantment.POWER);
        if (bowDamage > 0) {
            damage += (double) bowDamage * 0.5 + 0.5;
        }

        int flameEnchant = this.getEnchantmentLevel(Enchantment.FLAME);
        boolean flame = flameEnchant > 0;

        float knockbackH = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_H;
        float knockbackV = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_V;
        int knockbackEnchant = this.getEnchantmentLevel(Enchantment.PUNCH);
        if (knockbackEnchant > 0) {
            knockbackH += 0.1f * knockbackEnchant;
            knockbackV += 0.1f * knockbackEnchant;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector3 dir = Vector3.directionFromRotation(player.pitch, player.yaw)
                .add(0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian());
        CompoundTag nbt = Entity.getDefaultNBT(player.getEyePosition(), dir.multiply(1.2), (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection()) //TODO: pow
                .putShort("Fire", flame ? 45 * 60 : 0)
                .putDouble("damage", damage)
                .putFloat("KnockbackH", knockbackH)
                .putFloat("KnockbackV", knockbackV)
                .putByte("auxValue", matched.getDamage());

        if (matched.getDamage() != ItemArrow.NORMAL_ARROW) {
            Potion potion = Potion.getPotion(matched.getDamage() - ItemArrow.TIPPED_ARROW);
            if (potion != null) {
                Effect[] effects = potion.getEffects();
                ListTag<CompoundTag> mobEffects = new ListTag<>("mobEffects");
                for (Effect effect : effects) {
                    mobEffects.add(effect.save());
                }
                nbt.putList(mobEffects);
            }
        }

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2;

        EntityArrow arrow = new EntityArrow(player.getChunk(), nbt, player, f == 2);

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
            player.getInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            int infinityEnchant = this.getEnchantmentLevel(Enchantment.INFINITY);
            boolean infinity = infinityEnchant > 0 && matched.getDamage() == ItemArrow.NORMAL_ARROW;
            EntityProjectile projectile;
            if ((infinity || player.isCreative()) && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_CREATIVE);
            }
            if (player.isSurvivalLike()) {
                if (!infinity) {
                    inventory.removeItem(matched);
                }
                int itemDamaged = hurtAndBreak(1);
                if (itemDamaged != 0) {
                    if (itemDamaged < 0) {
                        pop();
                        player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                    }
                    player.getInventory().setItemInHand(this);
                } else if (!player.isServerAuthoritativeInventoryEnabled()) {
                    player.getInventory().sendHeldItem(player); // sync durability to correct client predictions
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().close();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        }

        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public int getUseDuration() {
        return 72000;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }

    private static class LazyHolder {
        private static final Item ARROW = Item.get(Item.ARROW, null, 1).clearCompoundTag();
    }
}
