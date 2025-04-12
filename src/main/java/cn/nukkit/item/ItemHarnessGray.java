package cn.nukkit.item;

public class ItemHarnessGray extends Item {
    public ItemHarnessGray() {
        this( 0, 1);
    }

    public ItemHarnessGray(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessGray(Integer meta, int count) {
        super(GRAY_HARNESS, meta, count, "Gray Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
