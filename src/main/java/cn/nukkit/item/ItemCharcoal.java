package cn.nukkit.item;

public class ItemCharcoal extends Item {
    public ItemCharcoal() {
        this(0, 1);
    }

    public ItemCharcoal(Integer meta) {
        this(meta, 1);
    }

    public ItemCharcoal(Integer meta, int count) {
        super(CHARCOAL, meta, count, "Charcoal");
    }

    @Override
    public int getFuelTime() {
        return 1600;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.15f;
    }

    @Override
    public boolean isCoal() {
        return true;
    }
}
