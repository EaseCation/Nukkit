package cn.nukkit.event.level;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * Called when a portal is created.
 */
public class PortalCreateEvent extends LevelEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block source;
    private final CreateReason reason;

    public PortalCreateEvent(Level level, Block source, CreateReason reason) {
        super(level);
        this.source = source;
        this.reason = reason;
    }

    public Block getSource() {
        return source;
    }

    /**
     * Gets the reason for the portal's creation.
     *
     * @return CreateReason for the portal's creation
     */
    public CreateReason getReason() {
        return reason;
    }

    /**
     * An enum to specify the various reasons for a portal's creation
     */
    public enum CreateReason {
        /**
         * When the blocks inside a portal are created due to a portal frame being set on fire.
         */
        FIRE,
        /**
         * When a nether portal frame and portal is created at the exit of an entered nether portal.
         */
        NETHER_PAIR,
        /**
         * When the target end platform is created as a result of a entity entering an end portal.
         */
        END_PLATFORM,
    }
}
