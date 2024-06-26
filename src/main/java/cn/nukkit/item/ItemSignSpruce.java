package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemSignSpruce extends Item {

    public ItemSignSpruce() {
        this(0, 1);
    }

    public ItemSignSpruce(Integer meta) {
        this(meta, 1);
    }

    public ItemSignSpruce(Integer meta, int count) {
        super(SPRUCE_SIGN, meta, count, "Spruce Sign");
        this.block = Block.get(BlockID.SPRUCE_STANDING_SIGN);
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
