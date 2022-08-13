package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class StonecutterInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = STONECUTTER_INPUT - STONECUTTER_INPUT_OFFSET;

    public static final int SIZE = 1 + STONECUTTER_INPUT_LAST - STONECUTTER_INPUT_OFFSET;

    public StonecutterInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.STONECUTTER, STONECUTTER_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_STONECUTTER;
        who.recipeTag = RecipeTag.STONECUTTER;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
        who.resetCraftingGridType();

        for (Item drop : who.getInventory().addItem(getItem(INPUT_SLOT))) {
            who.dropItem(drop);
        }
        clear(INPUT_SLOT);
    }

    public Item getInputSlot() {
        return getItem(INPUT_SLOT);
    }
}
