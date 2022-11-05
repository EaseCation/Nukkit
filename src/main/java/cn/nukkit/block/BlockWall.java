package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockWall extends BlockTransparentMeta {
    public static final int POST_BIT = 0b1;
    public static final int NORTH_CONNECTION_TYPE_MASK = 0b11_0;
    public static final int NORTH_CONNECTION_TYPE_OFFSET = 1;
    public static final int EAST_CONNECTION_TYPE_MASK = 0b11_00_0;
    public static final int EAST_CONNECTION_TYPE_OFFSET = 3;
    public static final int SOUTH_CONNECTION_TYPE_MASK = 0b11_00_00_0;
    public static final int SOUTH_CONNECTION_TYPE_OFFSET = 5;
    public static final int WEST_CONNECTION_TYPE_MASK = 0b11_00_00_00_0;
    public static final int WEST_CONNECTION_TYPE_OFFSET = 7;

    public static final int CONNECTION_TYPE_MASK = 0b11;
    public static final int CONNECTION_TYPE_NONE = 0b00;
    public static final int CONNECTION_TYPE_SHORT = 0b01;
    public static final int CONNECTION_TYPE_TALL = 0b10;

    protected BlockWall(int meta) {
        super(meta);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = getNorthConnectionType() != CONNECTION_TYPE_NONE;
        boolean south = getSouthConnectionType() != CONNECTION_TYPE_NONE;
        boolean west = getWestConnectionType() != CONNECTION_TYPE_NONE;
        boolean east = getEastConnectionType() != CONNECTION_TYPE_NONE;

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;

        if (north && south && !west && !east) {
            w = 0.3125;
            e = 0.6875;
        } else if (!north && !south && west && east) {
            n = 0.3125;
            s = 0.6875;
        }

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    public boolean canConnect(Block block, BlockFace face) {
        return block.isWall() || block.isFenceGate() || SupportType.hasFullSupport(block, face);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face.isVertical() && type == SupportType.CENTER;
    }

    @Override
    public boolean isWall() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int oldMeta = getDamage();

            //TODO: post and short wall

            if (canConnect(getSide(BlockFace.NORTH), BlockFace.SOUTH)) {
                setNorthConnectionType(CONNECTION_TYPE_TALL);
            } else {
                setNorthConnectionType(CONNECTION_TYPE_NONE);
            }
            if (canConnect(getSide(BlockFace.SOUTH), BlockFace.NORTH)) {
                setSouthConnectionType(CONNECTION_TYPE_TALL);
            } else {
                setSouthConnectionType(CONNECTION_TYPE_NONE);
            }
            if (canConnect(getSide(BlockFace.WEST), BlockFace.EAST)) {
                setWestConnectionType(CONNECTION_TYPE_TALL);
            } else {
                setWestConnectionType(CONNECTION_TYPE_NONE);
            }
            if (canConnect(getSide(BlockFace.EAST), BlockFace.WEST)) {
                setEastConnectionType(CONNECTION_TYPE_TALL);
            } else {
                setEastConnectionType(CONNECTION_TYPE_NONE);
            }

            if (oldMeta != getDamage()) {
                level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    public boolean isPost() {
        return (getDamage() & POST_BIT) == POST_BIT;
    }

    public void setPost(boolean post) {
        setDamage(post ? getDamage() | POST_BIT : getDamage() & ~POST_BIT);
    }

    public int getNorthConnectionType() {
        return getConnectionType(NORTH_CONNECTION_TYPE_OFFSET);
    }

    public void setNorthConnectionType(int type) {
        setConnectionType(NORTH_CONNECTION_TYPE_OFFSET, type);
    }

    public int getEastConnectionType() {
        return getConnectionType(EAST_CONNECTION_TYPE_OFFSET);
    }

    public void setEastConnectionType(int type) {
        setConnectionType(EAST_CONNECTION_TYPE_OFFSET, type);
    }

    public int getSouthConnectionType() {
        return getConnectionType(SOUTH_CONNECTION_TYPE_OFFSET);
    }

    public void setSouthConnectionType(int type) {
        setConnectionType(SOUTH_CONNECTION_TYPE_OFFSET, type);
    }

    public int getWestConnectionType() {
        return getConnectionType(WEST_CONNECTION_TYPE_OFFSET);
    }

    public void setWestConnectionType(int type) {
        setConnectionType(WEST_CONNECTION_TYPE_OFFSET, type);
    }

    protected int getConnectionType(int bitOffset) {
        return (getDamage() >> bitOffset) & CONNECTION_TYPE_MASK;
    }

    protected void setConnectionType(int bitOffset, int type) {
        setDamage((getDamage() & ~(CONNECTION_TYPE_MASK << bitOffset)) | ((type & CONNECTION_TYPE_MASK) << bitOffset));
    }
}
