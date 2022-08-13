package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.List;

/**
 * Chemistry feature.
 */
public class MaterialReducerRecipe implements Recipe {

    private final Item input;
    private final List<Item> outputs; // 9 element slots

    public MaterialReducerRecipe(Item input, List<Item> outputs) {
        this.input = input;
        this.outputs = outputs;
    }

    @Override
    public Item getResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerMaterialReducerRecipe(this);
    }

    @Override
    public RecipeType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeTag getTag() {
        throw new UnsupportedOperationException();
    }

    public Item getInput() {
        return input.clone();
    }

    public List<Item> getOutputs() {
        return outputs;
    }
}
