package cn.nukkit.item;

public class ItemTotem extends Item {

    public ItemTotem(Integer meta) {
        this(meta, 1);
    }

    public ItemTotem(Integer meta, int count) {
        super(TOTEM_OF_UNDYING, meta, 1, "Totem of Undying");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
