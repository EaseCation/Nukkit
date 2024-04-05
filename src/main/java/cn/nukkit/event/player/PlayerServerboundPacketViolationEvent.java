package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.PacketViolationReason;

import javax.annotation.Nullable;

public class PlayerServerboundPacketViolationEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PacketViolationReason reason;
    private final String tag;
    private final String context;
    private boolean kick = true;
    @Nullable
    private String message;

    public PlayerServerboundPacketViolationEvent(Player player, PacketViolationReason reason,  String tag) {
        this(player, reason, tag, "");
    }

    public PlayerServerboundPacketViolationEvent(Player player, PacketViolationReason reason, String tag, String context) {
        this.player = player;
        this.reason = reason;
        this.tag = tag;
        this.context = context;
    }

    public PacketViolationReason getReason() {
        return reason;
    }

    public String getTag() {
        return tag;
    }

    public String getContext() {
        return context;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }
}
