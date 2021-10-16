package cn.nukkit.entity;

import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface EntityRideable {

    /**
     * Mount or Dismounts an Entity from a rideable entity
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    boolean mountEntity(Entity entity);

    boolean dismountEntity(Entity entity);

    default void onPlayerRiding(Vector3 pos, double yaw, double pitch) {
        // Do nothing by default
    }

}
