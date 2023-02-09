package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

@ToString
public class SmithingTransformRecipe implements Recipe {
    private final String recipeId;

    private final Item output;
    private final Item input;
    private final Item addition;

    private final RecipeTag tag;

    public SmithingTransformRecipe(Item result, Item input, Item addition, RecipeTag tag) {
        this(null, result, input, addition, tag);
    }

    public SmithingTransformRecipe(String recipeId, Item result, Item input, Item addition, RecipeTag tag) {
        this.recipeId = recipeId != null ? recipeId : UUID.randomUUID().toString();
        this.output = result.clone();
        this.input = input.clone();
        this.addition = addition.clone();
        this.tag = tag;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public Item getInput() {
        return this.input.clone();
    }

    public Item getAddition() {
        return this.addition.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingTransformRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRANSFORM;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }
}
