package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.ProjectileFactory;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
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

    public boolean onClickAir(Player player, Vector3 directionVector) {
        CompoundTag nbt = Entity.getDefaultNBT(player.x, player.y + player.getEyeHeight() - 0.3, player.z, directionVector, (float) player.yaw, (float) player.pitch);

        this.correctNBT(nbt);

        EntityProjectile projectile = this.getProjectileEntityFactory().create(player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile != null) {
            if (projectile instanceof EntityEnderPearl) {
                if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                    projectile.close();
                    return false;
                }
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
                if (projectile instanceof EntityEnderPearl) {
                    player.onThrowEnderPearl();
                }
                projectile.spawnToAll();
                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_THROW, EntityFullNames.PLAYER);
            }
        } else {
            return false;
        }
        return true;
    }

    protected void correctNBT(CompoundTag nbt) {

    }
}
