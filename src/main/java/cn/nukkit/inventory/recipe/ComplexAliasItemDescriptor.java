package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class ComplexAliasItemDescriptor implements ItemDescriptor {
    private final String name;

    private final transient int fullId;

    public ComplexAliasItemDescriptor(String name) {
        this.name = name;

        this.fullId = Items.getFullIdByName(name, true, true);
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.COMPLEX_ALIAS;
    }

    @Override
    public boolean accepts(Item item) {
        if (item.getCount() < 1) {
            return false;
        }
        return fullId == item.getFullId();
    }

    public String getName() {
        return name;
    }

    public int getFullId() {
        return fullId;
    }
}
