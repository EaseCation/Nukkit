package cn.nukkit.item;

public class ItemPotterySherdMourner extends Item {
    public ItemPotterySherdMourner() {
        this(0, 1);
    }

    public ItemPotterySherdMourner(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdMourner(Integer meta, int count) {
        super(MOURNER_POTTERY_SHERD, meta, count, "Mourner Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
