package cn.nukkit.inventory.recipe;

import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class DeferredItemDescriptor implements ItemDescriptor {
    private final String name;
    private final int meta;

    public DeferredItemDescriptor(String name, int meta) {
        this.name = name;
        this.meta = meta;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFERRED;
    }

    public String getName() {
        return name;
    }

    public int getMeta() {
        return meta;
    }
}
