package cn.nukkit.item;

public class ItemRawCopper extends Item {

    public ItemRawCopper() {
        this(0, 1);
    }

    public ItemRawCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemRawCopper(Integer meta, int count) {
        super(RAW_COPPER, meta, count, "Raw Copper");
    }
}
