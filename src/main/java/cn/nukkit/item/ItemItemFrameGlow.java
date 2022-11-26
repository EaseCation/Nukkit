package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemItemFrameGlow extends Item {
    public ItemItemFrameGlow() {
        this(0, 1);
    }

    public ItemItemFrameGlow(Integer meta) {
        this(meta, 1);
    }

    public ItemItemFrameGlow(Integer meta, int count) {
        super(GLOW_FRAME, 0, count, "Glow Item Frame");
        this.block = Block.get(Block.BLOCK_GLOW_FRAME);
    }
}
