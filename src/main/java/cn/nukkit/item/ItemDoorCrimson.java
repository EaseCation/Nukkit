package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemDoorCrimson extends Item {
    public ItemDoorCrimson() {
        this(0, 1);
    }

    public ItemDoorCrimson(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorCrimson(Integer meta, int count) {
        super(CRIMSON_DOOR, meta, count, "Crimson Door");
        this.block = Block.get(Block.CRIMSON_DOOR);
    }
}
