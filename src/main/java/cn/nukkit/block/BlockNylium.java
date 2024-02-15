package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class BlockNylium extends BlockNetherrack {
    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(NETHERRACK),
            };
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block above = up();
            if (above.isTransparent() || !above.isSolid()) {
                return 0;
            }

            level.setBlock(this, get(NETHERRACK), true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    protected boolean onFertilized() {
        if (y >= level.getHeightRange().getMaxY() - 1) {
            return false;
        }

        Block up = up();
        if (!up.isAir()) {
            return false;
        }

        placeVegetation();
        return true;
    }

    protected void scatterVegetation(Function<Random, Block> blockProvider) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int thisX = getFloorX();
        int thisY = getFloorY();
        int thisZ = getFloorZ();
        for (int i = 0; i < 9; i++) {
            int x = thisX + random.nextInt(-2, 3);
            int z = thisZ + random.nextInt(-2, 3);
            Block block = level.getBlock(x, thisY, z);
            int id = block.getId();

            if (id != CRIMSON_NYLIUM && id != WARPED_NYLIUM && id != SOUL_SOIL
                    && id != GRASS_BLOCK && id != DIRT && id != PODZOL && id != MYCELIUM && id != FARMLAND && id != DIRT_WITH_ROOTS && id != MOSS_BLOCK) {
                continue;
            }

            Block above = block.up();
            if (!above.isAir()) {
                continue;
            }

            level.setBlock(above, blockProvider.apply(random), true);
        }
    }

    protected abstract void placeVegetation();
}
