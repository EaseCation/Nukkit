package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemNetherSprouts extends Item {
    public ItemNetherSprouts() {
        this(0, 1);
    }

    public ItemNetherSprouts(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherSprouts(Integer meta, int count) {
        super(NETHER_SPROUTS, meta, count, "Nether Sprouts");
        this.block = Block.get(Block.NETHER_SPROUTS);
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
