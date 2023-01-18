package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.PacketViolationReason;

public class PlayerServerboundPacketViolationEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PacketViolationReason reason;
    private boolean kick = true;

    public PlayerServerboundPacketViolationEvent(Player player, PacketViolationReason reason) {
        this.player = player;
        this.reason = reason;
    }

    public PacketViolationReason getReason() {
        return reason;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }
}
