package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBamboo extends BlockTransparentMeta {

    public static final int STALK_THICKNESS_BIT = 0b1;
    public static final int LEAF_SIZE_MASK = 0b110;
    public static final int AGE_BIT = 0b1000;

    public static final int NO_LEAVES = 0;
    public static final int SMALL_LEAVES = 1;
    public static final int LARGE_LEAVES = 2;

    public BlockBamboo() {
        this(0);
    }

    public BlockBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO;
    }

    @Override
    public String getName() {
        return "Bamboo";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SWORD;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double inset = 1 - (isThick() ? 3 / 16.0 : 2 / 16.0);
        AxisAlignedBB aabb = new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1 - inset, this.y + 1, this.z + 1 - inset); // northwest corner, not the center

        int seed = Mth.getSeed(getFloorX(), 0, getFloorZ());
        return aabb.offset(((seed % 12) + 1) / 16.0, 0, (((seed >> 8) % 12) + 1) / 16.0);
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater() || !canBeSupportedBy(down().getId())) {
            return false;
        }
        return level.setBlock(this, Block.get(BAMBOO_SAPLING), true);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        int id = item.getId();
        if (id == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            BlockBamboo top = seekToTop();
            int y = top.getFloorY();
            if (y >= 255 || !top.grow(getMaxHeight(getFloorX(), getFloorZ()), y == 254 ? 1 : ThreadLocalRandom.current().nextInt(1, 3))) {
                return false;
            }
            //FIXME: unexpected update

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }

        if (id == getItemId()) {
            BlockBamboo top = seekToTop();
            if (top.getFloorY() >= 255 || !top.grow(Short.MAX_VALUE, 1)) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            int id = down.getId();
            if (id == BAMBOO || canBeSupportedBy(id)) {
                return 0;
            }

            level.useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getFloorY() == level.getMaxHeight()) {
                return 0;
            }

            if (isReady()) {
                if (/*level.getFullLight(this) < 9 ||*/ !grow(getMaxHeight(getFloorX(), getFloorZ()), 1)) { //TODO: light
                    setReady(false);
                    level.setBlock(this, this, true);
                }
                return type;
            }

            if (up().canBeReplaced()) {
                setReady(true);
                level.setBlock(this, this, true);
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean grow(int maxHeight, int growAmount) {
        Block up = up();
        if (!up.canBeReplaced()) {
            return false;
        }

        int height = 1;
        while (down(height).getId() == BAMBOO) {
            if (++height > maxHeight) {
                return false;
            }
        }

        int newHeight = height + growAmount;
        int stemMeta = getDamage() & ~(AGE_BIT | LEAF_SIZE_MASK);
        if (newHeight > 4) {
            stemMeta |= STALK_THICKNESS_BIT;
        }
        int smallLeavesMeta = stemMeta | (SMALL_LEAVES << 1);
        int largeLeavesMeta = stemMeta | (LARGE_LEAVES << 1);

        int x = getFloorX();
        int y = getFloorY();
        int z = getFloorZ();
        int newTop = y + growAmount;
        switch (newHeight) {
            case 2:
                level.setBlock(x, newTop, z, Block.get(BAMBOO, smallLeavesMeta), true, true);
                break;
            case 3:
                level.setBlock(x, newTop, z, Block.get(BAMBOO, smallLeavesMeta), true, true);
                level.setBlock(x, newTop - 1, z, Block.get(BAMBOO, smallLeavesMeta), true, false);
                break;
            case 4:
                level.setBlock(x, newTop, z, Block.get(BAMBOO, largeLeavesMeta), true, true);
                level.setBlock(x, newTop - 1, z, Block.get(BAMBOO, smallLeavesMeta), true, false);
                level.setBlock(x, newTop - 2, z, Block.get(BAMBOO, stemMeta), true, false);
                level.setBlock(x, newTop - 3, z, Block.get(BAMBOO, stemMeta), true, false);
                break;
            default:
                if (newHeight <= 4) {
                    return false;
                }

                level.setBlock(x, newTop, z, Block.get(BAMBOO, largeLeavesMeta), true, true);
                level.setBlock(x, newTop - 1, z, Block.get(BAMBOO, largeLeavesMeta), true, false);
                level.setBlock(x, newTop - 2, z, Block.get(BAMBOO, smallLeavesMeta), true, false);
                int topStem = newTop - 3;
                int stemCount = Math.min(growAmount, newHeight - 3);
                for (int i = 0; i <= stemCount; i++) {
                    level.setBlock(x, topStem - i, z, Block.get(BAMBOO, stemMeta), true, false);
                }
                break;
        }
        return true;
    }

    private BlockBamboo seekToTop() {
        Block top = this;
        Block up;
        while ((up = top.up()).getId() == BAMBOO) {
            top = up;
        }
        return (BlockBamboo) top;
    }

    private static int getMaxHeight(int x, int z) {
        return 12 + Mth.getSeed(x, 0, z) % 5;
    }

    public boolean isReady() {
        return (getDamage() & AGE_BIT) == AGE_BIT;
    }

    public void setReady(boolean ready) {
        setDamage(ready ? getDamage() | AGE_BIT : getDamage() & ~AGE_BIT);
    }

    public boolean isThick() {
        return (getDamage() & AGE_BIT) == AGE_BIT;
    }

    public void setThick(boolean thick) {
        setDamage(thick ? getDamage() | STALK_THICKNESS_BIT : getDamage() & ~STALK_THICKNESS_BIT);
    }

    static boolean canBeSupportedBy(int id) {
        return id == GRASS || id == DIRT || id == SAND || id == GRAVEL || id == MYCELIUM || id == PODZOL;
    }
}