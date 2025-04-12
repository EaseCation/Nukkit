package cn.nukkit.item;

public class ItemHarnessWhite extends Item {
    public ItemHarnessWhite() {
        this( 0, 1);
    }

    public ItemHarnessWhite(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessWhite(Integer meta, int count) {
        super(WHITE_HARNESS, meta, count, "White Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
