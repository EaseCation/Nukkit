package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntitySculkSensor.VibrationListener;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

public class BlockEntitySculkShrieker extends BlockEntity {
    /**
     * int.
     */
    private static final String QUEUED_RESPONSE_THREAT_LEVEL_TAG = "QueuedResponseThreatLevel";
    /**
     * compound.
     */
    private static final String VIBRATION_LISTENER_TAG = "VibrationListener";

    @Getter
    @Setter
    private int queuedResponseThreatLevel;

    @Getter
    private VibrationListener vibrationListener;

    public BlockEntitySculkShrieker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.SCULK_SHRIEKER;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains(QUEUED_RESPONSE_THREAT_LEVEL_TAG)) {
            queuedResponseThreatLevel = namedTag.getInt(QUEUED_RESPONSE_THREAT_LEVEL_TAG);
        } else {
            queuedResponseThreatLevel = 0;
        }

        vibrationListener = new VibrationListener();
        if (namedTag.contains(VIBRATION_LISTENER_TAG)) {
            vibrationListener.load(namedTag.getCompound(VIBRATION_LISTENER_TAG));
        } else {
            vibrationListener.load(new CompoundTag());
        }

        super.initBlockEntity();

//        this.scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (queuedResponseThreatLevel != 0) {
            namedTag.putInt(QUEUED_RESPONSE_THREAT_LEVEL_TAG, queuedResponseThreatLevel);
        } else {
            namedTag.remove(QUEUED_RESPONSE_THREAT_LEVEL_TAG);
        }

        CompoundTag vibrationListener = new CompoundTag();
        this.vibrationListener.save(vibrationListener);
        namedTag.putCompound(VIBRATION_LISTENER_TAG, vibrationListener);
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        //TODO: vibration listener
        return true;
    }
}
