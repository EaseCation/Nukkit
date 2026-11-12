package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.Collection;

public class ShapelessChemistryRecipe extends ShapelessRecipe {

    public ShapelessChemistryRecipe(Item result, Collection<RecipeIngredient> ingredients, RecipeTag tag) {
        super(result, ingredients, tag);
    }

    public ShapelessChemistryRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item result, Collection<RecipeIngredient> ingredients, RecipeTag tag) {
        super(vanillaRecipeId, recipeId, priority, result, ingredients, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS_CHEMISTRY;
    }
}
