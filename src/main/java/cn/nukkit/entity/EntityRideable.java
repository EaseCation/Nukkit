package cn.nukkit.entity;

import cn.nukkit.Player;

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

    default void onPlayerInput(Player player, double motionX, double motionY) {
        // Do nothing by default
    }

    default void onPlayerInput(Player player, double x, double y, double z, double yaw, double pitch) {
    }

    default void updatePlayerJump(Player player, boolean jumping) {
    }
}
