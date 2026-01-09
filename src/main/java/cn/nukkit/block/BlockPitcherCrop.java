package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public class BlockPitcherCrop extends BlockCrops {
    public static final int UPPER_BLOCK_BIT = 0b1;
    public static final int GROWTH_MASK = 0b1110;
    public static final int GROWTH_START = 1;

    BlockPitcherCrop() {

    }

    @Override
    public int getId() {
        return PITCHER_CROP;
    }

    @Override
    public String getName() {
        return "Pitcher Crop";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.PITCHER_POD);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (isUpper()) {
            return new Item[0];
        }

        return new Item[]{
                Item.get(getGrowth() >= 4 ? ItemBlockID.PITCHER_PLANT : Item.PITCHER_POD),
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face != BlockFace.UP) {
            return false;
        }

        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (target.getId() != FARMLAND) {
            return false;
        }

        setDamage(0);
        return level.setBlock(block, this, true);
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (isUpper()) {
            level.useBreakOn(downVec(), true);
            return true;
        }
        return super.onBreak(item, player);
    }

    @Override
    public boolean canBeActivated() {
        return !isUpper();
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (isUpper()) {
                return false;
            }

            if (getGrowth() >= 4) {
                return true;
            }

            if (grow()) {
                level.addParticle(new BoneMealParticle(this));

                if (player != null && player.isSurvivalLike()) {
                    item.pop();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = down();
            if (isUpper()) {
                if (below.getId() != getId() || (below.getDamage() & UPPER_BLOCK_BIT) != 0) {
                    level.setBlock(this, Blocks.air(), true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else if (below.getId() != FARMLAND || getGrowth() >= 3 && up().getId() != getId()) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (isUpper()) {
                return 0;
            }

            if (getGrowth() >= 4) {
                return 0;
            }

            if (ThreadLocalRandom.current().nextInt(2) != 1) {
                return 0;
            }

            if (grow()) {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    private boolean grow() {
        int growth = getGrowth();
        if (growth >= 4) {
            return false;
        }

        Block block = getGrowthBlock(growth + 1);
        Block upper = null;

        if (growth >= 2) {
            if (growth == 2) {
                Block above = up();
                if (!above.isAir()) {
                    return false;
                }
            }

            upper = block.clone();
            upper.setDamage(upper.getDamage() | UPPER_BLOCK_BIT);
        }

        BlockGrowEvent event = new BlockGrowEvent(this, block);
        event.call();
        if (event.isCancelled()) {
            return false;
        }

        if (upper != null) {
            level.setBlock(upVec(), upper, true, false);
        }
        level.setBlock(this, event.getNewState(), true);
        return true;
    }

    @Override
    protected Block getGrowthBlock(int growth) {
        Block block = clone();
        block.setDamage(growth << GROWTH_START);
        return block;
    }

    public int getGrowth() {
        return getDamage() >> GROWTH_START;
    }

    public boolean isUpper() {
        return (getDamage() & UPPER_BLOCK_BIT) != 0;
    }
}
