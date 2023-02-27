package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * @author CreeperFace
 */
public class BlockTripWireHook extends BlockTransparentMeta implements Faceable {

    public BlockTripWireHook() {
        this(0);
    }

    public BlockTripWireHook(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Tripwire Hook";
    }

    @Override
    public int getId() {
        return TRIPWIRE_HOOK;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
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
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!SupportType.hasFullSupport(this.getSide(face.getOpposite()), face)) {
                this.level.useBreakOn(this);
            }

            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.calculateState(false, true, -1, null);
            return type;
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face.isVertical() || !SupportType.hasFullSupport(this.getSide(face.getOpposite()), face)) {
            return false;
        }

        this.setFace(face);

        this.level.setBlock(this, this, true);

        if (player != null) {
            this.calculateState(false, false, -1, null);
        }
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        super.onBreak(item);
        boolean attached = isAttached();
        boolean powered = isPowered();

        if (attached || powered) {
            this.calculateState(true, false, -1, null);
        }

        if (powered) {
            this.level.updateAroundRedstone(this, null);
            this.level.updateAroundRedstone(this.getSideVec(getBlockFace().getOpposite()), null);
        }

        return true;
    }

    public void calculateState(boolean onBreak, boolean updateAround, int pos, Block block) {
        if (!this.level.isRedstoneEnabled()) {
            return;
        }

        BlockFace facing = getBlockFace();
        boolean attached = isAttached();
        boolean powered = isPowered();
        boolean canConnect = !onBreak;
        boolean nextPowered = false;
        int distance = 0;
        Block[] blocks = new Block[42];

        for (int i = 1; i < 42; ++i) {
            Vector3 vector = this.getSideVec(facing, i);
            Block b = this.level.getBlock(vector);

            if (b instanceof BlockTripWireHook) {
                if (((BlockTripWireHook) b).getBlockFace() == facing.getOpposite()) {
                    distance = i;
                }
                break;
            }

            if (b.getId() != Block.TRIP_WIRE && i != pos) {
                blocks[i] = null;
                canConnect = false;
            } else {
                if (i == pos) {
                    b = block != null ? block : b;
                }

                if (b instanceof BlockTripWire) {
                    boolean disarmed = !((BlockTripWire) b).isDisarmed();
                    boolean wirePowered = ((BlockTripWire) b).isPowered();
                    nextPowered |= disarmed && wirePowered;

                    if (i == pos) {
                        this.level.scheduleUpdate(this, 10);
                        canConnect &= disarmed;
                    }
                }
                blocks[i] = b;
            }
        }

        canConnect = canConnect & distance > 1;
        nextPowered = nextPowered & canConnect;
        BlockTripWireHook hook = (BlockTripWireHook) Block.get(BlockID.TRIPWIRE_HOOK);
        hook.setAttached(canConnect);
        hook.setPowered(nextPowered);


        if (distance > 0) {
            Vector3 vec = this.getSideVec(facing, distance);
            BlockFace face = facing.getOpposite();
            hook.setFace(face);
            this.level.setBlock(vec, hook, true, false);
            this.level.updateAroundRedstone(vec, null);
            this.level.updateAroundRedstone(vec.getSideVec(face.getOpposite()), null);
            this.addSound(vec, canConnect, nextPowered, attached, powered);
        }

        this.addSound(this, canConnect, nextPowered, attached, powered);

        if (!onBreak) {
            hook.setFace(facing);
            this.level.setBlock(this, hook, true, false);

            if (updateAround) {
                this.level.updateAroundRedstone(this, null);
                this.level.updateAroundRedstone(this.getSideVec(facing.getOpposite()), null);
            }
        }

        if (attached != canConnect) {
            for (int i = 1; i < distance; i++) {
                Vector3 vc = this.getSideVec(facing, i);
                block = blocks[i];

                if (block != null && this.level.getBlockIdAt(0, vc.getFloorX(), vc.getFloorY(), vc.getFloorZ()) != Block.AIR) {
                    if (canConnect ^ ((block.getDamage() & 0x04) > 0)) {
                        block.setDamage(block.getDamage() ^ 0x04);
                    }

                    this.level.setBlock(vc, block, true, false);
                }
            }
        }
    }

    private void addSound(Vector3 pos, boolean canConnect, boolean nextPowered, boolean attached, boolean powered) {
        if (nextPowered && !powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_ON, (this.getId() << BLOCK_META_BITS) | this.getDamage());
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        } else if (!nextPowered && powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_OFF, (this.getId() << BLOCK_META_BITS) | this.getDamage());
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
        } else if (canConnect && !attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_ATTACH);
        } else if (!canConnect && attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_DETACH);
        }
    }

    public boolean isAttached() {
        return (getDamage() & 0x04) > 0;
    }

    public boolean isPowered() {
        return (this.getDamage() & 0x08) > 0;
    }

    public void setPowered(boolean value) {
        if (value ^ this.isPowered()) {
            this.setDamage(this.getDamage() ^ 0x08);
        }
    }

    public void setAttached(boolean value) {
        if (value ^ this.isAttached()) {
            this.setDamage(this.getDamage() ^ 0x04);
        }
    }

    public void setFace(BlockFace face) {
        this.setDamage(this.getDamage() - this.getDamage() % 4);
        this.setDamage(this.getDamage() | face.getHorizontalIndex());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isPowered() ? 0 : getBlockFace() == side ? 15 : 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
