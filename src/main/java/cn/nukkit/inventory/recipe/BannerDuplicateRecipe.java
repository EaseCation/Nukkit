package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.item.ItemID;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class BannerDuplicateRecipe extends MultiRecipe {
    public BannerDuplicateRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.BANNER));
    }

    @Override
    public int getGridIngredientCount() {
        return 2;
    }

    @Override
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        if (repetitions < 1 || repetitions > 256 || inputs.size() != 2
                || !(inputs.getFirst() instanceof ItemBanner first) || !(inputs.getLast() instanceof ItemBanner second)
                || first.getCount() != repetitions || second.getCount() != repetitions
                || first.getDamage() != second.getDamage() || !first.isDefaultBanner() || !second.isDefaultBanner()) {
            return null;
        }
        ItemBanner pattern;
        if (first.getPatternsSize() > 0 && second.getPatternsSize() == 0) {
            pattern = first;
        } else if (second.getPatternsSize() > 0 && first.getPatternsSize() == 0) {
            pattern = second;
        } else {
            return null;
        }
        if (pattern.getPatternsSize() > 6) {
            return null;
        }
        return pattern.clone(repetitions * 2);
    }
}
