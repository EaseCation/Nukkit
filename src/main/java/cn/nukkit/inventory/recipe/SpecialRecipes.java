package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.List;

public final class SpecialRecipes {
    private static final Long2ObjectMap<SpecialRecipe> RECIPES = new Long2ObjectOpenHashMap<>();

    @Nullable
    public static SpecialRecipe match(long outputHash, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag) {
        SpecialRecipe specialRecipe = RECIPES.get(outputHash);
        if (specialRecipe != null && specialRecipe.match(inputList, primaryOutput, extraOutputList, tag)) {
            return specialRecipe;
        }
        return null;
    }

    private static void register(SpecialRecipe recipe) {
        RECIPES.put(recipe.getResult().asHash(), recipe);
    }

    public static void registerRecipes() {
    }

    static {
        register(new DecoratedPotRecipe());
    }

    private SpecialRecipes() {
        throw new IllegalStateException();
    }
}
