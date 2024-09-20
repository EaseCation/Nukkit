package cn.nukkit.inventory.recipe;

import lombok.ToString;

@ToString
public class RecipeIngredient {
    public static final RecipeIngredient EMPTY = new RecipeIngredient(InvalidItemDescriptor.INSTANCE, 0);

    private final ItemDescriptor descriptor;
    private final int count;

    public RecipeIngredient(ItemDescriptor descriptor, int count) {
        this.descriptor = descriptor;
        this.count = count;
    }

    public ItemDescriptor getDescriptor() {
        return descriptor;
    }

    public int getCount() {
        return count;
    }
}
