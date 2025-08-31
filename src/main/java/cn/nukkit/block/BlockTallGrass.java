package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockTallGrass extends BlockFlowable {
    public static final int TYPE_GRASS = 1;
    public static final int TYPE_FERN = 2;

    private static final String[] NAMES = new String[]{
            "Short Grass",
            "Short Grass",
            "Fern",
            "Fern",
    };

    public BlockTallGrass() {
        this(1);
    }

    public BlockTallGrass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SHORT_GRASS;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            setDamage(getValidType());
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!level.getHeightRange().isValidBlockY(getFloorY() + 1)) {
                return true;
            }

            Block up = this.up();

            if (up.getId() == AIR) {
                int meta;

                switch (this.getValidType()) {
                    default:
                    case TYPE_GRASS:
                        meta = BlockDoublePlant.TYPE_TALL_GRASS;
                        break;
                    case TYPE_FERN:
                        meta = BlockDoublePlant.TYPE_LARGE_FERN;
                        break;
                }

                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                this.level.addParticle(new BoneMealParticle(this));
                this.level.setBlock(this, get(DOUBLE_PLANT, meta), true, false);
                this.level.setBlock(up, get(DOUBLE_PLANT, meta ^ BlockDoublePlant.TOP_HALF_BITMASK), true);
            }

            return true;
        }

        if (item.getId() == getItemId(SNOW_LAYER)) {
            level.setExtraBlock(this, this, true, false);
            level.setBlock(this, Block.get(SNOW_LAYER, BlockSnowLayer.COVERED_BIT), true);

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId(SNOW_LAYER, BlockSnowLayer.COVERED_BIT));
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    Item.get(getItemId(), this.getValidType(), 1)
            };
        }

        if (ThreadLocalRandom.current().nextInt(10) == 0) {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getValidType());
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        if (getValidType() == TYPE_FERN) {
            return 65;
        }
        return 30;
    }

    @Override
    public boolean isPottable() {
        return getValidType() == TYPE_FERN;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS_BLOCK || id == Block.DIRT || id == COARSE_DIRT || id == Block.PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }

    private int getValidType() {
        int meta = getDamage();
        return meta >= 2 && meta <= 3 ? TYPE_FERN : TYPE_GRASS;
    }
}
