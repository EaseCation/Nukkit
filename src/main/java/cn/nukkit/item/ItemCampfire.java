package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemCampfire extends Item {

    public ItemCampfire() {
        this(0, 1);
    }

    public ItemCampfire(Integer meta) {
        this(meta, 1);
    }

    public ItemCampfire(Integer meta, int count) {
        super(CAMPFIRE, 0, count, "Campfire");
        this.block = Block.get(BlockID.BLOCK_CAMPFIRE);
    }
}
