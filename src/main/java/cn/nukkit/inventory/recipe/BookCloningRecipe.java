package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.item.ItemID;

import java.util.List;
import java.util.UUID;

public final class BookCloningRecipe extends MultiRecipe {
    public BookCloningRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.WRITTEN_BOOK));
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions) {
        if (repetitions < 1 || repetitions > 256) {
            return List.of();
        }
        ItemBookWritten original = null;
        int blanks = 0;
        for (Item input : inputs) {
            if (input instanceof ItemBookWritten book && original == null && input.getCount() == repetitions) {
                original = book;
            } else if (input.is(ItemID.WRITABLE_BOOK) && input.getCount() > 0
                    && input.getCount() % repetitions == 0 && input.getCount() <= repetitions * 8 - blanks) {
                blanks += input.getCount();
            } else {
                return List.of();
            }
        }
        if (original == null || blanks == 0 || original.getGeneration() < ItemBookWritten.GENERATION_ORIGINAL
                || original.getGeneration() >= ItemBookWritten.GENERATION_COPY_OF_COPY) {
            return List.of();
        }
        ItemBookWritten copies = (ItemBookWritten) original.clone(blanks);
        copies.setGeneration(original.getGeneration() + 1);
        return List.of(copies, original.clone());
    }
}
