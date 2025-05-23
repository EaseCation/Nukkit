package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * @author CreeperFace
 */
public abstract class BlockRedstoneDiode extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;

    public BlockRedstoneDiode() {
        this(0);
    }

    public BlockRedstoneDiode(int meta) {
        super(meta);
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
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.level.isRedstoneEnabled()) {
            for (BlockFace face : BlockFace.getValues()) {
                this.level.updateAroundRedstone(this.getSideVec(face), null);
            }
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!SupportType.hasCenterSupport(block.getSide(BlockFace.DOWN), BlockFace.UP)) {
            return false;
        }

        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        this.level.setBlock(block, this, true, true);

        if (this.level.isRedstoneEnabled()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1);
            }
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            if (!this.isLocked()) {
                boolean shouldBePowered = this.shouldBePowered();

                if (this.isPoweredBlock() && !shouldBePowered) {
                    this.level.setBlock(this, this.getUnpowered(), true, true);

                    this.level.updateAroundRedstone(this.getSideVec(getFacing().getOpposite()), null);
                } else if (!this.isPoweredBlock()) {
                    this.level.setBlock(this, this.getPowered(), true, true);
                    this.level.updateAroundRedstone(this.getSideVec(getFacing().getOpposite()), null);

                    if (!shouldBePowered) {
                        level.scheduleUpdate(getPowered(), this, this.getDelay());
                    }
                }
            }
            return type;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (type == Level.BLOCK_UPDATE_NORMAL && !SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            getLevel().getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }

            this.updateState();
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    public void updateState() {
        if (!this.isLocked()) {
            boolean shouldPowered = this.shouldBePowered();

            if ((this.isPoweredBlock() && !shouldPowered || !this.isPoweredBlock() && shouldPowered) && !this.level.isBlockTickPending(this, this)) {
                /*int priority = -1;

                if (this.isFacingTowardsRepeater()) {
                    priority = -3;
                } else if (this.isPowered) {
                    priority = -2;
                }*/

                this.level.scheduleUpdate(this, this, this.getDelay());
            }
        }
    }

    public boolean isLocked() {
        return false;
    }

    protected int calculateInputStrength() {
        BlockFace face = getFacing();
        Vector3 pos = this.getSideVec(face);
        int power = this.level.getRedstonePower(pos, face);

        if (power >= 15) {
            return power;
        } else {
            Block block = this.level.getBlock(pos);
            return Math.max(power, block.getId() == Block.REDSTONE_WIRE ? block.getDamage() : 0);
        }
    }

    protected int getPowerOnSides() {
        BlockFace face = getFacing();
        BlockFace face1 = face.rotateY();
        BlockFace face2 = face.rotateYCCW();
        return Math.max(this.getPowerOnSide(this.getSideVec(face1), face1), this.getPowerOnSide(this.getSideVec(face2), face2));
    }

    protected int getPowerOnSide(Vector3 pos, BlockFace side) {
        Block block = this.level.getBlock(pos);
        return isAlternateInput(block) ? (block.getId() == Block.REDSTONE_BLOCK ? 15 : (block.getId() == Block.REDSTONE_WIRE ? block.getDamage() : this.level.getStrongPower(pos, side))) : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    protected boolean shouldBePowered() {
        return this.calculateInputStrength() > 0;
    }

    public abstract BlockFace getFacing();

    protected abstract int getDelay();

    protected abstract Block getUnpowered();

    protected abstract Block getPowered();

    @Override
    public double getMaxY() {
        return this.y + 0.125;
    }

    protected boolean isAlternateInput(Block block) {
        return block.isPowerSource();
    }

    public static boolean isDiode(Block block) {
        return block instanceof BlockRedstoneDiode;
    }

    protected int getRedstoneSignal() {
        return 15;
    }

    public int getStrongPower(BlockFace side) {
        return getWeakPower(side);
    }

    public int getWeakPower(BlockFace side) {
        return !this.isPowered() ? 0 : (getFacing() == side ? this.getRedstoneSignal() : 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isPowered() {
        return isPoweredBlock();
    }

    protected abstract boolean isPoweredBlock();

    public boolean isFacingTowardsRepeater() {
        BlockFace side = getFacing().getOpposite();
        Block block = this.getSide(side);
        return block instanceof BlockRedstoneDiode && ((BlockRedstoneDiode) block).getFacing() != side;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.125, this.z + 1);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
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

    @Override
    public boolean isDiode() {
        return true;
    }
}
