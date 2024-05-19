package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemSignBirch extends Item {

    public ItemSignBirch() {
        this(0, 1);
    }

    public ItemSignBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemSignBirch(Integer meta, int count) {
        super(BIRCH_SIGN, meta, count, "Birch Sign");
        this.block = Block.get(BlockID.BIRCH_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
