package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

public class PlayerVehicleInputEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final float moveVecX;
    private final float moveVecY;

    private final boolean jumping;
    private final boolean sneaking;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerVehicleInputEvent(Player player, float moveVecX, float moveVecY) {
        this(player, moveVecX, moveVecY, false, false);
    }

    public PlayerVehicleInputEvent(Player player, float moveVecX, float moveVecY, boolean jumping, boolean sneaking) {
        this.player = player;
        this.moveVecX = moveVecX;
        this.moveVecY = moveVecY;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    public float getMoveVecX() {
        return moveVecX;
    }

    public float getMoveVecY() {
        return moveVecY;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isSneaking() {
        return sneaking;
    }
}
