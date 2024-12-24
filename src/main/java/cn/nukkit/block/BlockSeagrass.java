package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class BlockSeagrass extends BlockTransparent {

    public static final int DEFAULT_SEAGRASS = 0;
    public static final int DOUBLE_SEAGRASS_TOP = 1;
    public static final int DOUBLE_SEAGRASS_BOTTOM = 2;

    public BlockSeagrass() {
        this(0);
    }

    public BlockSeagrass(int meta) {
        super(meta & 0x3);
    }

    @Override
    public String getName() {
        return "Seagrass";
    }

    @Override
    public int getId() {
        return SEAGRASS;
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
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
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
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (!item.isShears()) {
            return new Item[0];
        }
        return new Item[]{toItem(true)};
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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

        Block below = down();
        if (!canSurvive(below)) {
            return false;
        }

        level.setExtraBlock(this, Block.get(FLOWING_WATER), true, false);

        setDamage(DEFAULT_SEAGRASS);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (getDamage() != DEFAULT_SEAGRASS) {
                return true;
            }

            if (y >= level.getHeightRange().getMaxY() - 1) {
                return true;
            }

            Block up = up();
            if (!up.isWater() || !up.isFullLiquid()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            setDamage(DOUBLE_SEAGRASS_BOTTOM);
            level.setBlock(this, this, true, false);

            level.setExtraBlock(up, Block.get(FLOWING_WATER), true, false);
            level.setBlock(up, Block.get(SEAGRASS, DOUBLE_SEAGRASS_TOP), true);

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block extra = level.getExtraBlock(this);
            if (!extra.isWater() || !extra.isFullLiquid()) {
                level.useBreakOn(this, true);
                return type;
            }

            int meta = getDamage();
            Block below = down();

            if (meta == DOUBLE_SEAGRASS_TOP) {
                if (below.getId() == SEAGRASS && below.getDamage() == DOUBLE_SEAGRASS_BOTTOM) {
                    return 0;
                }
                level.useBreakOn(this, true);
                return type;
            }

            if (meta == DOUBLE_SEAGRASS_BOTTOM) {
                Block up = up();
                if (up.getId() != SEAGRASS || up.getDamage() != DOUBLE_SEAGRASS_TOP) {
                    level.useBreakOn(this, true);
                    return type;
                }
            }

            if (canSurvive(below)) {
                return 0;
            }

            level.useBreakOn(this, true);
            return type;
        }

        return 0;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    private boolean canSurvive(Block below) {
        int id = below.getId();
        return id != MAGMA && id != SOUL_SAND && SupportType.hasFullSupport(below, BlockFace.UP);
    }

    static boolean trySpawnSeaGrass(Block fertilized, Item item, @Nullable Player player) {
        Block up = fertilized.up();
        if (!up.isWaterSource()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        Level level = fertilized.level;
        level.addParticle(new BoneMealParticle(fertilized));

        int thisX = up.getFloorX();
        int thisY = up.getFloorY();
        int thisZ = up.getFloorZ();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        TRY:
        for (int i = 16; i < 64; i++) {
            int x = thisX;
            int y = thisY;
            int z = thisZ;
            for (int j = 0; j < i / 16; j++) {
                x += random.nextInt(-1, 2);
                y += random.nextInt(-1, 2) * random.nextInt(3) / 2;
                z += random.nextInt(-1, 2);
                Block block = level.getBlock(x, y - 1, z);
                int id = block.getId();
                if (id != DIRT && id != SAND && id != GRAVEL && id != CLAY) {
                    continue TRY;
                }
            }

            Block block = level.getBlock(x, y, z);
            if (!block.isWaterSource()) {
                continue;
            }
            Block above = level.getBlock(x, y + 1, z);
            if (!above.isWaterSource()) {
                continue;
            }

            Block placeBlock;
            Block placeAbove = null;
            int num = random.nextInt(8);
            if (num != 0) {
                if (--num != 0) {
                    if (num == 1) {
                        placeBlock = get(CORAL_FAN, random.nextInt(5));
                    } else {
                        placeBlock = get(SEAGRASS);
                    }
                } else {
                    placeBlock = get(BlockCoral.CORALS[random.nextInt(5)]);
                }
            } else {
                placeBlock = get(SEAGRASS, BlockSeagrass.DOUBLE_SEAGRASS_BOTTOM);
                placeAbove = get(SEAGRASS, BlockSeagrass.DOUBLE_SEAGRASS_TOP);
            }

            if (placeAbove != null) {
                level.setExtraBlock(x, y + 1, z, above, true, false);
                level.setBlock(x, y + 1, z, placeAbove, true, false);
            }
            level.setExtraBlock(x, y, z, block, true, false);
            level.setBlock(x, y, z, placeBlock, true, true);
        }
        return true;
    }
}
