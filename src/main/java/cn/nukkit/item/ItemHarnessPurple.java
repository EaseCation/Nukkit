package cn.nukkit.item;

public class ItemHarnessPurple extends Item {
    public ItemHarnessPurple() {
        this( 0, 1);
    }

    public ItemHarnessPurple(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessPurple(Integer meta, int count) {
        super(PURPLE_HARNESS, meta, count, "Purple Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
