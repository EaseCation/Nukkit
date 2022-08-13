package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AnvilInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = ANVIL_INPUT - ANVIL_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = ANVIL_INGREDIENT - ANVIL_INPUT_OFFSET;

    public static final int SIZE = 1 + ANVIL_INPUT_LAST - ANVIL_INPUT_OFFSET;

    private int cost;

    public AnvilInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ANVIL, ANVIL_INPUT_OFFSET, SIZE, position);
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

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    public Item getInputSlot() {
        return this.getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return this.getItem(INGREDIENT_SLOT);
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
