package cn.nukkit.item;

public class ItemPotterySherdShelter extends Item {
    public ItemPotterySherdShelter() {
        this(0, 1);
    }

    public ItemPotterySherdShelter(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdShelter(Integer meta, int count) {
        super(SHELTER_POTTERY_SHERD, meta, count, "Shelter Pottery Sherd");
    }
}
