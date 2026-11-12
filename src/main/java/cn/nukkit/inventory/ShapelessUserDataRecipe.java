package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.inventory.recipe.RecipeMatcher;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

public class ShapelessUserDataRecipe extends ShapelessRecipe {

    public ShapelessUserDataRecipe(Item result, Collection<RecipeIngredient> ingredients, RecipeTag tag) {
        super(result, ingredients, tag);
    }

    public ShapelessUserDataRecipe(@Nullable String vanillaRecipeId, String recipeId, int priority, Item result, Collection<RecipeIngredient> ingredients, RecipeTag tag) {
        super(vanillaRecipeId, recipeId, priority, result, ingredients, tag);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS_USER_DATA;
    }

    public boolean matchUserData(Item primaryOutput, List<Item> inputList) {
        if (getResultUnsafe().isBundle() || getResultUnsafe().isShulkerBox()) {
            Item source = findContainerSource(inputList);
            return source != null && hasSameUserData(primaryOutput, source);
        }
        for (Item inputItem : inputList) {
            if (hasSameUserData(primaryOutput, inputItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 染色仅改变容器种类，产物继承实际容器输入的完整数据，不能从染料复制数据。
     */
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        if (repetitions <= 0 || !RecipeMatcher.matchIngredients(inputs, getIngredientsAggregate(), repetitions)) {
            return null;
        }
        Item source = findContainerSource(inputs);
        if (source == null) {
            return null;
        }
        long sourceCount = 0;
        for (Item input : inputs) {
            if (isContainerSource(input)) {
                sourceCount += input.getCount();
            }
        }
        long outputCount = (long) getResultUnsafe().getCount() * repetitions;
        if (sourceCount != outputCount || outputCount > Integer.MAX_VALUE) {
            return null;
        }
        Item output = getResult();
        CompoundTag tag = source.getNamedTag();
        if (tag == null) {
            output.clearNamedTag();
        } else {
            output.setNamedTag(tag.clone());
        }
        Set<String> canPlace = source.getCanPlaceOnBlocks();
        Set<String> canDestroy = source.getCanDestroyBlocks();
        output.setCanPlaceOnBlocks(canPlace == null ? null : new HashSet<>(canPlace));
        output.setCanDestroyBlocks(canDestroy == null ? null : new HashSet<>(canDestroy));
        output.setCount((int) outputCount);
        return output;
    }

    @Nullable
    private Item findContainerSource(List<Item> inputs) {
        Item source = null;
        for (Item input : inputs) {
            if (!isContainerSource(input)) {
                continue;
            }
            if (source != null && !hasSameUserData(source, input)) {
                return null;
            }
            source = input;
        }
        return source;
    }

    private boolean isContainerSource(Item input) {
        Item result = getResultUnsafe();
        if (input.isNull() || !(result.isBundle() && input.isBundle()
                || result.isShulkerBox() && input.isShulkerBox())) {
            return false;
        }
        for (RecipeIngredient ingredient : getIngredientsAggregate()) {
            if (ingredient.getCount() > 0 && ingredient.accepts(input)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSameUserData(Item first, Item second) {
        return Objects.equals(first.getNamedTag(), second.getNamedTag())
                && Objects.equals(first.getCanPlaceOnBlocks(), second.getCanPlaceOnBlocks())
                && Objects.equals(first.getCanDestroyBlocks(), second.getCanDestroyBlocks());
    }
}
