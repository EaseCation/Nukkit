package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * Represents when a player has an item repaired via the Mending enchantment.
 */
public class PlayerItemMendEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item item;
    private final EntityXPOrb xpOrb;
    private int repairAmount;

    public PlayerItemMendEvent(Player player, Item item, EntityXPOrb xpOrb, int repairAmount) {
        this.player = player;
        this.item = item;
        this.xpOrb = xpOrb;
        this.repairAmount = repairAmount;
    }

    /**
     * Get the item to be repaired.
     * This is not necessarily the item the player is holding.
     *
     * @return the item to be repaired
     */
    public Item getItem() {
        return item;
    }

    /**
     * Get the xp orb triggering the event.
     *
     * @return the xp orb
     */
    public EntityXPOrb getXpOrb() {
        return xpOrb;
    }

    /**
     * Get the amount the item is to be repaired.
     * The default value is twice the value of the consumed xp orb or the remaining damage left on the item, whichever is smaller.
     *
     * @return how much damage will be repaired by the xp orb
     */
    public int getRepairAmount() {
        return repairAmount;
    }

    /**
     * Set the amount the item will be repaired.
     * Half of this value will be subtracted from the xp orb which initiated this event.
     *
     * @param amount how much damage will be repaired on the item
     */
    public void setRepairAmount(int amount) {
        this.repairAmount = amount;
    }
}
