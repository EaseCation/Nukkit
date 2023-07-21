package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSign extends Item {

    public ItemSign() {
        this(0, 1);
    }

    public ItemSign(Integer meta) {
        this(meta, 1);
    }

    public ItemSign(Integer meta, int count) {
        super(OAK_SIGN, meta, count, "Oak Sign");
        this.block = Block.get(BlockID.STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
