package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

/**
 * Called when a players experience changes naturally
 */
public class PlayerExpChangeEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int exp;

    public PlayerExpChangeEvent(final Player player, final int expAmount) {
        this.player = player;
        exp = expAmount;
    }

    /**
     * Get the amount of experience the player will receive
     *
     * @return The amount of experience
     */
    public int getAmount() {
        return exp;
    }

    /**
     * Set the amount of experience the player will receive
     *
     * @param amount The amount of experience to set
     */
    public void setAmount(int amount) {
        exp = amount;
    }

}
