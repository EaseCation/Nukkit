package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import javax.annotation.Nullable;

/**
 * This event is fired when the spawn point of the player is changed.
 */
public class PlayerSpawnChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    private Position newSpawn;
    private final Cause cause;

    public PlayerSpawnChangeEvent(Player player, @Nullable Position newSpawn, Cause cause) {
        this.player = player;
        this.newSpawn = newSpawn;
        this.cause = cause;
    }

    /**
     * Gets the new spawn to be set.
     *
     * @return new spawn location
     */
    @Nullable
    public Position getNewSpawn() {
        return this.newSpawn;
    }

    /**
     * Sets the new spawn location.
     *
     * @param newSpawn new spawn location
     */
    public void setNewSpawn(@Nullable Position newSpawn) {
        this.newSpawn = newSpawn;
    }

    /**
     * Gets the cause of spawn change.
     *
     * @return change cause
     */
    public Cause getCause() {
        return this.cause;
    }

    public enum Cause {
        /**
         * Indicate the spawn was set by the player interacting with a bed.
         */
        BED,
        /**
         * Indicate the spawn was set by the player interacting with a respawn anchor.
         */
        RESPAWN_ANCHOR,
        /**
         * Indicate the spawn was set by a command.
         */
        COMMAND,
        /**
         * Indicate the spawn was set by the use of plugins.
         */
        PLUGIN,
        /**
         * Indicate the spawn was reset by an invalid bed position or empty respawn anchor.
         */
        RESET,
    }
}
