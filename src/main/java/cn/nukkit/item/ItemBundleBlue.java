package cn.nukkit.item;

public class ItemBundleBlue extends ItemBundle {
    public ItemBundleBlue() {
        this( 0, 1);
    }

    public ItemBundleBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleBlue(Integer meta, int count) {
        super(BLUE_BUNDLE, meta, count, "Blue Bundle");
    }
}
