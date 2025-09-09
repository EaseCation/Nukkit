package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.Mth;

public abstract class BlockHangingSign extends BlockSignPost {
    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int ATTACHED_DIRECTION_MASK = 0b1111_000;
    public static final int ATTACHED_DIRECTION_START = 3;
    public static final int HANGING_BIT = 0b1_0000_000;
    public static final int ATTACHED_BIT = 0b10_0000_000;

    protected BlockHangingSign(int meta) {
        super(meta);
    }

    @Override
    public abstract int getId();

    @Override
    public abstract String getName();

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.HANGING_SIGN;
    }

    @Override
    public int getFuelTime() {
        return 800;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (isHanging()) {
            return null;
        }
        if (getBlockFace().getAxis() == Axis.Z) {
            return shrink(0, 0, 6 / 16f);
        }
        return shrink(6 / 16f, 0, 0);
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        if (isHanging()) {
            return shrink(0, 0, 6 / 16f);
        }
        return recalculateBoundingBox();
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDefaultMeta());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(0);
        if (face == BlockFace.DOWN) {
            setHanging(true);
            if (target.canProvideSupport(BlockFace.DOWN, SupportType.FULL)) {
                if (player != null) {
                    setFacingDirection(player.getHorizontalFacing().getOpposite().getIndex());
                } else {
                    setFacingDirection(2);
                }
            } else if (target.canProvideSupport(BlockFace.DOWN, SupportType.CENTER)) {
                if (player != null) {
                    setAttached(true);
                    setAttachedDirection(Mth.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0xf);
                } else {
                    setFacingDirection(2);
                }
            } else if (target instanceof BlockHangingSign above) {
                if (player != null) {
                    int attachedDirection = Mth.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0xf;
                    if ((attachedDirection & 3) == 0) {
                        BlockFace facing = player.getHorizontalFacing().getOpposite();
                        int dir = facing.getIndex();
                        if (above.isAttached()) {
                            int aboveAttachedDirection = above.getAttachedDirection();
                            if ((aboveAttachedDirection == 0 || aboveAttachedDirection == 8) && (attachedDirection == 0 || attachedDirection == 8)
                                    || (aboveAttachedDirection == 4 || aboveAttachedDirection == 12) && (attachedDirection == 4 || attachedDirection == 12)) {
                                setFacingDirection(dir);
                            } else {
                                setAttached(true);
                                setAttachedDirection(attachedDirection);
                            }
                        } else if (above.getBlockFace().getAxis() == facing.getAxis()) {
                            setFacingDirection(dir);
                        } else {
                            setAttached(true);
                            setAttachedDirection(attachedDirection);
                        }
                    } else {
                        setAttached(true);
                        setAttachedDirection(attachedDirection);
                    }
                }
            } else {
                return false;
            }
        } else if (face == BlockFace.UP) {
            if (player != null) {
                BlockFace facing = player.getHorizontalFacing();
                BlockFace cw = facing.rotateY();
                BlockFace ccw = cw.getOpposite();
                if (getSide(cw).canProvideSupport(ccw, SupportType.FULL) || getSide(ccw).canProvideSupport(cw, SupportType.FULL)) {
                    setFacingDirection(facing.getIndex());
                    if (!isFacingFront(player)) {
                        setFacingDirection(facing.getOpposite().getIndex());
                    }
                } else {
                    BlockFace opposite = facing.getOpposite();
                    if (getSide(facing).canProvideSupport(opposite, SupportType.FULL) || getSide(opposite).canProvideSupport(facing, SupportType.FULL)) {
                        setFacingDirection(cw.getIndex());
                        if (!isFacingFront(player)) {
                            setFacingDirection(ccw.getIndex());
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                setFacingDirection(2);
            }
        } else if (face != null && player != null) {
            if (!target.canProvideSupport(face, SupportType.FULL) && !getSide(face).canProvideSupport(face.getOpposite(), SupportType.FULL)) {
                return false;
            }
            BlockFace cw = face.rotateY();
            setFacingDirection(cw.getIndex());
            if (!isFacingFront(player)) {
                setFacingDirection(cw.getOpposite().getIndex());
            }
        }

        if (!level.setBlock(this, this, true)) {
            return false;
        }
        BlockEntitySign sign = createBlockEntity(item);

        if (player != null) {
            sign.lockedForEditingBy = player;
            player.openSignEditor(getFloorX(), getFloorY(), getFloorZ());
        }
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face == BlockFace.DOWN && item.isHangingSign()) {
            return false;
        }
        return super.onActivate(item, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isHanging()) {
                return 0;
            }
            Block above = up();
            if (!above.isHangingSign() && !above.canProvideSupport(BlockFace.DOWN, SupportType.CENTER)) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getFacingDirection());
    }

    @Override
    public boolean isBlockItem() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public boolean isStandingSign() {
        return false;
    }

    @Override
    public boolean isHangingSign() {
        return true;
    }

    @Override
    protected String getBlockEntityId() {
        return BlockEntity.HANGING_SIGN;
    }

    @Override
    protected float getRotationDegrees() {
        if (isAttached()) {
            return getAttachedDirection() * 360f / 16;
        }

        return switch (getFacingDirection()) {
            case 3 -> 0;
            case 4 -> 90;
            case 5 -> 270;
            default -> 180;
        };
    }

    public int getFacingDirection() {
        return getDamage() & FACING_DIRECTION_MASK;
    }

    public void setFacingDirection(int direction) {
        setDamage((getDamage() & ~(ATTACHED_DIRECTION_MASK | FACING_DIRECTION_MASK)) | direction);
    }

    public int getAttachedDirection() {
        return (getDamage() & ATTACHED_DIRECTION_MASK) >> ATTACHED_DIRECTION_START;
    }

    public void setAttachedDirection(int attachedDirection) {
        setDamage((getDamage() & ~(ATTACHED_DIRECTION_MASK | FACING_DIRECTION_MASK)) | (attachedDirection << ATTACHED_DIRECTION_START));
    }

    public boolean isHanging() {
        return (getDamage() & HANGING_BIT) != 0;
    }

    public void setHanging(boolean hanging) {
        setDamage(hanging ? getDamage() | HANGING_BIT : getDamage() & ~HANGING_BIT);
    }

    public boolean isAttached() {
        return (getDamage() & ATTACHED_BIT) != 0;
    }

    public void setAttached(boolean attached) {
        setDamage(attached ? getDamage() | ATTACHED_BIT : getDamage() & ~ATTACHED_BIT);
    }
}
