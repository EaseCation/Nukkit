package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDoorWood extends Item {

    public ItemDoorWood() {
        this(0, 1);
    }

    public ItemDoorWood(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorWood(Integer meta, int count) {
        super(WOODEN_DOOR, meta, count, "Oak Door");
        this.block = Block.get(BlockID.BLOCK_WOODEN_DOOR);
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
