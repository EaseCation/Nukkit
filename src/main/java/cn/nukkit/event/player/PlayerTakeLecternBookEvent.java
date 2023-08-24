package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * This event is called when a player clicks the button to take a book of a Lectern.
 * If this event is cancelled the book remains on the lectern.
 */
public class PlayerTakeLecternBookEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityLectern lectern;

    public PlayerTakeLecternBookEvent(Player player, BlockEntityLectern lectern) {
        this.player = player;
        this.lectern = lectern;
    }

    /**
     * Gets the lectern involved.
     *
     * @return the Lectern
     */
    public BlockEntityLectern getLectern() {
        return lectern;
    }
}
