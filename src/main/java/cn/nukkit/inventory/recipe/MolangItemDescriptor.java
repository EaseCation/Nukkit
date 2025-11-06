package cn.nukkit.inventory.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ItemDescriptorType;
import lombok.ToString;

@ToString
public class MolangItemDescriptor implements ItemDescriptor {
    private final String expression;
    private final int version;

    public MolangItemDescriptor(String expression, int version) {
        this.expression = expression;
        this.version = version;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.MOLANG;
    }

    @Override
    public boolean accepts(Item item) {
        if (item.getCount() < 1) {
            return false;
        }
        throw new UnsupportedOperationException("not yet implemented");
    }

    public String getExpression() {
        return expression;
    }

    public int getVersion() {
        return version;
    }
}
