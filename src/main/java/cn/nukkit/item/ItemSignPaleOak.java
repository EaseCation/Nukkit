package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemSignPaleOak extends Item {
    public ItemSignPaleOak() {
        this(0, 1);
    }

    public ItemSignPaleOak(Integer meta) {
        this(meta, 1);
    }

    public ItemSignPaleOak(Integer meta, int count) {
        super(PALE_OAK_SIGN, meta, count, "Pale Oak Sign");
        this.block = Block.get(Block.PALE_OAK_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
