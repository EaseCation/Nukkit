package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

public class PlayerPassengerJumpEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final float strength;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerPassengerJumpEvent(Player player, float strength) {
        this.player = player;
        this.strength = strength;
    }

    public float getStrength() {
        return strength;
    }
}
