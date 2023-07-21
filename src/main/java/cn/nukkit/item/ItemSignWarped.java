package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemSignWarped extends Item {
    public ItemSignWarped() {
        this(0, 1);
    }

    public ItemSignWarped(Integer meta) {
        this(meta, 1);
    }

    public ItemSignWarped(Integer meta, int count) {
        super(WARPED_SIGN, meta, count, "Warped Sign");
        this.block = Block.get(Block.WARPED_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
