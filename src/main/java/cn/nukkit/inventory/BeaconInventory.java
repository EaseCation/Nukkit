package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: Rover656
 */
public class BeaconInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = BEACON_PAYMENT - BEACON_INPUT_OFFSET;
    public static final int SIZE = 1 + BEACON_INPUT_LAST - BEACON_INPUT_OFFSET;

    public BeaconInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.BEACON, BEACON_INPUT_OFFSET, SIZE, position);
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
        this.clear(INPUT_SLOT);
    }
}
