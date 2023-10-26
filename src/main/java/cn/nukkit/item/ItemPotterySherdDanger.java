package cn.nukkit.item;

public class ItemPotterySherdDanger extends Item {
    public ItemPotterySherdDanger() {
        this(0, 1);
    }

    public ItemPotterySherdDanger(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdDanger(Integer meta, int count) {
        super(DANGER_POTTERY_SHERD, meta, count, "Danger Pottery Sherd");
    }
}
