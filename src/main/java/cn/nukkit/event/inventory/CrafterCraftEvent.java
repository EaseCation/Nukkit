package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.CrafterInventory;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.item.Item;

import java.util.List;

public class CrafterCraftEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final CraftingRecipe recipe;
    private final List<Item> results;

    public CrafterCraftEvent(CrafterInventory inventory, CraftingRecipe recipe, List<Item> results) {
        super(inventory);
        this.recipe = recipe;
        this.results = results.stream().map(Item::clone).toList();
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public List<Item> getResults() {
        return results.stream().map(Item::clone).toList();
    }
}
