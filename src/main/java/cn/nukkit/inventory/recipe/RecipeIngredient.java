package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import lombok.ToString;

@ToString
public class RecipeIngredient {
    public static final RecipeIngredient EMPTY = new RecipeIngredient(InvalidItemDescriptor.INSTANCE, 0);

    private final ItemDescriptor descriptor;
    private final int count;

    public RecipeIngredient(Item item) {
        this(new DefaultItemDescriptor(item), item.getCount());
    }

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

    public boolean accepts(Item item) {
        return descriptor.accepts(item);
    }
}
