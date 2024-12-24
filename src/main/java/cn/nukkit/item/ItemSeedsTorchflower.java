package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemSeedsTorchflower extends Item {
    public ItemSeedsTorchflower() {
        this(0, 1);
    }

    public ItemSeedsTorchflower(Integer meta) {
        this(meta, 1);
    }

    public ItemSeedsTorchflower(Integer meta, int count) {
        super(TORCHFLOWER_SEEDS, meta, count, "Torchflower Seeds");
        this.block = Block.get(Block.TORCHFLOWER_CROP);
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }
}
