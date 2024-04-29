package cn.nukkit.item;

public class ItemPotterySherdFlow extends Item {
    public ItemPotterySherdFlow() {
        this(0, 1);
    }

    public ItemPotterySherdFlow(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdFlow(Integer meta, int count) {
        super(FLOW_POTTERY_SHERD, meta, count, "Flow Pottery Sherd");
    }
}
