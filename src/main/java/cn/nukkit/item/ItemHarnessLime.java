package cn.nukkit.item;

public class ItemHarnessLime extends Item {
    public ItemHarnessLime() {
        this( 0, 1);
    }

    public ItemHarnessLime(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessLime(Integer meta, int count) {
        super(LIME_HARNESS, meta, count, "Lime Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
