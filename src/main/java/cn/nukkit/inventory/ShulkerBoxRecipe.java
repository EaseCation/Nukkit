package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Collection;

public class ShulkerBoxRecipe extends ShapelessRecipe {

    public ShulkerBoxRecipe(Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(result, ingredients, tag);
    }

    public ShulkerBoxRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients, RecipeTag tag) {
        super(recipeId, priority, result, ingredients, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHULKER_BOX;
    }
}
