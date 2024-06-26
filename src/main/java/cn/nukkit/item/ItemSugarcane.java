package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSugarcane extends Item {

    public ItemSugarcane() {
        this(0, 1);
    }

    public ItemSugarcane(Integer meta) {
        this(meta, 1);
    }

    public ItemSugarcane(Integer meta, int count) {
        super(SUGAR_CANE, meta, count, "Sugar Cane");
        this.block = Block.get(BlockID.BLOCK_REEDS);
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
