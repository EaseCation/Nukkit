package cn.nukkit.inventory;

import cn.nukkit.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface InventoryHolder {

    Inventory getInventory();

    default int openInventory(Player player) {
        return player.addWindow(getInventory());
    }
}
