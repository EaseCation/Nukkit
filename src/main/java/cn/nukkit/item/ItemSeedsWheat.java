package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSeedsWheat extends Item {

    public ItemSeedsWheat() {
        this(0, 1);
    }

    public ItemSeedsWheat(Integer meta) {
        this(meta, 1);
    }

    public ItemSeedsWheat(Integer meta, int count) {
        super(WHEAT_SEEDS, meta, count, "Wheat Seeds");
        this.block = Block.get(BlockID.WHEAT);
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }
}
