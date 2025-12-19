package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemCocoaBeans extends Item {
    public ItemCocoaBeans() {
        this(0, 1);
    }

    public ItemCocoaBeans(Integer meta) {
        this(meta, 1);
    }

    public ItemCocoaBeans(Integer meta, int count) {
        super(COCOA_BEANS, meta, count, "Cocoa Beans");
        this.block = Block.get(BlockID.COCOA);
    }
}
