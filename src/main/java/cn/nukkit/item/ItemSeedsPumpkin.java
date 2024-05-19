package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSeedsPumpkin extends Item {

    public ItemSeedsPumpkin() {
        this(0, 1);
    }

    public ItemSeedsPumpkin(Integer meta) {
        this(meta, 1);
    }

    public ItemSeedsPumpkin(Integer meta, int count) {
        super(PUMPKIN_SEEDS, meta, count, "Pumpkin Seeds");
        this.block = Block.get(BlockID.PUMPKIN_STEM);
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }
}
