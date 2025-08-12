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
public abstract class BlockWall extends BlockTransparent {
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

    private static final float EPSILON = 0.01f;
    private static final AxisAlignedBB NORTH_TEST = box(3.5f, 0, 0, 12.5f, 16, 3.5f);
    private static final AxisAlignedBB SOUTH_TEST = box(3.5f, 0, 12.5f, 12.5f, 16, 16);
    private static final AxisAlignedBB WEST_TEST = box(0, 0, 3.5f, 3.5f, 16, 12.5f);
    private static final AxisAlignedBB EAST_TEST = box(12.5f, 0, 3.5f, 16, 16, 12.5f);
    private static final AxisAlignedBB POST_TEST = box(7, 0, 7, 9, 16, 9);

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

    private static boolean canConnect(Block block, BlockFace face) {
        if (block.is(BARRIER) || block.is(MELON_BLOCK) || block.isPumpkin()) {
            return false;
        }
        return block.isWall() || block instanceof BlockThin
                || block.isFenceGate() && ((BlockFenceGate) block).getBlockFace().getAxis() == face.rotateY().getAxis()
                || SupportType.hasFullSupport(block, face);
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

            recalculateConnections();

            if (oldMeta != getDamage()) {
                level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    public void recalculateConnections() {
        Block aboveBlock = getSide(BlockFace.UP);
        Block northBlock = getSide(BlockFace.NORTH);
        Block southBlock = getSide(BlockFace.SOUTH);
        Block westBlock = getSide(BlockFace.WEST);
        Block eastBlock = getSide(BlockFace.EAST);

        boolean northConnected = canConnect(northBlock, BlockFace.SOUTH);
        boolean southConnected = canConnect(southBlock, BlockFace.NORTH);
        boolean westConnected = canConnect(westBlock, BlockFace.EAST);
        boolean eastConnected = canConnect(eastBlock, BlockFace.WEST);
        boolean upConnected = false;

        int northType = northConnected ? CONNECTION_TYPE_SHORT : CONNECTION_TYPE_NONE;
        int southType = southConnected ? CONNECTION_TYPE_SHORT : CONNECTION_TYPE_NONE;
        int westType = westConnected ? CONNECTION_TYPE_SHORT : CONNECTION_TYPE_NONE;
        int eastType = eastConnected ? CONNECTION_TYPE_SHORT : CONNECTION_TYPE_NONE;
        boolean post = false;

        AxisAlignedBB outline = aboveBlock.getSelectionBoundingBox();
        if (outline != null) {
            outline = outline.getOffsetBoundingBox(-aboveBlock.getX(), -aboveBlock.getY(), -aboveBlock.getZ());
            if (outline.getMinY() < EPSILON) {
                if (aboveBlock.isWall()) {
                    BlockWall aboveWall = (BlockWall) aboveBlock;
                    if (northConnected && aboveWall.getNorthConnectionType() != CONNECTION_TYPE_NONE) {
                        northType = CONNECTION_TYPE_TALL;
                    }
                    if (southConnected && aboveWall.getSouthConnectionType() != CONNECTION_TYPE_NONE) {
                        southType = CONNECTION_TYPE_TALL;
                    }
                    if (westConnected && aboveWall.getWestConnectionType() != CONNECTION_TYPE_NONE) {
                        westType = CONNECTION_TYPE_TALL;
                    }
                    if (eastConnected && aboveWall.getEastConnectionType() != CONNECTION_TYPE_NONE) {
                        eastType = CONNECTION_TYPE_TALL;
                    }
                    post = aboveWall.isPost();
                    upConnected = true;
                } else {
                    AxisAlignedBB postTest = POST_TEST.clone();
                    if (northConnected && NORTH_TEST.intersectsWithXZ(outline)) {
                        northType = CONNECTION_TYPE_TALL;
                        postTest.setMinZ(0);
                    }
                    if (southConnected && SOUTH_TEST.intersectsWithXZ(outline)) {
                        southType = CONNECTION_TYPE_TALL;
                        postTest.setMaxZ(1);
                    }
                    if (westConnected && WEST_TEST.intersectsWithXZ(outline)) {
                        westType = CONNECTION_TYPE_TALL;
                        postTest.setMinX(0);
                    }
                    if (eastConnected && EAST_TEST.intersectsWithXZ(outline)) {
                        eastType = CONNECTION_TYPE_TALL;
                        postTest.setMaxX(1);
                    }
                    if (postTest.intersectsWithXZ(outline)) {
                        upConnected = true;
                    }
                }
            }
        }

        setNorthConnectionType(northType);
        setSouthConnectionType(southType);
        setWestConnectionType(westType);
        setEastConnectionType(eastType);
        setPost(post || upConnected && (northType != CONNECTION_TYPE_TALL || southType != CONNECTION_TYPE_TALL) && (westType != CONNECTION_TYPE_TALL || eastType != CONNECTION_TYPE_TALL)
                || northConnected != southConnected || westConnected != eastConnected // 1 or 3
                || !northConnected && !westConnected // 0
                || northConnected && westConnected); // 4
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
