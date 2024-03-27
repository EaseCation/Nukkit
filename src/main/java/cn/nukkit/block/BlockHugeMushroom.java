package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockHugeMushroom extends BlockSolidMeta {
    protected BlockHugeMushroom(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
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
