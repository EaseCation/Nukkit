package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

public class BlockSculkSensor extends BlockTransparent {
    public static final int SCULK_SENSOR_PHASE_INACTIVE = 0;
    public static final int SCULK_SENSOR_PHASE_ACTIVE = 1;
    public static final int SCULK_SENSOR_PHASE_COOLDOWN = 2;
    public static final int SCULK_SENSOR_PHASE_MASK = 0b11;

    public BlockSculkSensor() {
        this(0);
    }

    public BlockSculkSensor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCULK_SENSOR;
    }

    @Override
    public String getName() {
        return "Sculk Sensor";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.SCULK_SENSOR;
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
    public int getLightLevel() {
        return 1;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public int getDropExp() {
        return 5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return y + 0.5;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 9 / 16f, z + 1);
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SCULK_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int phase = getPhase();
            if (phase == SCULK_SENSOR_PHASE_ACTIVE) {
                setPhase(SCULK_SENSOR_PHASE_COOLDOWN);
                level.setBlock(this, this, true);

                level.scheduleUpdate(this, 10);
                return type;
            }
            if (phase == SCULK_SENSOR_PHASE_COOLDOWN) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SCULK_SENSOR_POWER_OFF);

                setPhase(SCULK_SENSOR_PHASE_INACTIVE);
                level.setBlock(this, this, true);
                return type;
            }
            return 0;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (getPhase() != SCULK_SENSOR_PHASE_INACTIVE) {
            return;
        }

        if (!canActive(15)) {
            return;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SCULK_SENSOR_POWER_ON);

        setPhase(SCULK_SENSOR_PHASE_ACTIVE);
        level.setBlock(this, this, true);

        level.scheduleUpdate(this, getActiveTime());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return getWeakPower(side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return isActive() ? 1 : 0;
    }

    private BlockEntitySculkSensor createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SCULK_SENSOR);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntitySculkSensor) BlockEntities.createBlockEntity(BlockEntityType.SCULK_SENSOR, getChunk(), nbt);
    }

    @Nullable
    private BlockEntitySculkSensor getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntitySculkSensor) {
            return (BlockEntitySculkSensor) blockEntity;
        }
        return null;
    }

    protected boolean canActive(int vibration) {
        return true;
    }

    protected int getActiveTime() {
        return 30;
    }

    public int getPhase() {
        return getDamage();
    }

    public void setPhase(int phase) {
        setDamage(phase);
    }

    public boolean isActive() {
        return getPhase() == SCULK_SENSOR_PHASE_ACTIVE;
    }
}
