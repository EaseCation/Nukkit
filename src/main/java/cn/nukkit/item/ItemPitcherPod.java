package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemPitcherPod extends Item {
    public ItemPitcherPod() {
        this(0, 1);
    }

    public ItemPitcherPod(Integer meta) {
        this(meta, 1);
    }

    public ItemPitcherPod(Integer meta, int count) {
        super(PITCHER_POD, meta, count, "Pitcher Pod");
        this.block = Block.get(Block.PITCHER_CROP);
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }
}
