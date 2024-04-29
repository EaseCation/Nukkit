package cn.nukkit.item;

public class ItemPotterySherdGuster extends Item {
    public ItemPotterySherdGuster() {
        this(0, 1);
    }

    public ItemPotterySherdGuster(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdGuster(Integer meta, int count) {
        super(GUSTER_POTTERY_SHERD, meta, count, "Guster Pottery Sherd");
    }
}
