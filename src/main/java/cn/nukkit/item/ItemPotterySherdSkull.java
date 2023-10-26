package cn.nukkit.item;

public class ItemPotterySherdSkull extends Item {
    public ItemPotterySherdSkull() {
        this(0, 1);
    }

    public ItemPotterySherdSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdSkull(Integer meta, int count) {
        super(SKULL_POTTERY_SHERD, meta, count, "Skull Pottery Sherd");
    }
}
