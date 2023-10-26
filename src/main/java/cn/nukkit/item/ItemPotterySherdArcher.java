package cn.nukkit.item;

public class ItemPotterySherdArcher extends Item {
    public ItemPotterySherdArcher() {
        this(0, 1);
    }

    public ItemPotterySherdArcher(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdArcher(Integer meta, int count) {
        super(ARCHER_POTTERY_SHERD, meta, count, "Archer Pottery Sherd");
    }
}
