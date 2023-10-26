package cn.nukkit.item;

public class ItemPotterySherdBlade extends Item {
    public ItemPotterySherdBlade() {
        this(0, 1);
    }

    public ItemPotterySherdBlade(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdBlade(Integer meta, int count) {
        super(BLADE_POTTERY_SHERD, meta, count, "Blade Pottery Sherd");
    }
}
