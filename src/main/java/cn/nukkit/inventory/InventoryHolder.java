package cn.nukkit.inventory;

import cn.nukkit.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface InventoryHolder {

    Inventory getInventory();

    default void openInventory(Player player) {
        player.addWindow(getInventory());
    }
}
