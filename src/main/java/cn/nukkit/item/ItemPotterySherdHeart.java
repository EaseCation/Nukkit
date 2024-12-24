package cn.nukkit.item;

public class ItemPotterySherdHeart extends Item {
    public ItemPotterySherdHeart() {
        this(0, 1);
    }

    public ItemPotterySherdHeart(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdHeart(Integer meta, int count) {
        super(HEART_POTTERY_SHERD, meta, count, "Heart Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
