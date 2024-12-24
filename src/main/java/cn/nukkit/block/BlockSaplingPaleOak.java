package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockSaplingPaleOak extends BlockSapling {
    public static final int AGE_BIT = 0b1;

    public BlockSaplingPaleOak() {
        this(0);
    }

    public BlockSaplingPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_SAPLING;
    }

    @Override
    public String getName() {
        return "Pale Oak Sapling";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    protected int getAgeBit() {
        return AGE_BIT;
    }

    @Override
    protected void grow() {
        //TODO
    }
}
