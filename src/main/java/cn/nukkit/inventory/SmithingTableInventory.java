package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class SmithingTableInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = SMITHING_TABLE_INPUT - SMITHING_TABLE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = SMITHING_TABLE_INGREDIENT - SMITHING_TABLE_INPUT_OFFSET;

    public static final int SIZE = 1 + SMITHING_TABLE_INPUT_LAST - SMITHING_TABLE_INPUT_OFFSET;

    public SmithingTableInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, SMITHING_TABLE_INPUT_OFFSET, SIZE, position);
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
            clear(i);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_SMITHING_TABLE;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    public Item getInputSlot() {
        return getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return getItem(INGREDIENT_SLOT);
    }
}
