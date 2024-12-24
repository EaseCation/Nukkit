package cn.nukkit.item;

public class ItemPotterySherdSnort extends Item {
    public ItemPotterySherdSnort() {
        this(0, 1);
    }

    public ItemPotterySherdSnort(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdSnort(Integer meta, int count) {
        super(SNORT_POTTERY_SHERD, meta, count, "Snort Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
