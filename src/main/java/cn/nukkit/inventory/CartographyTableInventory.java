package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemMap;
import cn.nukkit.inventory.recipe.MapRecipe;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nullable;
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
    public RecipeTag getRecipeTag() {
        return RecipeTag.CARTOGRAPHY_TABLE;
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

        if (who.isServerAuthoritativeInventoryEnabled()) {
            return;
        }

        for (int i = 0; i < SIZE; ++i) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }

        who.resetCraftingGridType();
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

    @Nullable
    public Item getOutput(@Nullable Recipe recipe, @Nullable String newName) {
        FakeBlockRecipeResult result = getResult(getInputSlot(), getMaterialSlot(), recipe, newName);
        return result != null ? result.output() : null;
    }

    @Nullable
    public static FakeBlockRecipeResult getResult(Item input, Item material, @Nullable Recipe recipe, @Nullable String newName) {
        if (isSingleMapInput(input, material)) {
            recipe = null;
        }
        if (!shouldSwapInputs(input, material, recipe)) {
            return getResultForInputs(input, material, recipe, newName);
        }
        FakeBlockRecipeResult result = getResultForInputs(material, input, recipe, newName);
        return result == null ? null : new FakeBlockRecipeResult(result.output(), result.materialCount(), result.inputCount(), result.cost(), result.experience());
    }

    public static boolean isSingleMapInput(Item input, Item material) {
        return !input.isNull() && input.isMap() && material.isNull()
                || input.isNull() && !material.isNull() && material.isMap();
    }

    private static boolean shouldSwapInputs(Item input, Item material, @Nullable Recipe recipe) {
        if (input.isNull()) {
            return !material.isNull();
        }
        return recipe instanceof MapRecipe && material.isMap() && (!input.isMap() || material instanceof ItemMap && !(input instanceof ItemMap));
    }

    @Nullable
    private static FakeBlockRecipeResult getResultForInputs(Item input, Item material, @Nullable Recipe recipe, @Nullable String newName) {
        if (newName != null && newName.length() > 30 || input.isNull()) {
            return null;
        }
        if (recipe instanceof MultiRecipe multiRecipe && !multiRecipe.isSupported()) {
            return null;
        }
        Item output;
        int materialCount = 0;
        if (recipe == null) {
            if (!input.isMap() || !material.isNull()) {
                return null;
            }
            output = input.clone();
            output.setCount(1);
        } else {
            if (recipe.getTag() != RecipeTag.CARTOGRAPHY_TABLE) {
                return null;
            }
            if (recipe instanceof MapRecipe mapRecipe) {
                output = mapRecipe.getResult(input, material, 1);
                materialCount = 1;
                if (output == null) {
                    return null;
                }
            } else if (recipe instanceof CraftingRecipe craftingRecipe) {
                List<Item> ingredients = new ObjectArrayList<>(2);
                Item inputUnit = input.clone();
                inputUnit.setCount(1);
                ingredients.add(inputUnit);
                if (!material.isNull()) {
                    Item materialUnit = material.clone();
                    materialUnit.setCount(1);
                    ingredients.add(materialUnit);
                    materialCount = 1;
                }
                if (!craftingRecipe.matchItems(ingredients, List.of()) || !craftingRecipe.getResultUnsafe().isMap()
                        || !craftingRecipe.getExtraResults().isEmpty()) {
                    return null;
                }
                output = craftingRecipe.getResult();
            } else {
                return null;
            }
        }
        FakeBlockRecipeHelper.applyCustomName(output, newName);
        return new FakeBlockRecipeResult(output, 1, materialCount, 0, 0);
    }

    public static boolean matchesResult(Item input, Item material, @Nullable Recipe recipe, @Nullable String name, FakeBlockRecipeResult result) {
        if (name != null && name.length() > 30 || result.cost() != 0 || result.experience() != 0) {
            return false;
        }
        if (recipe instanceof MapRecipe mapRecipe && !isSingleMapInput(input, material)) {
            boolean swapped = shouldSwapInputs(input, material, recipe);
            return recipe.getTag() == RecipeTag.CARTOGRAPHY_TABLE && result.inputCount() == 1 && result.materialCount() == 1
                    && mapRecipe.matchesResult(swapped ? material : input, swapped ? input : material, result.output(), name);
        }
        FakeBlockRecipeResult expected = getResult(input, material, recipe, name);
        return expected != null && expected.inputCount() == result.inputCount() && expected.materialCount() == result.materialCount()
                && expected.output().equalsExact(result.output());
    }
}
