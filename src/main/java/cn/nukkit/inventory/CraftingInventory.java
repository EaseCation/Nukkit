package cn.nukkit.inventory;

import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class CraftingInventory extends FakeBlockUIComponent {
    public static final int SIZE = 1 + CRAFTING_TABLE_INPUT_LAST - CRAFTING_TABLE_INPUT_OFFSET;

    public CraftingInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.WORKBENCH, CRAFTING_TABLE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public RecipeTag getRecipeTag() {
        return RecipeTag.CRAFTING_TABLE;
    }
}
