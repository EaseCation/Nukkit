package cn.nukkit.item;

public class ItemHarnessBrown extends Item {
    public ItemHarnessBrown() {
        this( 0, 1);
    }

    public ItemHarnessBrown(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessBrown(Integer meta, int count) {
        super(BROWN_HARNESS, meta, count, "Brown Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
