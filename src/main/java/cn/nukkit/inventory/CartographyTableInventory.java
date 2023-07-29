package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class CartographyTableInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = CARTOGRAPHY_TABLE_INPUT - CARTOGRAPHY_TABLE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = CARTOGRAPHY_TABLE_INGREDIENT - CARTOGRAPHY_TABLE_INPUT_OFFSET;

    public static final int SIZE = 1 + CARTOGRAPHY_TABLE_INPUT_LAST - CARTOGRAPHY_TABLE_INPUT_OFFSET;

    public CartographyTableInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.CARTOGRAPHY, CARTOGRAPHY_TABLE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_CARTOGRAPHY;
        who.recipeTag = RecipeTag.CARTOGRAPHY_TABLE;
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
    public boolean onTakeResult(Player player, Item result) {
        List<Item> inputs = new ObjectArrayList<>(2);

        Item input = getInputSlot();
        if (!input.isNull()) {
            inputs.add(input);
        }
        Item material = getMaterialSlot();
        if (!material.isNull()) {
            inputs.add(material);
        }

        if (inputs.isEmpty()) {
            return false;
        }

        // Requires special handling to keep map data
//        CraftingRecipe recipe = player.getServer().getCraftingManager().matchRecipe(inputs, result, Collections.emptyList(), RecipeTag.CARTOGRAPHY_TABLE);
//        if (recipe == null) {
//            return false;
//        }

        int outputId = result.getId();
        if (outputId != ItemID.EMPTY_MAP && outputId != ItemID.FILLED_MAP) {
            return false;
        }

        if (!input.isNull() && !isValidInput(input) || !material.isNull() && !isValidInput(material)) {
            return false;
        }

        //TODO: more check

        return true;
    }

    @Override
    public void postTakeResultResolve(Player player) {
        pop(INPUT_SLOT);
        pop(INGREDIENT_SLOT);
    }

    private static boolean isValidInput(Item item) {
        int id = item.getId();
        return id == ItemID.EMPTY_MAP || id == ItemID.FILLED_MAP || id == ItemID.PAPER || id == ItemID.COMPASS || id == Block.getItemId(BlockID.GLASS_PANE);
    }

    public Item getInputSlot() {
        return getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return getItem(INGREDIENT_SLOT);
    }
}
