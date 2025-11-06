package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class DeferredItemDescriptor implements ItemDescriptor {
    private final String name;
    private final int meta;

    private final transient int id;

    public DeferredItemDescriptor(String name, int meta) {
        this.name = name;
        this.meta = meta;

        this.id = Items.getIdByName(name, true, true);
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFERRED;
    }

    @Override
    public boolean accepts(Item item) {
        if (item.getCount() < 1) {
            return false;
        }
        return id == item.getId() && meta == item.getDamage();
    }

    public String getName() {
        return name;
    }

    public int getMeta() {
        return meta;
    }

    public int getId() {
        return id;
    }
}
