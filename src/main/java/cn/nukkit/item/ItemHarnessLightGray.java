package cn.nukkit.item;

public class ItemHarnessLightGray extends Item {
    public ItemHarnessLightGray() {
        this( 0, 1);
    }

    public ItemHarnessLightGray(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessLightGray(Integer meta, int count) {
        super(LIGHT_GRAY_HARNESS, meta, count, "Light Gray Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
