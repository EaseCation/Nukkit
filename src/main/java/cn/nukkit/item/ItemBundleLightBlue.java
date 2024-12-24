package cn.nukkit.item;

public class ItemBundleLightBlue extends ItemBundle {
    public ItemBundleLightBlue() {
        this( 0, 1);
    }

    public ItemBundleLightBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleLightBlue(Integer meta, int count) {
        super(LIGHT_BLUE_BUNDLE, meta, count, "Light Blue Bundle");
    }
}
