package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockDoublePlant extends BlockFlowable {
    public static final int[] DOUBLE_PLANTS = {
            SUNFLOWER,
            LILAC,
            TALL_GRASS,
            LARGE_FERN,
            ROSE_BUSH,
            PEONY,
    };

    public static final int TYPE_SUNFLOWER = 0;
    public static final int TYPE_LILAC = 1;
    public static final int TYPE_TALL_GRASS = 2;
    public static final int TYPE_LARGE_FERN = 3;
    public static final int TYPE_ROSE_BUSH = 4;
    public static final int TYPE_PEONY = 5;

    @Deprecated
    public static final int TYPE_MASK = 0b111;
    public static final int UPPER_BLOCK_BIT = 0b1;

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isUpper()) {
                Block below = down();
                if (below.getId() != getId() || below.getDamage() == UPPER_BLOCK_BIT) {
                    this.getLevel().useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                Block above;
                if (!canSurvive() || (above = up()).getId() != getId() || above.getDamage() != UPPER_BLOCK_BIT) {
                    this.getLevel().useBreakOn(this, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        if (!level.getHeightRange().isValidBlockY(getFloorY() + 1)) {
            return false;
        }

        Block up = up();
        if (up.canBeReplaced()) {
            setUpper(false);
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above
            this.getLevel().setBlock(up, Block.get(getId(), UPPER_BLOCK_BIT), true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (isUpper()) {
            Block down = down();
            if (down.getId() == getId() && down.getDamage() != UPPER_BLOCK_BIT) {
                level.addParticle(new DestroyBlockParticle(down, down));
                this.getLevel().setBlock(down, Block.get(BlockID.AIR), true, true);
            }
        } else {
            Block above = up();
            if (above.getId() == getId() && above.getDamage() == UPPER_BLOCK_BIT) {
                level.addParticle(new DestroyBlockParticle(above, above));
                this.getLevel().setBlock(above, Block.get(BlockID.AIR), true, true);
            }
        }
        return super.onBreak(item, player);
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }
            this.level.addParticle(new BoneMealParticle(this));
            this.level.dropItem(this, this.toItem());
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean isDoublePlant() {
        return true;
    }

    public boolean isUpper() {
        return (getDamage() & UPPER_BLOCK_BIT) != 0;
    }

    public void setUpper(boolean value) {
        setDamage(value ? UPPER_BLOCK_BIT : 0);
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == COARSE_DIRT || id == PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
