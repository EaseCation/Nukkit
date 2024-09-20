package cn.nukkit.inventory.recipe;

import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class ComplexAliasItemDescriptor implements ItemDescriptor {
    private final String name;

    public ComplexAliasItemDescriptor(String name) {
        this.name = name;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.COMPLEX_ALIAS;
    }

    public String getName() {
        return name;
    }
}
