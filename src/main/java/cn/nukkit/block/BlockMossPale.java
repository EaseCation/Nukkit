package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import java.util.Random;

public class BlockMossPale extends BlockMoss {
    BlockMossPale() {

    }

    @Override
    public int getId() {
        return PALE_MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Pale Moss Block";
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }

    @Override
    protected void placeVegetation(int x, int y, int z, Random random) {
        Block block;
        Block blockAbove = null;
        int rand = random.nextInt(60);
        if (rand < 10) {
            if (level.getBlock(x, y + 1, z).isAir()) {
                block = Block.get(TALL_GRASS);
                blockAbove = Block.get(TALL_GRASS, BlockDoublePlant.UPPER_BLOCK_BIT);
            } else {
                block = Block.get(SHORT_GRASS);
            }
        } else if (rand < 35) {
            block = Block.get(PALE_MOSS_CARPET);
        } else {
            block = Block.get(SHORT_GRASS);
        }
        if (blockAbove != null) {
            level.setBlock(x, y + 1, z, blockAbove, true,false);
        }
        level.setBlock(x, y, z, block, true);
    }
}
