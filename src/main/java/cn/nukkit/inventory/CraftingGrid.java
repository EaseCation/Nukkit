package cn.nukkit.inventory;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingGrid extends PlayerUIComponent {

    public static final int SIZE = 1 + CRAFTING_INVENTORY_INPUT_LAST -  CRAFTING_INVENTORY_INPUT_OFFSET;

    CraftingGrid(PlayerUIInventory playerUI) {
        this(playerUI, CRAFTING_INVENTORY_INPUT_OFFSET, SIZE);
    }

    protected CraftingGrid(PlayerUIInventory playerUI, int offset, int size) {
        super(playerUI, offset, size);
    }
}
