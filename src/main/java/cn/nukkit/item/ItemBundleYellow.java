package cn.nukkit.item;

public class ItemBundleYellow extends ItemBundle {
    public ItemBundleYellow() {
        this( 0, 1);
    }

    public ItemBundleYellow(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleYellow(Integer meta, int count) {
        super(YELLOW_BUNDLE, meta, count, "Yellow Bundle");
    }
}
