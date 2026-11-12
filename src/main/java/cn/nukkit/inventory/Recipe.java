package cn.nukkit.inventory;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Recipe {

    Item getResult();

    Item getResultUnsafe();

    void registerToCraftingManager(CraftingManager manager);

    RecipeType getType();

    RecipeTag getTag();

    int getNetworkId();

    void setNetworkId(int networkId);
}
