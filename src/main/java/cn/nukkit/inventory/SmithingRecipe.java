package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;

import java.util.UUID;

public abstract class SmithingRecipe implements Recipe {
    protected final String recipeId;

    protected final RecipeIngredient base;
    protected final RecipeIngredient ingredient;
    protected final RecipeIngredient template;

    protected final RecipeTag tag;
    protected int networkId;

    protected SmithingRecipe(RecipeIngredient template, RecipeIngredient base, RecipeIngredient ingredient, RecipeTag tag) {
        this(null, template, base, ingredient, tag);
    }

    protected SmithingRecipe(String recipeId, RecipeIngredient template, RecipeIngredient base, RecipeIngredient ingredient, RecipeTag tag) {
        this.recipeId = recipeId != null ? recipeId : UUID.randomUUID().toString();
        this.template = template;
        this.base = base;
        this.ingredient = ingredient;
        this.tag = tag;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public RecipeIngredient getBase() {
        return this.base;
    }

    public RecipeIngredient getIngredient() {
        return this.ingredient;
    }

    public RecipeIngredient getTemplate() {
        return this.template;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }
}
