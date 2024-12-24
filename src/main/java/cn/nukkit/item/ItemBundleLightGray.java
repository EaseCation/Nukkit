package cn.nukkit.item;

public class ItemBundleLightGray extends ItemBundle {
    public ItemBundleLightGray() {
        this( 0, 1);
    }

    public ItemBundleLightGray(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleLightGray(Integer meta, int count) {
        super(LIGHT_GRAY_BUNDLE, meta, count, "Light Gray Bundle");
    }
}
