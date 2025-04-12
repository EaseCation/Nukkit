package cn.nukkit.item;

public class ItemHarnessOrange extends Item {
    public ItemHarnessOrange() {
        this( 0, 1);
    }

    public ItemHarnessOrange(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessOrange(Integer meta, int count) {
        super(ORANGE_HARNESS, meta, count, "Orange Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
