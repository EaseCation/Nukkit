package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.inventory.SmithingTrimRecipe;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

public class TrimTransaction extends SmithingTransaction {
    public TrimTransaction(Player source, SmithingTrimRecipe recipe, int repetitions, Item base, Item material, Item template) {
        super(source, recipe, repetitions, base, material, template);
    }

    @Override
    @Nullable
    protected Item createOutput() {
        return SmithingTableInventory.getOutput((SmithingTrimRecipe) smithingRecipe, baseItem, materialItem, templateItem);
    }
}
