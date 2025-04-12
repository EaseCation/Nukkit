package cn.nukkit.item;

public class ItemHarnessLightBlue extends Item {
    public ItemHarnessLightBlue() {
        this( 0, 1);
    }

    public ItemHarnessLightBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemHarnessLightBlue(Integer meta, int count) {
        super(LIGHT_BLUE_HARNESS, meta, count, "Light Blue Harness");
    }

    @Override
    public boolean isHarness() {
        return true;
    }
}
