package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockCarpetMossPale extends BlockCarpetMoss {
    public static final int UPPER_BLOCK_BIT = 0b1;
    public static final int SIDE_START = 1;

    public static final int SIDE_TYPE_MASK = 0b11;
    public static final int SIDE_TYPE_NONE = 0b00;
    public static final int SIDE_TYPE_SHORT = 0b01;
    public static final int SIDE_TYPE_TALL = 0b10;

    BlockCarpetMossPale() {

    }

    @Override
    public int getId() {
        return PALE_MOSS_CARPET;
    }

    @Override
    public String getName() {
        return "Pale Moss Carpet";
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE | BlockToolType.SWORD;
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
    public boolean isSolid() {
        return !isUpper();
    }

    @Override
    public boolean canPassThrough() {
        return isUpper();
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (isUpper()) {
            return null;
        }
        return this;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (isUpper()) {
            return new Item[0];
        }
        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            boolean upper = isUpper();
            if (upper) {
                return true;
            }

            Block above = up();
            int aboveMeta = 0;
            if (above.getId() == getId()) {
                aboveMeta = above.getDamage();
                if ((aboveMeta & UPPER_BLOCK_BIT) == 0) {
                    return true;
                }
            } else if (!above.isAir()) {
                return true;
            }

            int upperSides = calculateSides(above, true, getDamage() >> SIDE_START, true);
            if (upperSides == 0 || (upperSides << SIDE_START | UPPER_BLOCK_BIT) == aboveMeta) {
                return true;
            }

            int sides = calculateSides(this, false, upperSides, true);
            if (getDamage() == sides << SIDE_START) {
                return true;
            }

            setDamage(sides << SIDE_START);
            level.setBlock(this, this, true, false);
            level.setBlock(above, get(getId(), (upperSides << SIDE_START) | UPPER_BLOCK_BIT), true);

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            boolean upper = isUpper();

            Block below = down();
            if (upper) {
                if (below.getId() != getId() || (below.getDamage() & UPPER_BLOCK_BIT) != 0) {
                    level.useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else if (below.isAir()) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            int anotherSides;
            if (upper) {
                anotherSides = below.getDamage() >> SIDE_START;
            } else {
                Block above = up();
                if (above.getId() == getId() && (above.getDamage() & UPPER_BLOCK_BIT) != 0) {
                    anotherSides = above.getDamage() >> SIDE_START;
                } else {
                    anotherSides = 0;
                }
            }

            int sides = calculateSides(this, upper, anotherSides, !upper);
            if (upper && sides == 0) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            int oldMeta = getDamage();
            int newMeta = (oldMeta & UPPER_BLOCK_BIT) | sides << SIDE_START;
            if (oldMeta == newMeta) {
                return 0;
            }
            setDamage(newMeta);
            level.setBlock(this, this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    private static int calculateSides(Block target, boolean upper, int pairSides, boolean grow) {
        int currentSides = !grow ? target.getDamage() >> SIDE_START : -1;

        int sides = 0;
        for (BlockFace side : BlockFace.Plane.HORIZONTAL) {
            BlockFace opposite = side.getOpposite();
            int offset = opposite.getHorizontalIndex() * 2;

            if (!hasSide(currentSides, offset)) {
                continue;
            }

            if (upper && !hasSide(pairSides, offset)) {
                continue;
            }

            Block block = target.getSide(side);
            if (!block.canProvideSupport(opposite, SupportType.FULL)) {
                continue;
            }

            int sideType;
            if (!upper && hasSide(pairSides, offset)) {
                sideType = SIDE_TYPE_TALL;
            } else {
                sideType = SIDE_TYPE_SHORT;
            }
            sides |= sideType << offset;
        }
        return sides;
    }

    private static boolean hasSide(int sides, int offset) {
        return ((sides >> offset) & SIDE_TYPE_MASK) != SIDE_TYPE_NONE;
    }

    public boolean isUpper() {
        return (getDamage() & UPPER_BLOCK_BIT) != 0;
    }
}
