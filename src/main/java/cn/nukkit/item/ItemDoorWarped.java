package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemDoorWarped extends Item {
    public ItemDoorWarped() {
        this(0, 1);
    }

    public ItemDoorWarped(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorWarped(Integer meta, int count) {
        super(WARPED_DOOR, meta, count, "Warped Door");
        this.block = Block.get(Block.BLOCK_WARPED_DOOR);
    }
}
