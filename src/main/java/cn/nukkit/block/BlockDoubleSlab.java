package cn.nukkit.block;

import cn.nukkit.item.Item;

public abstract class BlockDoubleSlab extends BlockSolidMeta {

    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1000;

    protected BlockDoubleSlab(int meta) {
        super(meta);
    }

    @Override
    public boolean isSlab() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(getSlabBlockId()), getSlabType());
    }

    public int getSlabType() {
        return getDamage() & TYPE_MASK;
    }

    protected abstract int getSlabBlockId();
}
