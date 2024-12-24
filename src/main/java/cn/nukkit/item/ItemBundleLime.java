package cn.nukkit.item;

public class ItemBundleLime extends ItemBundle {
    public ItemBundleLime() {
        this( 0, 1);
    }

    public ItemBundleLime(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleLime(Integer meta, int count) {
        super(LIME_BUNDLE, meta, count, "Lime Bundle");
    }
}
