package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import lombok.ToString;

@ToString(callSuper = true)
public class SmithingTrimRecipe extends SmithingRecipe {
    public SmithingTrimRecipe(RecipeIngredient template, RecipeIngredient input, RecipeIngredient addition, RecipeTag tag) {
        this(null, template, input, addition, tag);
    }

    public SmithingTrimRecipe(String recipeId, RecipeIngredient template, RecipeIngredient input, RecipeIngredient addition, RecipeTag tag) {
        super(recipeId, template, input, addition, tag);
    }

    @Override
    public Item getResult() {
        return Items.air();
    }

    @Override
    public Item getResultUnsafe() {
        return Items.air();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingTrimRecipe(this);
        manager.registerNetworkRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRIM;
    }
}
