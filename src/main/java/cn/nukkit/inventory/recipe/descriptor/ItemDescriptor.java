package cn.nukkit.inventory.recipe.descriptor;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ItemDescriptorType;

public interface ItemDescriptor {
    ItemDescriptorType getType();

    boolean accepts(Item item);
}
