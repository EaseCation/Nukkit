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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
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
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && !player.isCreative()) {
                item.pop();
            }

            if (item.is(Item.RAPID_FERTILIZER) || player != null && player.isCreative() || ThreadLocalRandom.current().nextFloat() < 0.4f) {
                grow();
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
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == PODZOL || id == MYCELIUM || id == FARMLAND || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS
                || id == CRIMSON_NYLIUM || id == WARPED_NYLIUM || id == SOUL_SOIL;
    }

    protected abstract boolean grow();
}
