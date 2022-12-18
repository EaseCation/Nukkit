package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockAmethystBud extends BlockAmethystCluster {
    protected BlockAmethystBud(int meta) {
        super(meta);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == Level.BLOCK_UPDATE_NORMAL) {
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(5) != 0) {
                return 0;
            }

            level.setBlock(this, get(getNextStageBlockId(), getDamage()), true);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    protected abstract int getNextStageBlockId();
}
