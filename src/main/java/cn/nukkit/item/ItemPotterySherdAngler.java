package cn.nukkit.item;

public class ItemPotterySherdAngler extends Item {
    public ItemPotterySherdAngler() {
        this(0, 1);
    }

    public ItemPotterySherdAngler(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdAngler(Integer meta, int count) {
        super(ANGLER_POTTERY_SHERD, meta, count, "Angler Pottery Sherd");
    }
}
