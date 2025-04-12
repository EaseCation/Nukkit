package cn.nukkit.item;

public class ItemHarnessRed extends Item {
    public ItemHarnessRed() {
        this( 0, 1);
    }

    public ItemHarnessRed(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessRed(Integer meta, int count) {
        super(RED_HARNESS, meta, count, "Red Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
