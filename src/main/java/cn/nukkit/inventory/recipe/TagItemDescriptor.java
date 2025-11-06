package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class TagItemDescriptor implements ItemDescriptor {
    private final String tag;

    public TagItemDescriptor(String tag) {
        this.tag = tag;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.ITEM_TAG;
    }

    @Override
    public boolean accepts(Item item) {
        if (item.getCount() < 1) {
            return false;
        }
        return item.hasItemTag(tag);
    }

    public String getTag() {
        return tag;
    }
}
