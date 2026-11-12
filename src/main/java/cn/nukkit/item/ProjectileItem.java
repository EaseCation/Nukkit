package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.InventorySlotReference;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public abstract class ProjectileItem extends Item {

    public ProjectileItem(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public abstract ProjectileFactory getProjectileEntityFactory();

    public abstract float getThrowForce();

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.isMainThreadInputEnabled() || player.isServerAuthoritativeInventoryEnabled()) {
            return onClickAirGuarded(player, directionVector);
        }
        CompoundTag nbt = Entity.getDefaultNBT(player.x, player.y + player.getEyeHeight() - 0.3, player.z, directionVector, (float) player.yaw, (float) player.pitch);

        this.correctNBT(nbt);

        EntityProjectile projectile = this.getProjectileEntityFactory().create(player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile != null) {
            CooldownCategory cooldownCategory = getCooldownCategory();
            if (cooldownCategory != null && player.isItemCooling(cooldownCategory, getCooldownDuration())) {
                projectile.close();
                return false;
            }

            projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));

            ProjectileLaunchEvent ev = new ProjectileLaunchEvent(projectile);
            player.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                projectile.close();
            } else {
                if (!player.isCreative()) {
                    this.count--;
                }

                if (cooldownCategory != null) {
                    player.startItemCooldown(cooldownCategory);
                }

                projectile.spawnToAll();

                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_THROW, EntityFullNames.PLAYER);
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean onClickAirGuarded(Player player, Vector3 directionVector) {
        InventorySlotReference source = player.getInventory().captureHeldItem();
        long movementEpoch = player.getMovementEpoch();
        Level level = player.getLevel();
        if (!player.canContinueItemUse(movementEpoch) || !source.getSnapshot().equalsExact(this)) {
            source.sendContents(player);
            return false;
        }
        CooldownCategory cooldownCategory = getCooldownCategory();
        if (cooldownCategory != null && player.isItemCooling(cooldownCategory, getCooldownDuration())) {
            return false;
        }

        CompoundTag nbt = Entity.getDefaultNBT(player.x, player.y + player.getEyeHeight() - 0.3,
                player.z, directionVector, (float) player.yaw, (float) player.pitch);
        correctNBT(nbt);
        EntityProjectile projectile = getProjectileEntityFactory().create(
                level.getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile == null) {
            return false;
        }
        projectile.setMotion(projectile.getMotion().multiply(getThrowForce()));
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(projectile);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || projectile.isClosed() || player.getLevel() != level
                || !player.canContinueItemUse(movementEpoch)
                || !source.isSelectedBy(player.getInventory()) || !source.isCurrent()) {
            projectile.close();
            source.sendContents(player);
            return false;
        }

        // 先结算原槽再发布实体，插件取消或改写扣除不能产生免费投射物。
        if (!player.isCreative() && !source.consume(1)
                || player.getLevel() != level || !player.canContinueItemUse(movementEpoch)) {
            projectile.close();
            source.sendContents(player);
            return false;
        }
        if (cooldownCategory != null) {
            player.startItemCooldown(cooldownCategory);
        }
        if (projectile.isClosed() || player.getLevel() != level || !player.canContinueItemUse(movementEpoch)) {
            projectile.close();
            return false;
        }
        projectile.spawnToAll();
        if (player.getLevel() == level && player.canContinueItemUse(movementEpoch)) {
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_THROW, EntityFullNames.PLAYER);
        }
        // 与弩保持相同的自结算约定，外层不能再次写回回调前取得的物品。
        return false;
    }

    protected void correctNBT(CompoundTag nbt) {

    }
}
