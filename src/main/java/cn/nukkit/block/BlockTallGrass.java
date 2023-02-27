package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
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
    public static final int GRASS = 1;
    public static final int FERN = 2;

    private static final String[] NAMES = new String[]{
            "Grass",
            "Grass",
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
        return TALLGRASS;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            Block up = this.up();

            if (up.getId() == AIR) {
                int meta;

                switch (this.getDamage()) {
                    case 0:
                    case 1:
                        meta = BlockDoublePlant.TALL_GRASS;
                        break;
                    case 2:
                    case 3:
                        meta = BlockDoublePlant.LARGE_FERN;
                        break;
                    default:
                        meta = -1;
                }

                if (meta != -1) {
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }

                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.setBlock(this, get(DOUBLE_PLANT, meta), true, false);
                    this.level.setBlock(up, get(DOUBLE_PLANT, meta ^ BlockDoublePlant.TOP_HALF_BITMASK), true);
                }
            }

            return true;
        }

        if (item.getId() == getItemId(SNOW_LAYER)) {
            level.setExtraBlock(this, this, true, false);
            level.setBlock(this, Block.get(SNOW_LAYER), true);

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, SNOW_LAYER << Block.BLOCK_META_BITS);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropSeeds = ThreadLocalRandom.current().nextInt(10) == 0;
        if (item.isShears()) {
            //todo enchantment
            if (dropSeeds) {
                return new Item[]{
                        Item.get(Item.WHEAT_SEEDS),
                        Item.get(Item.TALLGRASS, this.getDamage(), 1)
                };
            } else {
                return new Item[]{
                        Item.get(Item.TALLGRASS, this.getDamage(), 1)
                };
            }
        }

        if (dropSeeds) {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
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
    public boolean isVegetation() {
        return true;
    }

    private boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS || id == Block.DIRT || id == Block.PODZOL || id == FARMLAND || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK;
    }
}
