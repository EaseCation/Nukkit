package cn.nukkit.item;

public class ItemHarnessCyan extends Item {
    public ItemHarnessCyan() {
        this( 0, 1);
    }

    public ItemHarnessCyan(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessCyan(Integer meta, int count) {
        super(CYAN_HARNESS, meta, count, "Cyan Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
