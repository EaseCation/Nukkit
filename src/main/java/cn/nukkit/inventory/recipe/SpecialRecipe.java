package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.RecipeType;
import cn.nukkit.item.Item;

import java.util.List;
import java.util.UUID;

public abstract class SpecialRecipe implements CraftingRecipe {
    protected final Item result;
    protected UUID uuid;

    SpecialRecipe(Item result) {
        this.result = result;
        uuid = UUID.randomUUID();
    }

    @Override
    public String getRecipeId() {
        return "";
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID id) {
        uuid = id;
    }

    @Override
    public boolean requiresCraftingTable() {
        return false;
    }

    @Override
    public List<Item> getExtraResults() {
        return List.of();
    }

    @Override
    public List<Item> getAllResults() {
        return null;
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList, int multiplier) {
        return false;
    }

    @Override
    public List<Item> getIngredientsAggregate() {
        return List.of();
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
    }

    @Override
    public RecipeType getType() {
        return RecipeType.MULTI;
    }

    @Override
    public RecipeTag getTag() {
        return RecipeTag.CRAFTING_TABLE;
    }

    public abstract boolean match(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag);
}
