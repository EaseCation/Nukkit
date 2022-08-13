package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = ENCHANTING_TABLE_INPUT - ENCHANTING_TABLE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = ENCHANTING_TABLE_LAPIS_LAZULI - ENCHANTING_TABLE_INPUT_OFFSET;

    public static final int SIZE = 1 + ENCHANTING_TABLE_INPUT_LAST - ENCHANTING_TABLE_INPUT_OFFSET;

    public EnchantInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ENCHANT_TABLE, ENCHANTING_TABLE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ENCHANT;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
        who.resetCraftingGridType();

        for (int i = 0; i < SIZE; i++) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }
    }

    public Item getInputSlot() {
        return this.getItem(INPUT_SLOT);
    }

    public Item getReagentSlot() {
        return this.getItem(INGREDIENT_SLOT);
    }

}
