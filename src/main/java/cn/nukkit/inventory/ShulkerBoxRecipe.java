package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.Collection;

public class ShulkerBoxRecipe extends ShapelessRecipe {

    public ShulkerBoxRecipe(Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(result, ingredients, tag);
    }

    public ShulkerBoxRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(vanillaRecipeId, recipeId, priority, result, ingredients, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHULKER_BOX;
    }
}
