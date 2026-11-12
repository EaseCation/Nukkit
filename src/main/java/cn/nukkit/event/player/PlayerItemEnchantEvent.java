package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.transaction.EnchantTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentOption;

/**
 * Called when a player enchants an item using an enchanting table.
 */
public class PlayerItemEnchantEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EnchantTransaction transaction;
    private final EnchantmentOption option;
    private final Item inputItem;
    private final Item outputItem;
    private final int cost;

    public PlayerItemEnchantEvent(Player player, EnchantTransaction transaction, EnchantmentOption option, Item inputItem, Item outputItem, int cost) {
        this.player = player;
        this.transaction = transaction;
        this.option = option;
        this.inputItem = inputItem;
        this.outputItem = outputItem;
        this.cost = cost;
    }

    /**
     * Returns the inventory transaction involved in this enchant event.
     */
    public EnchantTransaction getTransaction() {
        return transaction;
    }

    /**
     * Returns the enchantment option used.
     */
    public EnchantmentOption getOption() {
        return option;
    }

    /**
     * Returns the item to be enchanted.
     */
    public Item getInputItem() {
        return inputItem;
    }

    /**
     * Returns the enchanted item.
     */
    public Item getOutputItem() {
        return outputItem;
    }

    /**
     * Returns the number of XP levels and lapis that will be subtracted after enchanting
     * if the player is not in creative mode.
     */
    public int getCost() {
        return cost;
    }
}
