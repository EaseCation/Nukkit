package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockKelp extends BlockTransparent {

    public static final int MAX_AGE = 25;

    BlockKelp() {

    }

    @Override
    public String getName() {
        return "Kelp";
    }

    @Override
    public int getId() {
        return KELP;
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
    public boolean isSolid() {
        return false;
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
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.KELP);
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!block.isWater() || !block.isFullLiquid()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        level.setExtraBlock(this, Block.get(FLOWING_WATER), true, false);

        setDamage(ThreadLocalRandom.current().nextInt(MAX_AGE));
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!grow()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
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
    public boolean onBreak(Item item, Player player) {
        if (!super.onBreak(item, player)) {
            return false;
        }

        Block below = down();
        if (below.getId() == KELP) {
            int meta = below.getDamage();
            if (meta == 0) {
                return true;
            }

            below.setDamage(ThreadLocalRandom.current().nextInt(meta));
            level.setBlock(below, below, true, false);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block extra = level.getExtraBlock(this);
            if (!extra.isWater() || !extra.isFullLiquid()) {
                level.useBreakOn(this, true);
                return type;
            }

            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextFloat() >= 0.14f || !grow()) {
                return 0;
            }
            return type;
        }

        return 0;
    }

    private boolean grow() {
        int age = getDamage();
        if (age >= MAX_AGE) {
            return false;
        }

        if (y >= level.getHeightRange().getMaxY() - 1) {
            return false;
        }

        Block up = up();
        if (!up.isWater() || !up.isFullLiquid()) {
            return false;
        }

        level.setExtraBlock(up, Block.get(FLOWING_WATER), true, false);
        level.setBlock(up, Block.get(KELP, age + 1), true);
        return true;
    }

    private boolean canSurvive() {
        Block below = down();
        int id = below.getId();
        return id == KELP || id == MOB_SPAWNER || id != MAGMA && id != SOUL_SAND && (!below.isTransparent() || id == SLIME || id == HONEY_BLOCK);
    }
}
