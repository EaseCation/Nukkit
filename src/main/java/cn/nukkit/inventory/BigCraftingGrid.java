package cn.nukkit.inventory;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * @author CreeperFace
 */
public class BigCraftingGrid extends CraftingGrid {

    public static final int SIZE = 1 + CRAFTING_TABLE_INPUT_LAST -  CRAFTING_TABLE_INPUT_OFFSET;

    BigCraftingGrid(PlayerUIInventory playerUI) {
        super(playerUI, CRAFTING_TABLE_INPUT_OFFSET, SIZE);
    }
}
