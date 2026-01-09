package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public abstract class BlockSapling extends BlockFlowable {
    public static final int[] SAPLINGS = {
            OAK_SAPLING,
            SPRUCE_SAPLING,
            BIRCH_SAPLING,
            JUNGLE_SAPLING,
            ACACIA_SAPLING,
            DARK_OAK_SAPLING,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    /**
     * placeholder
     */
    public static final int BIRCH_TALL = 8 | BIRCH;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    @Deprecated
    public static final int TYPE_MASK = 0b111;
    public static final int AGE_BIT = 0b1;

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (!item.is(Item.RAPID_FERTILIZER) && (player == null || !player.isCreative()) && ThreadLocalRandom.current().nextFloat() >= 0.45f) {
                return true;
            }

            this.grow();

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
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1) {
                if (this.isAge()) {
                    this.grow();
                } else {
                    this.setAge(true);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    protected abstract void grow();

    protected boolean findSaplings(int x, int z) {
        return this.isSameType(this.add(x, 0, z))
                && this.isSameType(this.add(x + 1, 0, z))
                && this.isSameType(this.add(x, 0, z + 1))
                && this.isSameType(this.add(x + 1, 0, z + 1));
    }

    private boolean isSameType(Vector3 pos) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId();
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
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

    private boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS_BLOCK || id == Block.DIRT || id == COARSE_DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    public boolean isAge() {
        return (getDamage() & AGE_BIT) != 0;
    }

    public void setAge(boolean age) {
        setDamage(age ? AGE_BIT : 0);
    }
}
