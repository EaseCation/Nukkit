package cn.nukkit.item;

public class ItemHarnessYellow extends Item {
    public ItemHarnessYellow() {
        this( 0, 1);
    }

    public ItemHarnessYellow(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessYellow(Integer meta, int count) {
        super(YELLOW_HARNESS, meta, count, "Yellow Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
