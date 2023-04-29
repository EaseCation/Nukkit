package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

@ToString
public class SmithingTrimRecipe implements Recipe {
    private final String recipeId;

    private final Item template;
    private final Item input;
    private final Item addition;

    private final RecipeTag tag;

    public SmithingTrimRecipe(Item template, Item input, Item addition, RecipeTag tag) {
        this(null, template, input, addition, tag);
    }

    public SmithingTrimRecipe(String recipeId, Item template, Item input, Item addition, RecipeTag tag) {
        this.recipeId = recipeId != null ? recipeId : UUID.randomUUID().toString();
        this.template = template.clone();
        this.input = input.clone();
        this.addition = addition.clone();
        this.tag = tag;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public Item getTemplate() {
        return this.template.clone();
    }

    public Item getInput() {
        return this.input.clone();
    }

    public Item getAddition() {
        return this.addition.clone();
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingTrimRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRIM;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }
}
