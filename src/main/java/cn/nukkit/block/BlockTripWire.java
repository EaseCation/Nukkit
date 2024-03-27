package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * @author CreeperFace
 */
public class BlockTripWire extends BlockTransparentMeta {

    public BlockTripWire(int meta) {
        super(meta);
    }

    public BlockTripWire() {
        this(0);
    }

    @Override
    public int getId() {
        return TRIP_WIRE;
    }

    @Override
    public String getName() {
        return "Tripwire";
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
    public float getResistance() {
        return 0;
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.STRING);
    }

    public boolean isPowered() {
        return (this.getDamage() & 1) > 0;
    }

    public boolean isAttached() {
        return (this.getDamage() & 4) > 0;
    }

    public boolean isDisarmed() {
        return (this.getDamage() & 8) > 0;
    }

    public void setPowered(boolean value) {
        if (value ^ this.isPowered()) {
            this.setDamage(this.getDamage() ^ 0x01);
        }
    }

    public void setAttached(boolean value) {
        if (value ^ this.isAttached()) {
            this.setDamage(this.getDamage() ^ 0x04);
        }
    }

    public void setDisarmed(boolean value) {
        if (value ^ this.isDisarmed()) {
            this.setDamage(this.getDamage() ^ 0x08);
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!this.level.isRedstoneEnabled()) {
            return;
        }

        if (!entity.doesTriggerPressurePlate()) {
            return;
        }

        boolean powered = this.isPowered();

        if (!powered) {
            this.setPowered(true);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);

            this.level.scheduleUpdate(this, 10);
        }
    }

    public void updateHook(boolean scheduleUpdate) {
        if (!this.level.isRedstoneEnabled()) {
            return;
        }

        for (BlockFace side : new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST}) {
            for (int i = 1; i < 42; ++i) {
                Block block = this.getSide(side, i);

                if (block instanceof BlockTripWireHook) {
                    BlockTripWireHook hook = (BlockTripWireHook) block;

                    if (hook.getBlockFace() == side.getOpposite()) {
                        hook.calculateState(false, true, i, this);
                    }

                    /*if(scheduleUpdate) {
                        this.level.scheduleUpdate(hook, 10);
                    }*/
                    break;
                }

                if (block.getId() != Block.TRIP_WIRE) {
                    break;
                }
            }
        }
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isPowered()) {
                return type;
            }

            boolean found = false;
            for (Entity entity : this.level.getCollidingEntities(this.getCollisionBoundingBox())) {
                if (!entity.doesTriggerPressurePlate()) {
                    continue;
                }

                found = true;
            }

            if (found) {
                this.level.scheduleUpdate(this, 10);
            } else {
                this.setPowered(false);
                this.level.setBlock(this, this, true, false);
                this.updateHook(false);
            }
            return type;
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(this, this, true, true);
        this.updateHook(false);

        return true;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (item.getId() == Item.SHEARS) {
            this.setDisarmed(true);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        } else {
            this.setPowered(true);
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
            this.updateHook(true);
        }

        return true;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
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
