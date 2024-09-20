package cn.nukkit.inventory.recipe;

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

    public String getTag() {
        return tag;
    }
}
