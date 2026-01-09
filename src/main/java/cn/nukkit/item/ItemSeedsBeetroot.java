package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSeedsBeetroot extends Item {

    public ItemSeedsBeetroot() {
        this(0, 1);
    }

    public ItemSeedsBeetroot(Integer meta) {
        this(meta, 1);
    }

    public ItemSeedsBeetroot(Integer meta, int count) {
        super(BEETROOT_SEEDS, meta, count, "Beetroot Seeds");
        this.block = Block.get(BlockID.BEETROOT);
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }
}
