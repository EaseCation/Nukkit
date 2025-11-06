package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class InvalidItemDescriptor implements ItemDescriptor {
    public static final InvalidItemDescriptor INSTANCE = new InvalidItemDescriptor();

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.NONE;
    }

    @Override
    public boolean accepts(Item item) {
        return item.isNull();
    }
}
