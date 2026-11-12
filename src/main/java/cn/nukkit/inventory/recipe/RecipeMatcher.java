package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class RecipeMatcher {
    private RecipeMatcher() {
    }

    public static boolean matchIngredients(List<Item> providedItems, List<RecipeIngredient> ingredients, int repetitions) {
        if (repetitions <= 0) {
            return false;
        }
        int[] remaining = new int[providedItems.size()];
        long providedCount = 0;
        for (int index = 0; index < providedItems.size(); index++) {
            Item item = providedItems.get(index);
            if (!item.isNull()) {
                remaining[index] = item.getCount();
                providedCount += item.getCount();
            }
        }

        List<IngredientMatch> matches = new ArrayList<>(ingredients.size());
        long requiredCount = 0;
        for (RecipeIngredient ingredient : ingredients) {
            if (ingredient.getCount() < 0) {
                return false;
            }
            if (ingredient.getCount() == 0) {
                continue;
            }
            long count = (long) ingredient.getCount() * repetitions;
            requiredCount += count;
            if (requiredCount > providedCount) {
                return false;
            }
            IntSet acceptedItems = new IntOpenHashSet();
            for (int index = 0; index < providedItems.size(); index++) {
                if (remaining[index] > 0 && ingredient.accepts(providedItems.get(index))) {
                    acceptedItems.add(index);
                }
            }
            if (acceptedItems.isEmpty()) {
                return false;
            }
            matches.add(new IngredientMatch(count, acceptedItems));
        }
        if (requiredCount != providedCount) {
            return false;
        }

        // Most picky ingredients first - avoid picky ingredient getting their items stolen by wildcard ingredients
        // Augmenting paths may reassign earlier matches; ingredient tags need not form a containment hierarchy.
        matches.sort(Comparator.comparingInt(match -> match.acceptedItems().size()));
        long[][] assigned = new long[matches.size()][remaining.length];
        boolean[] visited = new boolean[matches.size()];
        for (int match = 0; match < matches.size(); match++) {
            long needed = matches.get(match).count();
            while (needed > 0) {
                Arrays.fill(visited, false);
                long taken = assign(match, needed, matches, remaining, assigned, visited);
                if (taken == 0) {
                    return false;
                }
                needed -= taken;
            }
        }
        return true;
    }

    private static long assign(int match, long needed, List<IngredientMatch> matches, int[] remaining, long[][] assigned, boolean[] visited) {
        if (visited[match]) {
            return 0;
        }
        visited[match] = true;
        IntSet accepted = matches.get(match).acceptedItems();
        for (int item = 0; item < remaining.length; item++) {
            if (accepted.contains(item) && remaining[item] > 0) {
                int taken = (int) Math.min(needed, remaining[item]);
                remaining[item] -= taken;
                assigned[match][item] += taken;
                return taken;
            }
        }
        for (int item = 0; item < remaining.length; item++) {
            if (!accepted.contains(item)) {
                continue;
            }
            for (int previous = 0; previous < matches.size(); previous++) {
                if (visited[previous] || assigned[previous][item] == 0) {
                    continue;
                }
                long moved = assign(previous, Math.min(needed, assigned[previous][item]), matches, remaining, assigned, visited);
                if (moved > 0) {
                    assigned[previous][item] -= moved;
                    assigned[match][item] += moved;
                    return moved;
                }
            }
        }
        return 0;
    }

    private record IngredientMatch(long count, IntSet acceptedItems) {
    }
}
