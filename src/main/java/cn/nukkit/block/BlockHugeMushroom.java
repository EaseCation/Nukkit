package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockHugeMushroom extends BlockSolidMeta {
    protected BlockHugeMushroom(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        int count = ThreadLocalRandom.current().nextInt(-7, 3);
        if (count <= 0) {
            return new Item[0];
        }

        return new Item[]{
                Item.get(getSmallMushroomId()),
        };
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected abstract int getSmallMushroomId();
}
