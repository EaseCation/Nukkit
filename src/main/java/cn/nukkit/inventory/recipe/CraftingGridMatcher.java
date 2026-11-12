package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.CraftingGridMatch;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.RecipeType;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.inventory.ShapelessUserDataRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.inventory.transaction.FakeBlockTransactionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class CraftingGridMatcher {
    private CraftingGridMatcher() {
    }

    @Nullable
    public static CraftingGridMatch match(CraftingRecipe recipe, Item[] grid, int disabledSlots) {
        if (grid.length != 9 || recipe.getTag() != RecipeTag.CRAFTING_TABLE) {
            return null;
        }
        for (int slot = 0; slot < grid.length; slot++) {
            if (grid[slot] == null || grid[slot].getCount() < 0 || (disabledSlots & 1 << slot) != 0 && !grid[slot].isNull()) {
                return null;
            }
        }
        int[] consumed = null;
        List<Item> results;
        if (recipe instanceof ShapedRecipe shaped && recipe.getType() == RecipeType.SHAPED) {
            for (int y = 0; y <= 3 - shaped.getHeight() && consumed == null; y++) {
                for (int x = 0; x <= 3 - shaped.getWidth() && consumed == null; x++) {
                    consumed = matchShaped(shaped, grid, x, y, false);
                    if (consumed == null && shaped.isAssumeSymmetry()) {
                        consumed = matchShaped(shaped, grid, x, y, true);
                    }
                }
            }
            results = shaped.getAllResults();
        } else if (recipe instanceof ShapelessRecipe shapeless && (recipe.getType() == RecipeType.SHAPELESS
                || recipe.getType() == RecipeType.SHAPELESS_USER_DATA)) {
            consumed = matchShapeless(shapeless, grid);
            results = shapeless.getAllResults();
            if (consumed != null && shapeless instanceof ShapelessUserDataRecipe userDataRecipe) {
                List<Item> ingredients = new ArrayList<>();
                for (int slot = 0; slot < grid.length; slot++) {
                    if (consumed[slot] > 0) {
                        ingredients.add(grid[slot].clone(consumed[slot]));
                    }
                }
                Item output = userDataRecipe.getResult(ingredients, 1);
                if (output == null) {
                    return null;
                }
                results = new ArrayList<>(1 + userDataRecipe.getExtraResults().size());
                results.add(output);
                results.addAll(userDataRecipe.getExtraResults());
            }
        } else if (recipe instanceof MultiRecipe multi && multi.isSupported()) {
            List<Item> outputs = multi.getResultsForGrid(grid);
            if (outputs.isEmpty()) {
                return null;
            }
            consumed = new int[9];
            for (int slot = 0; slot < 9; slot++) {
                consumed[slot] = grid[slot].isNull() ? 0 : 1;
            }
            results = outputs;
        } else {
            return null;
        }
        if (consumed == null || results.isEmpty() || results.stream().anyMatch(item -> item == null || item.isNull())) {
            return null;
        }
        return new CraftingGridMatch(recipe, consumed, results);
    }

    public static boolean verify(CraftingGridMatch cached, Item[] grid, int disabledSlots) {
        CraftingRecipe recipe = cached.recipe();
        if (!(recipe instanceof MultiRecipe multi)) {
            CraftingGridMatch current = match(recipe, grid, disabledSlots);
            return current != null && Arrays.equals(cached.consumed(), current.consumed())
                    && FakeBlockTransactionHelper.matchesItems(cached.results(), current.results());
        }
        int[] consumed = cached.consumed();
        List<Item> results = cached.results();
        if (!multi.isSupported() || grid.length != 9 || consumed.length != 9 || recipe.getTag() != RecipeTag.CRAFTING_TABLE
                || results.isEmpty() || results.stream().anyMatch(item -> item == null || item.isNull())) {
            return false;
        }
        List<Item> inputs = new ArrayList<>();
        for (int slot = 0; slot < grid.length; slot++) {
            Item item = grid[slot];
            if (item == null || item.getCount() < 0 || (disabledSlots & 1 << slot) != 0 && !item.isNull()
                    || consumed[slot] != (item.isNull() ? 0 : 1)) {
                return false;
            }
            if (!item.isNull()) {
                inputs.add(item.clone(1));
            }
        }
        // 动态产物可能分配新地图标识，只验证缓存结果，不能再次生成。
        return multi.match(inputs, results, 1, grid);
    }

    @Nullable
    private static int[] matchShaped(ShapedRecipe recipe, Item[] grid, int offsetX, int offsetY, boolean mirror) {
        int[] consumed = new int[9];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int slot = y * 3 + x;
                int recipeX = x - offsetX;
                int recipeY = y - offsetY;
                RecipeIngredient ingredient = recipeX >= 0 && recipeX < recipe.getWidth()
                        && recipeY >= 0 && recipeY < recipe.getHeight()
                        ? recipe.getIngredient(mirror ? recipe.getWidth() - 1 - recipeX : recipeX, recipeY)
                        : RecipeIngredient.EMPTY;
                if (ingredient.getCount() == 0) {
                    if (!grid[slot].isNull()) {
                        return null;
                    }
                } else if (ingredient.getCount() < 0 || grid[slot].getCount() < ingredient.getCount() || !ingredient.accepts(grid[slot])) {
                    return null;
                }
                consumed[slot] = ingredient.getCount();
            }
        }
        return consumed;
    }

    @Nullable
    private static int[] matchShapeless(ShapelessRecipe recipe, Item[] grid) {
        List<IngredientSlots> candidates = new ArrayList<>();
        int occupied = 0;
        for (Item item : grid) {
            if (!item.isNull()) {
                occupied++;
            }
        }
        for (RecipeIngredient ingredient : recipe.getIngredientList()) {
            if (ingredient.getCount() <= 0) {
                return null;
            }
            int mask = 0;
            for (int slot = 0; slot < grid.length; slot++) {
                if (grid[slot].getCount() >= ingredient.getCount() && ingredient.accepts(grid[slot])) {
                    mask |= 1 << slot;
                }
            }
            if (mask == 0) {
                return null;
            }
            candidates.add(new IngredientSlots(ingredient.getCount(), mask));
        }
        if (candidates.isEmpty() || candidates.size() != occupied) {
            return null;
        }
        candidates.sort(Comparator.comparingInt(candidate -> Integer.bitCount(candidate.slots())));
        int[] consumed = new int[9];
        return assign(candidates, 0, 0, consumed) ? consumed : null;
    }

    private static boolean assign(List<IngredientSlots> candidates, int index, int used, int[] consumed) {
        if (index == candidates.size()) {
            return true;
        }
        IngredientSlots candidate = candidates.get(index);
        int available = candidate.slots() & ~used;
        while (available != 0) {
            int slot = Integer.numberOfTrailingZeros(available);
            available &= ~(1 << slot);
            consumed[slot] = candidate.count();
            if (assign(candidates, index + 1, used | 1 << slot, consumed)) {
                return true;
            }
            consumed[slot] = 0;
        }
        return false;
    }

    private record IngredientSlots(int count, int slots) {
    }
}
