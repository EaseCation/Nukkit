package cn.nukkit.item;

public class ItemHarnessPink extends Item {
    public ItemHarnessPink() {
        this( 0, 1);
    }

    public ItemHarnessPink(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessPink(Integer meta, int count) {
        super(PINK_HARNESS, meta, count, "Pink Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
