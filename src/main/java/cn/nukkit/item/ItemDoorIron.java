package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDoorIron extends Item {

    public ItemDoorIron() {
        this(0, 1);
    }

    public ItemDoorIron(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorIron(Integer meta, int count) {
        super(IRON_DOOR, meta, count, "Iron Door");
        this.block = Block.get(BlockID.BLOCK_IRON_DOOR);
    }

}
