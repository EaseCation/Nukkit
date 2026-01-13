package cn.nukkit.item;

public class ItemInkSac extends Item {
    public ItemInkSac() {
        this(0, 1);
    }

    public ItemInkSac(Integer meta) {
        this(meta, 1);
    }

    public ItemInkSac(Integer meta, int count) {
        super(INK_SAC, meta, count, "Ink Sac");
    }

    @Override
    public String getDescriptionId() {
        return "item.dye.black.name";
    }
}
