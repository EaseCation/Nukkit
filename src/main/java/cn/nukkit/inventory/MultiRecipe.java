package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.UUID;

public class MultiRecipe implements Recipe {

    private final UUID id;

    public MultiRecipe(UUID id) {
        this.id = id;
    }

    @Override
    public Item getResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerMultiRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.MULTI;
    }

    @Override
    public RecipeTag getTag() {
        throw new UnsupportedOperationException();
    }

    public UUID getId() {
        return this.id;
    }
}
