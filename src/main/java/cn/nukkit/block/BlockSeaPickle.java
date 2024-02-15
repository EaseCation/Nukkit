package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockSeaPickle extends BlockFlowable {

    public static final int CLUSTER_COUNT_MASK = 0b11;
    public static final int DEAD_BIT = 0b100;

    public BlockSeaPickle() {
        this(0);
    }

    public BlockSeaPickle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SEA_PICKLE;
    }

    @Override
    public String getName() {
        return "Sea Pickle";
    }

    @Override
    public int getLightLevel() {
        return isDead() ? 0 : (getClusterCount() + 1) * 3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(getItemId(), 0, getClusterCount())
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block extra = null;
        if (block.isLava() || block.isWater() && !block.isFullLiquid()
                || !block.isAir() && (extra = level.getExtraBlock(this)).isWater() && !extra.isFullLiquid()) {
            return false;
        }

        if (!canBeSupportBy(down())) {
            return false;
        }

        if (block.isWater() || extra != null && extra.isWater() && !extra.isLiquidSource()) {
            level.setExtraBlock(this, get(FLOWING_WATER), true, false);
        }

        setDead(!block.isWater() && (extra == null || !extra.isWater()));
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        int id = item.getId();
        if (id == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            Block extra = level.getExtraBlock(this);
            if (!extra.isWater() || !extra.isFullLiquid()) {
                return true;
            }
            Block down = down();
            if (down.getId() != CORAL_BLOCK || (down.getDamage() & BlockCoralBlock.DEAD_BIT) == BlockCoralBlock.DEAD_BIT) {
                return true;
            }

            int count = getClusterCount();
            if (count != 4) {
                setClusterCount(4);
                level.setBlock(this, this, true);
            }

            int thisX = getFloorX();
            int thisY = getFloorY();
            int thisZ = getFloorZ();
            int minHeight = level.getHeightRange().getMinY();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int x = thisX - 2; x <= thisX + 2; x++) {
                for (int z = thisZ - 2; z <= thisZ + 2; z++) {
                    if (distanceManhattan(x, thisY, z) > 2) {
                        continue;
                    }
                    for (int y = thisY; y >= thisY - 1 && y >= minHeight; y--) {
                        if (x == thisX && y == thisY && z == thisZ || random.nextInt(3) != 0) {
                            continue;
                        }
                        Block block = level.getBlock(x, y, z);
                        if (!block.isWater()) {
                            continue;
                        }
                        Block below = level.getBlock(x, y - 1, z);
                        if (below.getId() != CORAL_BLOCK) {
                            continue;
                        }
                        level.setExtraBlock(x, y, z, block, true, false);
                        level.setBlock(x, y, z, get(SEA_PICKLE, random.nextInt(4)), true, true);
                    }
                }
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }

        if (id == getItemId()) {
            int count = getClusterCount();
            if (count == 4) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

            setClusterCount(count + 1);
            level.setBlock(this, this, true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canBeSupportBy(down())) {
                level.useBreakOn(this);
                return type;
            }

            if (isDead() || level.getExtraBlock(this).isWater()) {
                return 0;
            }

            setDead(true);
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canBeSupportBy(Block block) {
        int id = block.getId();
        return SupportType.hasFullSupport(block, BlockFace.UP) && id != SNOW_LAYER && id != SCAFFOLDING;
    }

    public int getClusterCount() {
        return (getDamage() & CLUSTER_COUNT_MASK) + 1;
    }

    public void setClusterCount(int count) {
        setDamage((getDamage() & ~CLUSTER_COUNT_MASK) | ((Mth.clamp(count, 1, 4) - 1) & CLUSTER_COUNT_MASK));
    }

    public boolean isDead() {
        return (getDamage() & DEAD_BIT) == DEAD_BIT;
    }

    public void setDead(boolean dead) {
        setDamage(dead ? getDamage() | DEAD_BIT : getDamage() & ~DEAD_BIT);
    }
}
