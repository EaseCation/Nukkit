package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public class SmithingTransformRecipe extends SmithingRecipe {
    private final UUID internalId = UUID.randomUUID();

    private final Item output;

    public SmithingTransformRecipe(Item result, RecipeIngredient template, RecipeIngredient input, RecipeIngredient addition, RecipeTag tag) {
        this(null, result, template, input, addition, tag);
    }

    public SmithingTransformRecipe(String recipeId, Item result, RecipeIngredient template, RecipeIngredient input, RecipeIngredient addition, RecipeTag tag) {
        super(recipeId, template, input, addition, tag);
        this.output = result;
    }

    public UUID getInternalId() {
        return this.internalId;
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public Item getResultUnsafe() {
        return output;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingTransformRecipe(this);
        manager.registerNetworkRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRANSFORM;
    }
}
