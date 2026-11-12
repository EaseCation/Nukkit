package cn.nukkit.inventory;

import cn.nukkit.item.Item;

public record FakeBlockRecipeResult(Item output, int inputCount, int materialCount, int cost, int experience) {
    public FakeBlockRecipeResult {
        output = output.clone();
    }

    @Override
    public Item output() {
        return output.clone();
    }
}
