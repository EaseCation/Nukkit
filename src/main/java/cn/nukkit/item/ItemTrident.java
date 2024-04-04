package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class ItemTrident extends ItemTool {

    public ItemTrident() {
        this(0, 1);
    }

    public ItemTrident(Integer meta) {
        this(meta, 1);
    }

    public ItemTrident(Integer meta, int count) {
        super(TRIDENT, meta, count, "Trident");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_TRIDENT;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getAttackDamage() {
        return 9;
    }

    private boolean canUse() {
        return getDamage() < getMaxDurability();
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        //TODO: riptide check
        return canUse();
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return canUse();
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        if (!canUse()) {
            return false;
        }

        if (this.hasEnchantment(Enchantment.RIPTIDE)) {
            return true;
        }

        int oldDamage = getDamage();
        if (player.isSurvivalLike()) {
            hurtAndBreak(1);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector3 dir = Vector3.directionFromRotation(player.pitch, player.yaw)
                .add(0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian());
        CompoundTag nbt = Entity.getDefaultNBT(player.getEyePosition(), dir, (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection());

        EntityThrownTrident trident = new EntityThrownTrident(player.getChunk(), nbt, player);
        trident.setFavoredSlot(player.getInventory().getHeldItemIndex());
        trident.setItem(this);

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2.5;

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, trident, f);

        if (f < 0.1 || ticksUsed < 5) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
            setDamage(oldDamage);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            ProjectileLaunchEvent ev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                entityShootBowEvent.getProjectile().close();
            } else {
                EntityProjectile projectile = entityShootBowEvent.getProjectile();
                if (player.isCreative() && projectile instanceof EntityThrownTrident) {
                    ((EntityThrownTrident) projectile).setPickupMode(EntityThrownTrident.PICKUP_CREATIVE);
                }
                projectile.spawnToAll();
                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_THROW);
                if (!player.isCreative()) {
                    this.count--;
                    player.getInventory().setItemInHand(this);
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
    public boolean additionalDamageOnBreak() {
        return true;
    }
}
