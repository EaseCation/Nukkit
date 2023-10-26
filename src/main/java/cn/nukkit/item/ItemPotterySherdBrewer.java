package cn.nukkit.item;

public class ItemPotterySherdBrewer extends Item {
    public ItemPotterySherdBrewer() {
        this(0, 1);
    }

    public ItemPotterySherdBrewer(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdBrewer(Integer meta, int count) {
        super(BREWER_POTTERY_SHERD, meta, count, "Brewer Pottery Sherd");
    }
}
