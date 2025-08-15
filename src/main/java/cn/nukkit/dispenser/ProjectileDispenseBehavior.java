package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior extends DefaultDispenseBehavior {

    private final EntityFactory factory;

    public ProjectileDispenseBehavior(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Item dispense(BlockDispenser source, BlockFace face, Item item) {
        if (item instanceof ItemDurable) {
            if (item.getDamage() >= item.getMaxDurability()) {
                return super.dispense(source, face, item);
            }

            item.hurtAndBreak(1);
        }

        Vector3 dispensePos = source.getDispensePosition();

        CompoundTag nbt = Entity.getDefaultNBT(dispensePos);
        this.correctNBT(nbt, item);

        Entity projectile = factory.create(source.level.getChunk(dispensePos.getChunkX(), dispensePos.getChunkZ()), nbt);

        if (!(projectile instanceof EntityProjectile)) {
            return super.dispense(source, face, item);
        }

        source.level.addLevelEvent(dispensePos, LevelEventPacket.EVENT_SOUND_SHOOT, 78642);

        Vector3 motion = new Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset())
                .normalize();

        projectile.setMotion(motion);
        ((EntityProjectile) projectile).inaccurate(getAccuracy());
        projectile.setMotion(projectile.getMotion().multiply(getMotion()));

        ((EntityProjectile) projectile).updateRotation();

        projectile.spawnToAll();
        return null;
    }

    protected double getMotion() {
        return 1.1;
    }

    protected float getAccuracy() {
        return 6;
    }

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    protected void correctNBT(CompoundTag nbt, Item item) {

    }
}
