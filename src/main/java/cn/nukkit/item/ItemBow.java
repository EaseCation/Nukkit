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
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBow extends ItemTool implements ItemReleasable {

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
        return player.getInventory().contains(LazyHolder.ARROW) || player.isCreative();
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

        Enchantment bowDamage = this.getEnchantment(Enchantment.POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }

        Enchantment flameEnchant = this.getEnchantment(Enchantment.FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

        float knockbackH = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_H;
        float knockbackV = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_V;
        Enchantment knockbackEnchant = this.getEnchantment(Enchantment.PUNCH);
        if (knockbackEnchant != null) {
            knockbackH += 0.1 * knockbackEnchant.getLevel();
            knockbackV += 0.1 * knockbackEnchant.getLevel();
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Mth.sin(player.yaw / 180 * Math.PI) * Mth.cos(player.pitch / 180 * Math.PI) * 1.2))
                        .add(new DoubleTag("", -Mth.sin(player.pitch / 180 * Math.PI) * 1.2))
                        .add(new DoubleTag("", Mth.cos(player.yaw / 180 * Math.PI) * Mth.cos(player.pitch / 180 * Math.PI) * 1.2)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw))
                        .add(new FloatTag("", (float) -player.pitch)))
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

        EntityArrow arrow = new EntityArrow(player.chunk, nbt, player, f == 2);

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
            player.getInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            Enchantment infinityEnchant = this.getEnchantment(Enchantment.INFINITY);
            boolean infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0 && matched.getDamage() == ItemArrow.NORMAL_ARROW;
            EntityProjectile projectile;
            if ((infinity || player.isCreative()) && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_CREATIVE);
            }
            if (player.isSurvivalLike()) {
                if (!infinity) {
                    inventory.removeItem(matched);
                }
                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.UNBREAKING);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= ThreadLocalRandom.current().nextInt(100))) {
                        this.setDamage(this.getDamage() + 1);
                        if (this.getDamage() >= getMaxDurability()) {
                            this.count--;
                        }
                        player.getInventory().setItemInHand(this);
                    }
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        }

        return true;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    private static class LazyHolder {
        private static final Item ARROW = Item.get(Item.ARROW, null, 1).clearCompoundTag();
    }
}
