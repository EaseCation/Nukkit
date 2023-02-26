package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@FunctionalInterface
public interface ProjectileFactory extends EntityFactory {
    EntityProjectile create(FullChunk chunk, CompoundTag nbt, Entity shootingEntity);

    @Override
    default EntityProjectile create(FullChunk chunk, CompoundTag nbt) {
        return create(chunk, nbt, null);
    }
}
