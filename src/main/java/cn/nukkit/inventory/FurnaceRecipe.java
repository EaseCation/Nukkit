package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class FurnaceRecipe implements Recipe {
    @Nullable
    private final String vanillaRecipeId;

    private String recipeId;

    private UUID uuid;

    private final Item output;

    private Item ingredient;

    private final int priority;

    private final RecipeTag tag;

    public FurnaceRecipe(Item result, Item ingredient, RecipeTag tag) {
        this(null, null, 0, result, ingredient, tag);
    }

    public FurnaceRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item result, Item ingredient, RecipeTag tag) {
        this.vanillaRecipeId = vanillaRecipeId;
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.tag = tag;
    }

    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.FURNACE_DATA : RecipeType.FURNACE;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }

    @Nullable
    public String getVanillaRecipeId() {
        return this.vanillaRecipeId;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;

        if (this.recipeId == null) {
            this.recipeId = getId().toString();
        }
    }

    public int getPriority() {
        return this.priority;
    }
}
