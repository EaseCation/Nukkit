package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.generator.object.tree.ObjectCherryTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

public class BlockSaplingCherry extends BlockSapling {
    public static final int AGE_BIT = 0b1;

    public BlockSaplingCherry() {
        this(0);
    }

    public BlockSaplingCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SAPLING;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
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
        new ObjectCherryTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current());
    }
}
