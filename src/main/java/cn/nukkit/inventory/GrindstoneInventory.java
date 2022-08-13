package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class GrindstoneInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = GRINDSTONE_INPUT - GRINDSTONE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = GRINDSTONE_INGREDIENT - GRINDSTONE_INPUT_OFFSET;

    public static final int SIZE = 1 + GRINDSTONE_INPUT_LAST - GRINDSTONE_INPUT_OFFSET;

    public GrindstoneInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.GRINDSTONE, GRINDSTONE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_GRINDSTONE;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
        who.resetCraftingGridType();

        for (int i = 0; i < SIZE; ++i) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            this.clear(i);
        }
    }

    public Item getInputSlot() {
        return getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return getItem(INGREDIENT_SLOT);
    }
}
