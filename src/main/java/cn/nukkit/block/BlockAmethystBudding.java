package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;

public class BlockAmethystBudding extends BlockSolid {
    BlockAmethystBudding() {

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
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return !V1_21_50.isAvailable();
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
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
