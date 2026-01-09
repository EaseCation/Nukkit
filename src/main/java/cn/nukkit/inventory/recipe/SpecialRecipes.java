package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpecialRecipes {
    private static final Long2ObjectMap<SpecialRecipe> RECIPES = new Long2ObjectOpenHashMap<>();
    private static final Map<UUID, SpecialRecipe> BY_ID = new HashMap<>();

    @Nullable
    public static SpecialRecipe getRecipe(UUID id) {
        return BY_ID.get(id);
    }

    @Nullable
    public static SpecialRecipe match(long outputHash, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, RecipeTag tag) {
        SpecialRecipe specialRecipe = RECIPES.get(outputHash);
        if (specialRecipe != null && specialRecipe.match(inputList, primaryOutput, extraOutputList, tag)) {
            return specialRecipe;
        }
        return null;
    }

    private static void register(UUID uuid, SpecialRecipe recipe) {
        RECIPES.put(recipe.getResult().asHash(), recipe);
        BY_ID.put(uuid, recipe);
    }

    public static void registerRecipes() {
    }

    static {
        register(UUID.fromString("685a742a-c42e-4a4e-88ea-5eb83fc98e5b"), new DecoratedPotRecipe());
    }

    private SpecialRecipes() {
        throw new IllegalStateException();
    }
}
