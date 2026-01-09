package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemDoorBirch extends Item {
    public ItemDoorBirch() {
        this(0, 1);
    }

    public ItemDoorBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorBirch(Integer meta, int count) {
        super(BIRCH_DOOR, meta, count, "Birch Door");
        this.block = Block.get(BlockID.BIRCH_DOOR);
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
