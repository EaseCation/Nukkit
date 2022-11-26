package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockNetherFungus extends BlockFlowable {
    protected BlockNetherFungus() {
        super(0);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
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

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                grow();
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }

    protected boolean canSurvive() {
        int id = down().getId();
        return id == GRASS || id == DIRT || id == PODZOL || id == MYCELIUM || id == FARMLAND
                || id == CRIMSON_NYLIUM || id == WARPED_NYLIUM || id == SOUL_SOIL;
    }

    protected abstract boolean grow();
}
