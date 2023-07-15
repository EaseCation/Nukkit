package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * @author CreeperFace
 */
public class PlayerCursorInventory extends PlayerUIComponent {
    private final PlayerUIInventory playerUI;

    PlayerCursorInventory(PlayerUIInventory playerUI) {
        super(playerUI, CURSOR, 1);
        this.playerUI = playerUI;
    }

    /**
     * This override is here for documentation and code completion purposes only.
     *
     * @return Player
     */
    @Override
    public Player getHolder() {
        return playerUI.getHolder();
    }

    public Item getItem() {
        return getItem(0);
    }

    public void setItem(Item item) {
        setItem(0, item);
    }
}
