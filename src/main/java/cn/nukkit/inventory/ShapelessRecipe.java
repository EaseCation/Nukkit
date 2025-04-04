package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ShapelessRecipe implements CraftingRecipe {
    @Nullable
    private final String vanillaRecipeId;

    private String recipeId;

    private final Item output;

    private long least, most;

    private final List<Item> ingredients;
    private final List<Item> ingredientsAggregate;

    private final int priority;

    private final RecipeTag tag;

    public ShapelessRecipe(Item result, Collection<Item> ingredients, RecipeTag tag) {
        this(null, null, 10, result, ingredients, tag);
    }

    public ShapelessRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item result, Collection<Item> ingredients, RecipeTag tag) {
        this.vanillaRecipeId = vanillaRecipeId;
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        this.ingredients = new ObjectArrayList<>();
        this.ingredientsAggregate = new ObjectArrayList<>();

        for (Item item : ingredients) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            boolean found = false;
            for (Item existingIngredient : this.ingredientsAggregate) {
                if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingIngredient.setCount(existingIngredient.getCount() + item.getCount());
                    found = true;
                    break;
                }
            }
            if (!found)
                this.ingredientsAggregate.add(item.clone());
            this.ingredients.add(item.clone());
        }

        this.ingredientsAggregate.sort(CraftingManager.recipeComparator);

        this.tag = tag;
    }

    @Nullable
    public String getVanillaRecipeId() {
        return this.vanillaRecipeId;
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public String getRecipeId() {
        return this.recipeId;
    }

    @Override
    public UUID getId() {
        return new UUID(least, most);
    }

    @Override
    public void setId(UUID uuid) {
        this.least = uuid.getLeastSignificantBits();
        this.most = uuid.getMostSignificantBits();

        if (this.recipeId == null) {
            this.recipeId = this.getId().toString();
        }
    }

    public List<Item> getIngredientList() {
        List<Item> ingredients = new ObjectArrayList<>();
        for (Item ingredient : this.ingredients) {
            ingredients.add(ingredient.clone());
        }

        return ingredients;
    }

    public int getIngredientCount() {
        return ingredients.size();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapelessRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.ingredients.size() > 4;
    }

    @Override
    public List<Item> getExtraResults() {
        return new ObjectArrayList<>();
    }

    @Override
    public List<Item> getAllResults() {
        return null;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList, int multiplier) {
        List<Item> haveInputs = new ObjectArrayList<>();
        for (Item item : inputList) {
            if (item.isNull())
                continue;
            haveInputs.add(item.clone());
        }
        List<Item> needInputs = new ObjectArrayList<>();
        if (multiplier != 1) {
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needInputs.add(itemClone);
            }
        } else {
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                needInputs.add(item.clone());
            }
        }

        if (!matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new ObjectArrayList<>();
        for (Item item : extraOutputList) {
            if (item.isNull())
                continue;
            haveOutputs.add(item.clone());
        }
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = new ObjectArrayList<>();
        if (multiplier != 1) {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needOutputs.add(itemClone);
            }
        } else {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                needOutputs.add(item.clone());
            }
        }
        needOutputs.sort(CraftingManager.recipeComparator);

        return this.matchItemList(haveOutputs, needOutputs);
    }

    private boolean matchItemList(List<Item> haveItems, List<Item> needItems) {
        for (Item needItem : new ObjectArrayList<>(needItems)) {
            for (Item haveItem : new ObjectArrayList<>(haveItems)) {
                if (needItem.equals(haveItem, needItem.hasMeta(), needItem.hasCompoundTag())) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItems.remove(haveItem);
                    }
                    if (needItem.getCount() == 0) {
                        needItems.remove(needItem);
                        break;
                    }
                }
            }
        }
        return haveItems.isEmpty() && needItems.isEmpty();
    }

    @Override
    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }
}
