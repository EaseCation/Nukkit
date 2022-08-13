package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Collection;

public class ShapelessChemistryRecipe extends ShapelessRecipe {

    public ShapelessChemistryRecipe(Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(result, ingredients, tag);
    }

    public ShapelessChemistryRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(recipeId, priority, result, ingredients, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS_CHEMISTRY;
    }
}
