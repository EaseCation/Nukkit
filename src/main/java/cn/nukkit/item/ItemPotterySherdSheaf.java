package cn.nukkit.item;

public class ItemPotterySherdSheaf extends Item {
    public ItemPotterySherdSheaf() {
        this(0, 1);
    }

    public ItemPotterySherdSheaf(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdSheaf(Integer meta, int count) {
        super(SHEAF_POTTERY_SHERD, meta, count, "Sheaf Pottery Sherd");
    }
}
