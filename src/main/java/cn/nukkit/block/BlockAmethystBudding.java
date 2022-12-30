package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockAmethystBudding extends BlockSolid {
    public BlockAmethystBudding() {
    }

    @Override
    public int getId() {
        return BUDDING_AMETHYST;
    }

    @Override
    public String getName() {
        return "Budding Amethyst";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 7.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(5) != 0) {
                return 0;
            }

            BlockFace face = BlockFace.random();
            Block block = getSide(face);

            switch (block.getId()) {
                case SMALL_AMETHYST_BUD:
                    level.setBlock(block, get(MEDIUM_AMETHYST_BUD, block.getDamage()), true);
                    return Level.BLOCK_UPDATE_RANDOM;
                case MEDIUM_AMETHYST_BUD:
                    level.setBlock(block, get(LARGE_AMETHYST_BUD, block.getDamage()), true);
                    return Level.BLOCK_UPDATE_RANDOM;
                case LARGE_AMETHYST_BUD:
                    level.setBlock(block, get(AMETHYST_CLUSTER, block.getDamage()), true);
                    return Level.BLOCK_UPDATE_RANDOM;
            }

            if (!block.isAir() && !block.isWaterSource()) {
                return 0;
            }

            if (block.isWaterSource()) {
                level.setExtraBlock(block, block, true, false);
            }
            level.setBlock(block, get(SMALL_AMETHYST_BUD, face.getIndex()), true);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }
}
