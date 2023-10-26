package cn.nukkit.item;

public class ItemPotterySherdArmsUp extends Item {
    public ItemPotterySherdArmsUp() {
        this(0, 1);
    }

    public ItemPotterySherdArmsUp(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdArmsUp(Integer meta, int count) {
        super(ARMS_UP_POTTERY_SHERD, meta, count, "Arms Up Pottery Sherd");
    }
}
