package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDripleafSmall extends BlockTransparentMeta implements Faceable {
    public static final int UPPER_BIT = 0b1;
    public static final int DIRECTION_MASK = 0b110;
    public static final int DIRECTION_OFFSET = 1;

    public BlockDripleafSmall() {
        this(0);
    }

    public BlockDripleafSmall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SMALL_DRIPLEAF_BLOCK;
    }

    @Override
    public String getName() {
        return "Small Dripleaf";
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
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
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), UPPER_BIT | 0b110);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!block.isAir() && !block.isWater()) {
            return false;
        }

        Block above = up();
        if (!above.isAir() && !above.isWater()) {
            return false;
        }

        int id = down().getId();
        if (id != GRASS_BLOCK && id != DIRT && id != MYCELIUM && id != PODZOL && id != FARMLAND && id != DIRT_WITH_ROOTS && id != MOSS_BLOCK && id != CLAY && id != MUD && id != MUDDY_MANGROVE_ROOTS) {
            return false;
        }

        int directionBits = 0;
        if (player != null) {
            directionBits = player.getHorizontalFacing().getOpposite().getHorizontalIndex() << DIRECTION_OFFSET;
            setDamage(directionBits);
        }

        if (above.isWater()) {
            level.setExtraBlock(above, above, true, false);
        }
        level.setBlock(above, get(SMALL_DRIPLEAF_BLOCK, directionBits | UPPER_BIT), true, false);

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
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            int maxHeight = level.getHeightRange().getMaxY() - 1;
            int thisX = getFloorX();
            int thisY = getFloorY();
            int thisZ = getFloorZ();
            int baseY = isUpper() ? thisY - 1 : thisY;
            int maxGrowth = ThreadLocalRandom.current().nextInt(4);
            int growth = 0;

            Block[] blocks = new Block[maxGrowth];
            for (; growth < maxGrowth; growth++) {
                int y = baseY + 2 + growth;

                if (y >= maxHeight) {
                    break;
                }

                Block block = level.getBlock(thisX, y, thisZ);
                blocks[growth] = block;

                if (!block.isAir() && !block.isWater()) {
                    break;
                }
            }

            int meta = ((getDamage() & DIRECTION_MASK) >> DIRECTION_OFFSET) << BlockDripleafBig.DIRECTION_OFFSET;
            level.setBlock(thisX, baseY, thisZ, get(BIG_DRIPLEAF, meta), true, false);
            if (growth > 0) {
                level.setBlock(thisX, baseY + 1, thisZ, get(BIG_DRIPLEAF, meta), true, false);
            }

            for (int i = 0; i < growth - 1; i++) {
                int y = baseY + 2 + i;

                Block block = blocks[i];
                if (block.isWater()) {
                    level.setExtraBlock(thisX, y, thisZ, block, true, false);
                }
                level.setBlock(thisX, y, thisZ, get(BIG_DRIPLEAF, meta), true, false);
            }

            int headY = baseY + 1 + growth;
            if (growth > 0) {
                Block block = blocks[growth - 1];
                if (block.isWater()) {
                    level.setExtraBlock(thisX, headY, thisZ, block, true, false);
                }
            }
            level.setBlock(thisX, headY, thisZ, get(BIG_DRIPLEAF, meta | BlockDripleafBig.HEAD_BIT), true, true);
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
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (isUpper()) {
                Block below = down();
                int meta = below.getDamage();
                if (below.getId() == SMALL_DRIPLEAF_BLOCK && (meta & DIRECTION_MASK) == (getDamage() & DIRECTION_MASK) && (meta & UPPER_BIT) != UPPER_BIT) {
                    return 0;
                }

                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            Block above = up();
            int meta = above.getDamage();
            if (above.getId() == SMALL_DRIPLEAF_BLOCK && (meta & DIRECTION_MASK) == (getDamage() & DIRECTION_MASK) && (meta & UPPER_BIT) == UPPER_BIT) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
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
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((getDamage() & DIRECTION_MASK) >> DIRECTION_OFFSET);
    }

    private boolean canSurvive() {
        int id = down().getId();
        if (id == SMALL_DRIPLEAF_BLOCK || id == MOSS_BLOCK || id == CLAY) {
            return true;
        }

        if (!level.getExtraBlock(this).isWater()) {
            return false;
        }
        return id == GRASS_BLOCK || id == DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    public boolean isUpper() {
        return (getDamage() & UPPER_BIT) == UPPER_BIT;
    }

    public void setUpper(boolean upper) {
        setDamage(upper ? getDamage() | UPPER_BIT : getDamage() & ~UPPER_BIT);
    }
}
