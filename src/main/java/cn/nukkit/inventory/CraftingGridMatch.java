package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.List;

public record CraftingGridMatch(CraftingRecipe recipe, int[] consumed, List<Item> results) {
    public CraftingGridMatch {
        consumed = consumed.clone();
        results = results.stream().map(Item::clone).toList();
    }

    @Override
    public int[] consumed() {
        return consumed.clone();
    }

    @Override
    public List<Item> results() {
        return results.stream().map(Item::clone).toList();
    }
}
