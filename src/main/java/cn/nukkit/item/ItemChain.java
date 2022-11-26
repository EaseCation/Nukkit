package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemChain extends Item {
    public ItemChain() {
        this(0, 1);
    }

    public ItemChain(Integer meta) {
        this(meta, 1);
    }

    public ItemChain(Integer meta, int count) {
        super(CHAIN, 0, count, "Chain");
        this.block = Block.get(Block.BLOCK_CHAIN);
    }
}