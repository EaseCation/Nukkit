package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCalibratedSculkSensor;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockSculkSensorCalibrated extends BlockSculkSensor implements Faceable {
    public static final int DIRECTION_MASK = 0b1100;
    public static final int DIRECTION_START = 2;

    public BlockSculkSensorCalibrated() {
        this(0);
    }

    public BlockSculkSensorCalibrated(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CALIBRATED_SCULK_SENSOR;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CALIBRATED_SCULK_SENSOR;
    }

    @Override
    public String getName() {
        return "Calibrated Sculk Sensor";
    }

    @Override
    public int getWeakPower(BlockFace side) {
        BlockFace face = getBlockFace();
        if (side == face) {
            return 0;
        }
        return super.getWeakPower(side);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex((getDamage() & DIRECTION_MASK) >> DIRECTION_START);
    }

    private BlockEntityCalibratedSculkSensor createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CALIBRATED_SCULK_SENSOR);

        return (BlockEntityCalibratedSculkSensor) BlockEntities.createBlockEntity(BlockEntityType.CALIBRATED_SCULK_SENSOR, getChunk(), nbt);
    }

    @Nullable
    private BlockEntityCalibratedSculkSensor getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityCalibratedSculkSensor) {
            return (BlockEntityCalibratedSculkSensor) blockEntity;
        }
        return null;
    }

    @Override
    protected boolean canActive(int vibration) {
        BlockFace face = getBlockFace();
        int inputStrength = level.getRedstonePower(getSidePos(face), face.getOpposite());
        if (inputStrength <= 0) {
            return true;
        }
        return inputStrength == vibration;
    }

    @Override
    protected int getActiveTime() {
        return 10;
    }

    @Override
    public int getPhase() {
        return getDamage() & SCULK_SENSOR_PHASE_MASK;
    }

    @Override
    public void setPhase(int phase) {
        setDamage((getDamage() & ~SCULK_SENSOR_PHASE_MASK) | (phase & SCULK_SENSOR_PHASE_MASK));
    }
}
