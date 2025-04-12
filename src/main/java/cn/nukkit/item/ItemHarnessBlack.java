package cn.nukkit.item;

public class ItemHarnessBlack extends Item {
    public ItemHarnessBlack() {
        this( 0, 1);
    }

    public ItemHarnessBlack(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessBlack(Integer meta, int count) {
        super(BLACK_HARNESS, meta, count, "Black Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
