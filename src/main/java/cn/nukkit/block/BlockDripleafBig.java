package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import static cn.nukkit.GameVersion.*;

public class BlockDripleafBig extends BlockTransparentMeta implements Faceable {
    public static final int TILT_MASK = 0b11;
    public static final int HEAD_BIT = 0b100;
    public static final int DIRECTION_MASK = 0b11000;
    public static final int DIRECTION_OFFSET = 3;

    public static final int TILT_NONE = 0;
    public static final int TILT_UNSTABLE = 1;
    public static final int TILT_PARTIAL_TILT = 2;
    public static final int TILT_FULL_TILT = 3;

    public BlockDripleafBig() {
        this(0);
    }

    public BlockDripleafBig(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIG_DRIPLEAF;
    }

    @Override
    public String getName() {
        return "Big Dripleaf";
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 0.1f;
        }
        return 0;
    }

    @Override
    public float getResistance() {
        return 0.5f;
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
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), HEAD_BIT);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block below = down();
        int id = below.getId();
        if (!(id == BIG_DRIPLEAF || id == GRASS_BLOCK || id == DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == CLAY)) {
            return false;
        }

        if (id == BIG_DRIPLEAF) {
            int meta = below.getDamage() & DIRECTION_MASK;
            level.setBlock(below, get(BIG_DRIPLEAF, meta), true, false);

            setDamage(meta | HEAD_BIT);
        } else {
            setDamage((player.getHorizontalFacing().getOpposite().getHorizontalIndex() << 3) | HEAD_BIT);
        }

        if (block.isWater()) {
            level.setExtraBlock(this, block, true, false);
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isBoneMeal()) {
            Block head = this;
            Block up;
            while ((up = head.up()).getId() == BIG_DRIPLEAF) {
                head = up;
            }

            if (head.getFloorY() + 1 >= level.getHeightRange().getMaxY() - 1) {
                return false;
            }

            Block above = head.up();
            if (!above.isAir() && !above.isWater()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            level.setBlock(above.downVec(), get(BIG_DRIPLEAF, getDamage() & DIRECTION_MASK), true, false);

            if (above.isWater()) {
                level.setExtraBlock(above, above, true, false);
            }
            level.setBlock(above, get(BIG_DRIPLEAF, (getDamage() & DIRECTION_MASK) | HEAD_BIT), true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!canSurvive()) {
                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (!isHead()) {
                if (up().getId() == BIG_DRIPLEAF) {
                    return 0;
                }

                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            int tilt = getTilt();
            if (tilt == TILT_NONE) {
                return 0;
            }

            if (level.isBlockPowered(this)) {
                setTilt(TILT_NONE);
                level.setBlock(this, this, true, false);
                return Level.BLOCK_UPDATE_SCHEDULED;
            }

            switch (tilt) {
                case TILT_UNSTABLE:
                    setTiltAndScheduleTick(TILT_PARTIAL_TILT);
                    break;
                case TILT_PARTIAL_TILT:
                    setTiltAndScheduleTick(TILT_FULL_TILT);
                    break;
                case TILT_FULL_TILT:
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BIG_DRIPLEAF_TILT_UP);

                    setTilt(TILT_NONE);
                    level.setBlock(this, this, true, false);
                    break;
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!isHead()) {
                return 0;
            }

            int tilt = getTilt();
            if (tilt == TILT_NONE) {
                return 0;
            }

            if (!level.isBlockPowered(this)) {
                return 0;
            }

            if (tilt != TILT_UNSTABLE) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BIG_DRIPLEAF_TILT_UP);
            }

            setTilt(TILT_NONE);
            level.setBlock(this, this, true, false);

            level.cancelScheduledUpdate(this, this);
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return isHead();
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!isHead()) {
            return;
        }

        if (getTilt() != TILT_NONE) {
            return;
        }

        if (entity instanceof EntityProjectile) {
            setTiltAndScheduleTick(TILT_FULL_TILT);
            return;
        }

        setTiltAndScheduleTick(TILT_UNSTABLE);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return isHead() ? this : super.recalculateCollisionBoundingBox();
    }

//    @Override
//    public double getMaxY() {
//        return y + 1 - 1.0 / 16;
//    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
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
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((getDamage() & DIRECTION_MASK) >> 3);
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == BIG_DRIPLEAF || id == GRASS_BLOCK || id == DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == CLAY;
    }

    private void setTiltAndScheduleTick(int tilt) {
        setTilt(tilt);
        level.setBlock(this, this, true, false);

        switch (tilt) {
            case TILT_NONE:
                level.scheduleUpdate(this, 1);
                break;
            case TILT_UNSTABLE:
                level.scheduleUpdate(this, 10);
                return;
            case TILT_PARTIAL_TILT:
                level.scheduleUpdate(this, 10);
                break;
            case TILT_FULL_TILT:
                level.scheduleUpdate(this, 100);
                break;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BIG_DRIPLEAF_TILT_DOWN);
    }

    public int getTilt() {
        return getDamage() & TILT_MASK;
    }

    public void setTilt(int tilt) {
        setDamage((getDamage() & ~TILT_MASK) | (tilt & TILT_MASK));
    }

    public boolean isHead() {
        return (getDamage() & HEAD_BIT) == HEAD_BIT;
    }

    public void setHead(boolean head) {
        setDamage(head ? getDamage() | HEAD_BIT : getDamage() & ~HEAD_BIT);
    }
}
