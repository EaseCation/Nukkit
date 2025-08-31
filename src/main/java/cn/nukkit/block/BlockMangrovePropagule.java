package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.tree.ObjectMangroveTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockMangrovePropagule extends BlockTransparent {
    public static final int STAGE_MASK = 0b111;
    public static final int HANGING_BIT = 0b1000;

    public static final int STAGE_FULLY_GROWN = 4;

    public BlockMangrovePropagule() {
        this(0);
    }

    public BlockMangrovePropagule(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_PROPAGULE;
    }

    @Override
    public String getName() {
        return "Mangrove Propagule";
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
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (isHanging() && getStage() < STAGE_FULLY_GROWN) {
            return new Item[0];
        }

        return new Item[]{
                toItem(true),
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(0);

        if (!canSurvive()) {
            return false;
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
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!isHanging()) {
                if (player != null && !player.isCreative()) {
                    item.pop();
                }

                level.addParticle(new BoneMealParticle(this));

                if (!item.is(Item.RAPID_FERTILIZER) && (player == null || !player.isCreative()) && ThreadLocalRandom.current().nextFloat() >= 0.45f) {
                    return true;
                }

                growTree();
                return true;
            }

            int stage = getStage();
            if (stage >= STAGE_FULLY_GROWN) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            setStage(stage + 1);
            level.setBlock(this, this, true);
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
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isHanging()) {
                if (ThreadLocalRandom.current().nextInt(7) != 0) {
                    return 0;
                }

                growTree();
                return Level.BLOCK_UPDATE_RANDOM;
            }

            int stage = getStage();
            if (stage >= STAGE_FULLY_GROWN) {
                return 0;
            }

            setStage(stage + 1);
            level.setBlock(this, this, true);
            return Level.BLOCK_UPDATE_RANDOM;
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
    public int getFuelTime() {
        return 100;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isPottable() {
        return true;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean isSapling() {
        return true;
    }

    private void growTree() {
        new ObjectMangroveTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current());
    }

    private boolean canSurvive() {
        if (isHanging()) {
            return up().getId() == MANGROVE_LEAVES;
        }

        int id = down().getId();
        return id == BIG_DRIPLEAF || id == GRASS_BLOCK || id == DIRT || id == COARSE_DIRT || id == MYCELIUM || id == PODZOL || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == CLAY || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    public int getStage() {
        return getDamage() & STAGE_MASK;
    }

    public void setStage(int stage) {
        setDamage((getDamage() & ~STAGE_MASK) | (stage & STAGE_MASK));
    }

    public boolean isHanging() {
        return (getDamage() & HANGING_BIT) == HANGING_BIT;
    }

    public void setHanging(boolean hanging) {
        setDamage(hanging ? getDamage() | HANGING_BIT : getDamage() & ~HANGING_BIT);
    }
}
