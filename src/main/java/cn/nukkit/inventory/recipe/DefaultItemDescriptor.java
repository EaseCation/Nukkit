package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class DefaultItemDescriptor implements ItemDescriptor {
    private final Item item;

    public DefaultItemDescriptor(Item item) {
        this.item = item;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.INTERNAL;
    }

    public Item getItem() {
        return item;
    }
}
