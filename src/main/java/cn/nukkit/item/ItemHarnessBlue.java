package cn.nukkit.item;

public class ItemHarnessBlue extends Item {
    public ItemHarnessBlue() {
        this( 0, 1);
    }

    public ItemHarnessBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessBlue(Integer meta, int count) {
        super(BLUE_HARNESS, meta, count, "Blue Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
