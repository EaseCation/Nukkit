package cn.nukkit.inventory.recipe;

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

    public String getExpression() {
        return expression;
    }

    public int getVersion() {
        return version;
    }
}
