package cn.nukkit.item;

public class ItemPotterySherdPlenty extends Item {
    public ItemPotterySherdPlenty() {
        this(0, 1);
    }

    public ItemPotterySherdPlenty(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdPlenty(Integer meta, int count) {
        super(PLENTY_POTTERY_SHERD, meta, count, "Plenty Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
