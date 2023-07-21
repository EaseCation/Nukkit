package cn.nukkit.item;

public class ItemIngotNetherite extends Item {

    public ItemIngotNetherite() {
        this(0, 1);
    }

    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, meta, count, "Netherite Ingot");
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }
}
