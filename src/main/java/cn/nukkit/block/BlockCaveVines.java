package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.SharedConstants.*;

public class BlockCaveVines extends BlockFlowable {
    public static final int MAX_AGE = 25;

    public BlockCaveVines() {
        this(0);
    }

    public BlockCaveVines(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAVE_VINES;
    }

    @Override
    public String getName() {
        return "Cave Vines";
    }

    @Override
    public Item toItem(boolean addUserData) {
        if (ENABLE_ITEM_NAME_PERSISTENCE) {
            return Item.get(Item.GLOW_BERRIES);
        }
        return Item.get(getItemId(CAVE_VINES));
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face != BlockFace.DOWN) {
            return false;
        }

        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        if (getDamage() == 0) {
            setDamage(ThreadLocalRandom.current().nextInt(MAX_AGE));
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

            level.setBlock(this, get(down().isCaveVines() ? CAVE_VINES_BODY_WITH_BERRIES : CAVE_VINES_HEAD_WITH_BERRIES, getDamage()), true);
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
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            int age = getDamage();
            if (age >= MAX_AGE) {
                return 0;
            }

            if (!level.isValidHeight(getFloorY() - 1)) {
                return 0;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (random.nextFloat() >= 0.1f) {
                return 0;
            }

            Block below = down();
            if (!below.isAir()) {
                return 0;
            }

            if (getId() == CAVE_VINES_HEAD_WITH_BERRIES) {
                level.setBlock(this, get(CAVE_VINES_BODY_WITH_BERRIES, age), true, false);
            }
            level.setBlock(below, get(random.nextFloat() >= 0.11f ? CAVE_VINES : CAVE_VINES_HEAD_WITH_BERRIES, age + 1), true);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        entity.onGround = true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean isCaveVines() {
        return true;
    }

    protected boolean canSurvive() {
        Block above = up();
        return above.isCaveVines() || SupportType.hasFullSupport(above, BlockFace.DOWN);
    }
}
