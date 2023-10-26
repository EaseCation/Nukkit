package cn.nukkit.item;

public class ItemPotterySherdHeartbreak extends Item {
    public ItemPotterySherdHeartbreak() {
        this(0, 1);
    }

    public ItemPotterySherdHeartbreak(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdHeartbreak(Integer meta, int count) {
        super(HEARTBREAK_POTTERY_SHERD, meta, count, "Heartbreak Pottery Sherd");
    }
}
