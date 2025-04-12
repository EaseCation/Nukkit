package cn.nukkit.item;

public class ItemHarnessMagenta extends Item {
    public ItemHarnessMagenta() {
        this( 0, 1);
    }

    public ItemHarnessMagenta(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessMagenta(Integer meta, int count) {
        super(MAGENTA_HARNESS, meta, count, "Magenta Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
