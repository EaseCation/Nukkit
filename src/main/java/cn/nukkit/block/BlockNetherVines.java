package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockNetherVines extends BlockFlowable {
    public static final int MAX_AGE = 25;

    protected static final float FERTILIZER_GROW_PROBABILITY_DECREASE_RATE = 0.826f;

    protected BlockNetherVines(int meta) {
        super(meta);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        Block side = getSide(getGrowDirection().getOpposite());
        int id = side.getId();
        if (id != getId() && !SupportType.hasFullSupport(side, BlockFace.DOWN)) {
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
            onFertilized();

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));
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

            Vector3 pos = getSideVec(getGrowDirection());
            if (!level.isValidHeight(pos.getFloorY())) {
                return 0;
            }

            if (ThreadLocalRandom.current().nextFloat() >= 0.14f) {
                return 0;
            }

            if (!level.getBlock(pos).isAir()) {
                return 0;
            }

            level.setBlock(pos, get(getId(), age + 1), true);
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.SILK_TOUCH) || ThreadLocalRandom.current().nextInt(3) == 0) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
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

    protected abstract boolean canSurvive();

    protected abstract void onFertilized();

    protected abstract BlockFace getGrowDirection();
}
