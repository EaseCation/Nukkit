package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.List;
import java.util.Map;

public class ShapedChemistryRecipe extends ShapedRecipe {

    public ShapedChemistryRecipe(Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults, RecipeTag tag) {
        super(primaryResult, shape, ingredients, extraResults, tag);
    }

    public ShapedChemistryRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults, RecipeTag tag) {
        super(recipeId, priority, primaryResult, shape, ingredients, extraResults, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED_CHEMISTRY;
    }
}
