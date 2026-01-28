package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

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
    public boolean onClickAir(Player player, Vector3 directionVector) {
        CompoundTag tag = getOrCreateNamedTag();
        if (tag.contains("pages")) {
            return false;
        }
        tag.putList("pages", new ListTag<>());
        setNamedTag(tag);
        player.getInventory().setItemInHand(this);
        return false;
    }

    @Override
    public String getInteractText(Player player) {
        return "action.interact.write";
    }
}
