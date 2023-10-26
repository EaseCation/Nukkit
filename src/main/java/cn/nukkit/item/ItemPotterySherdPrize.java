package cn.nukkit.item;

public class ItemPotterySherdPrize extends Item {
    public ItemPotterySherdPrize() {
        this(0, 1);
    }

    public ItemPotterySherdPrize(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdPrize(Integer meta, int count) {
        super(PRIZE_POTTERY_SHERD, meta, count, "Prize Pottery Sherd");
    }
}
