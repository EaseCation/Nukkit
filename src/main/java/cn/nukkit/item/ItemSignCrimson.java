package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemSignCrimson extends Item {
    public ItemSignCrimson() {
        this(0, 1);
    }

    public ItemSignCrimson(Integer meta) {
        this(meta, 1);
    }

    public ItemSignCrimson(Integer meta, int count) {
        super(CRIMSON_SIGN, meta, count, "Crimson Sign");
        this.block = Block.get(Block.CRIMSON_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
