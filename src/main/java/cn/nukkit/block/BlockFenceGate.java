package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGate extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int OPEN_BIT = 0b100;
    public static final int IN_WALL_BIT = 0b1000;

    public BlockFenceGate() {
        this(0);
    }

    public BlockFenceGate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    private static final double[] offMinX = new double[2];
    private static final double[] offMinZ = new double[2];
    private static final double[] offMaxX = new double[2];
    private static final double[] offMaxZ = new double[2];

    static {
        offMinX[0] = 0;
        offMinZ[0] = 0.375;
        offMaxX[0] = 1;
        offMaxZ[0] = 0.625;

        offMinX[1] = 0.375;
        offMinZ[1] = 0;
        offMaxX[1] = 0.625;
        offMaxZ[1] = 1;
    }

    private int getOffsetIndex() {
        return getDamage() & 0x1;
    }

    @Override
    public double getMinX() {
        return this.x + offMinX[getOffsetIndex()];
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ[getOffsetIndex()];
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX[getOffsetIndex()];
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ[getOffsetIndex()];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockFace dir = BlockFace.SOUTH;
        if (player != null) {
            dir = player.getDirection();
        }
        int meta = dir.getHorizontalIndex();
        if (getSide(dir.rotateY()).isWall() || getSide(dir.rotateYCCW()).isWall()) {
            meta |= IN_WALL_BIT;
        }
        this.setDamage(meta);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }

        if (!this.toggle(player)) {
            return false;
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        player = event.getPlayer();

        int meta = getDamage();
        int direction;

        if (player != null) {
            double yaw = player.yaw;
            double rotation = (yaw - 90) % 360;
            if (rotation < 0) {
                rotation += 360.0;
            }

            int originDirection = meta & 0x01;
            if (originDirection == 0) {
                if (rotation >= 0 && rotation < 180) {
                    direction = 2;
                } else {
                    direction = 0;
                }
            } else {
                if (rotation >= 90 && rotation < 270) {
                    direction = 3;
                } else {
                    direction = 1;
                }
            }
        } else {
            direction = meta & DIRECTION_MASK;
        }

        this.setDamage((meta & IN_WALL_BIT) | ((meta & OPEN_BIT) ^ OPEN_BIT) | direction);
        this.level.setBlock(this, this, true, false);

        level.addLevelSoundEvent(blockCenter(), isOpen() ? LevelSoundEventPacket.SOUND_FENCE_GATE_OPEN : LevelSoundEventPacket.SOUND_FENCE_GATE_CLOSE, getFullId());
        return true;
    }

    public boolean isOpen() {
        return (this.getDamage() & OPEN_BIT) != 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int meta = getDamage();
            BlockFace dir = getBlockFace();
            if ((meta & IN_WALL_BIT) != 0) {
                if (getSide(dir.rotateY()).isWall() || getSide(dir.rotateYCCW()).isWall()) {
                    return 0;
                } else {
                    setDamage(meta & ~IN_WALL_BIT);
                    this.level.setBlock(this, this, true, false);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else if (getSide(dir.rotateY()).isWall() || getSide(dir.rotateYCCW()).isWall()) {
                this.setDamage(meta | IN_WALL_BIT);
                this.level.setBlock(this, this, true, false);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            return 0;
        }

        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if ((!isOpen() && this.level.isBlockPowered(this)) || (isOpen() && !this.level.isBlockPowered(this))) {
                this.toggle(null);
                return type;
            }
        }

        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isFenceGate() {
        return true;
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
    public boolean canPassThrough() {
        return isOpen();
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (isOpen()) {
            return null;
        }
        return super.recalculateBoundingBox();
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return this;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }
}
