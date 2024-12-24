package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemSignBamboo extends Item {
    public ItemSignBamboo() {
        this(0, 1);
    }

    public ItemSignBamboo(Integer meta) {
        this(meta, 1);
    }

    public ItemSignBamboo(Integer meta, int count) {
        super(BAMBOO_SIGN, meta, count, "Bamboo Sign");
        this.block = Block.get(Block.BAMBOO_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
