package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

@ToString
public class SmithingTransformRecipe implements Recipe {
    private final UUID internalId;
    private final String recipeId;

    private final Item output;
    private final Item template;
    private final Item input;
    private final Item addition;

    private final RecipeTag tag;

    public SmithingTransformRecipe(Item result, Item template, Item input, Item addition, RecipeTag tag) {
        this(null, result, template, input, addition, tag);
    }

    public SmithingTransformRecipe(String recipeId, Item result, Item template, Item input, Item addition, RecipeTag tag) {
        internalId = UUID.randomUUID();
        this.recipeId = recipeId != null ? recipeId : internalId.toString();
        this.output = result.clone();
        this.template = template.clone();
        this.input = input.clone();
        this.addition = addition.clone();
        this.tag = tag;
    }

    public UUID getInternalId() {
        return this.internalId;
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
