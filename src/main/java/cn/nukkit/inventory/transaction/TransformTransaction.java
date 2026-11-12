package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.inventory.SmithingTransformRecipe;
import cn.nukkit.item.Item;

public class TransformTransaction extends SmithingTransaction {
    public TransformTransaction(Player source, SmithingTransformRecipe recipe, int repetitions, Item base, Item material, Item template) {
        super(source, recipe, repetitions, base, material, template);
    }

    @Override
    protected Item createOutput() {
        return SmithingTableInventory.getOutput((SmithingTransformRecipe) smithingRecipe, baseItem);
    }
}
