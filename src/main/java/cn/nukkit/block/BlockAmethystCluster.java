package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockAmethystCluster extends BlockTransparent implements Faceable {
    public static final int FACING_DIRECTION_MASK = 0b111;

    public BlockAmethystCluster() {
        this(0);
    }

    public BlockAmethystCluster(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return AMETHYST_CLUSTER;
    }

    @Override
    public String getName() {
        return "Amethyst Cluster";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getItemDefaultMeta());
    }

    @Override
    public int getItemDefaultMeta() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(Item.AMETHYST_SHARD, 0, item.isPickaxe() ? 4 : 2),
        };
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(face.getIndex());

        if (!canSurvive()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
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
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace()) {
            case DOWN:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 9.0 / 16, this.z + 3.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1, this.z + 1 - 3.0 / 16);
            default:
            case UP:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y, this.z + 3.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1 - 9.0 / 16, this.z + 1 - 3.0 / 16);
            case NORTH:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 3.0 / 16, this.z + 9.0 / 16, this.x + 1 - 3.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1);
            case SOUTH:
                return new SimpleAxisAlignedBB(this.x + 3.0 / 16, this.y + 3.0 / 16, this.z, this.x + 1 - 3.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1 - 9.0 / 16);
            case WEST:
                return new SimpleAxisAlignedBB(this.x + 9.0 / 16, this.y + 3.0 / 16, this.z + 3.0 / 16, this.x + 1, this.y + 1 - 3.0 / 16, this.z + 1 - 3.0 / 16);
            case EAST:
                return new SimpleAxisAlignedBB(this.x, this.y + 3.0 / 16, this.z + 3.0 / 16, this.x + 1 - 9.0 / 16, this.y + 1 - 3.0 / 16, this.z + 1 - 3.0 / 16);
        }
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & FACING_DIRECTION_MASK);
    }

    protected boolean canSurvive() {
        BlockFace facing = getBlockFace();
        return SupportType.hasFullSupport(getSide(facing.getOpposite()), facing);
    }
}
