package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemKelp extends Item {

    public ItemKelp() {
        this(0, 1);
    }

    public ItemKelp(Integer meta) {
        this(meta, 1);
    }

    public ItemKelp(Integer meta, int count) {
        super(KELP, 0, count, "Kelp");
        this.block = Block.get(BlockID.BLOCK_KELP);
    }
}
