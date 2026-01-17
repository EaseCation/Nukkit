package cn.nukkit.item;

import cn.nukkit.Player;

public class ItemBookAndQuill extends ItemBookWritable {

    public ItemBookAndQuill() {
        this(0, 1);
    }

    public ItemBookAndQuill(Integer meta) {
        this(meta, 1);
    }

    public ItemBookAndQuill(Integer meta, int count) {
        super(Item.WRITABLE_BOOK, meta, count, "Book and Quill");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public String getInteractText(Player player) {
        return "action.interact.write";
    }
}
