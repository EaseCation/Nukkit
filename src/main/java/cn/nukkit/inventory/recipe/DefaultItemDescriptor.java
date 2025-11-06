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

    @Override
    public boolean accepts(Item item) {
        if (item.getCount() < 1) {
            return false;
        }
        return this.item.equals(item, this.item.hasMeta(), this.item.hasCompoundTag());
    }

    public Item getItem() {
        return item;
    }
}
