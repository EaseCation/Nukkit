package cn.nukkit.item;

public class ItemPotterySherdHowl extends Item {
    public ItemPotterySherdHowl() {
        this(0, 1);
    }

    public ItemPotterySherdHowl(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdHowl(Integer meta, int count) {
        super(HOWL_POTTERY_SHERD, meta, count, "Howl Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
