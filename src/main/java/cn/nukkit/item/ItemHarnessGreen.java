package cn.nukkit.item;

public class ItemHarnessGreen extends Item {
    public ItemHarnessGreen() {
        this( 0, 1);
    }

    public ItemHarnessGreen(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessGreen(Integer meta, int count) {
        super(GREEN_HARNESS, meta, count, "Green Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
