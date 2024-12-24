package cn.nukkit.item;

public class ItemPotterySherdBurn extends Item {
    public ItemPotterySherdBurn() {
        this(0, 1);
    }

    public ItemPotterySherdBurn(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdBurn(Integer meta, int count) {
        super(BURN_POTTERY_SHERD, meta, count, "Burn Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
