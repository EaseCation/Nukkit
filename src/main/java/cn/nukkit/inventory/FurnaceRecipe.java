package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class FurnaceRecipe implements Recipe {

    private final Item output;

    private Item ingredient;

    private final RecipeTag tag;

    public FurnaceRecipe(Item result, Item ingredient, RecipeTag tag) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.tag = tag;
    }

    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.FURNACE_DATA : RecipeType.FURNACE;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }
}
