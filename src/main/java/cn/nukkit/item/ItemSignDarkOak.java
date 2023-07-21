package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemSignDarkOak extends Item {

    public ItemSignDarkOak() {
        this(0, 1);
    }

    public ItemSignDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemSignDarkOak(Integer meta, int count) {
        super(DARK_OAK_SIGN, meta, count, "Dark Oak Sign");
        this.block = Block.get(BlockID.DARKOAK_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
