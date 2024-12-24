package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Nullable;

public class BlockEntitySculkSensor extends BlockEntity {
    /**
     * compound.
     */
    private static final String VIBRATION_LISTENER_TAG = "VibrationListener";

    @Getter
    private VibrationListener vibrationListener;

    public BlockEntitySculkSensor(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.SCULK_SENSOR;
    }

    @Override
    protected void initBlockEntity() {
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

    @Data
    public static class VibrationListener {
        /**
         * compound.
         */
        static final String VIBRATION_SELECTOR_TAG = "selector";
        /**
         * int.
         */
        static final String LATEST_RECEIVED_VIBRATION_TAG = "event";
        /**
         * int.
         */
        static final String IN_FLIGHT_VIBRATION_TICKS_TAG = "ticks";
        /**
         * compound.
         */
        static final String IN_FLIGHT_VIBRATION_INFO_TAG = "pending";

        private VibrationSelector selector;

        private int event;

        private int ticks;
        @Nullable
        private VibrationInfo pending;

        public void load(CompoundTag tag) {
            selector = new VibrationSelector();
            if (tag.contains(VIBRATION_SELECTOR_TAG)) {
                selector.load(tag.getCompound(VIBRATION_SELECTOR_TAG));
            }

            if (tag.contains(LATEST_RECEIVED_VIBRATION_TAG)) {
                event = tag.getInt(LATEST_RECEIVED_VIBRATION_TAG);
            }

            if (tag.contains(IN_FLIGHT_VIBRATION_TICKS_TAG)) {
                ticks = tag.getInt(IN_FLIGHT_VIBRATION_TICKS_TAG);
            }
            if (tag.contains(IN_FLIGHT_VIBRATION_INFO_TAG)) {
                pending = new VibrationInfo();
                pending.load(tag.getCompound(IN_FLIGHT_VIBRATION_INFO_TAG));
            }
        }

        public void save(CompoundTag tag) {
            CompoundTag selector = new CompoundTag();
            this.selector.save(selector);
            tag.putCompound(VIBRATION_SELECTOR_TAG, selector);

            if (event != 0) {
                tag.putInt(LATEST_RECEIVED_VIBRATION_TAG, event);
            } else {
                tag.remove(LATEST_RECEIVED_VIBRATION_TAG);
            }

            if (pending != null) {
                tag.putInt(IN_FLIGHT_VIBRATION_TICKS_TAG, ticks);

                CompoundTag pending = new CompoundTag();
                this.pending.save(pending);
                tag.putCompound(IN_FLIGHT_VIBRATION_INFO_TAG, pending);
            } else {
                tag.remove(IN_FLIGHT_VIBRATION_TICKS_TAG);
                tag.remove(IN_FLIGHT_VIBRATION_INFO_TAG);
            }
        }
    }

    @Data
    public static class VibrationSelector {
        /**
         * int.
         */
        static final String FIRST_CANDIDATE_TICK_TAG = "tick";
        /**
         * compound.
         */
        static final String CANDIDATE_VIBRATION_CONTEXT_TAG = "context";

        private int tick = -1;
        @Nullable
        private VibrationInfo context;

        public void load(CompoundTag tag) {
            if (tag.contains(FIRST_CANDIDATE_TICK_TAG)) {
                tick = tag.getInt(FIRST_CANDIDATE_TICK_TAG);
            }
            if (tag.contains(CANDIDATE_VIBRATION_CONTEXT_TAG)) {
                context = new VibrationInfo();
                context.load(tag.getCompound(CANDIDATE_VIBRATION_CONTEXT_TAG));
            }
        }

        public void save(CompoundTag tag) {
            if (tick != -1 && context != null) {
                tag.putInt(FIRST_CANDIDATE_TICK_TAG, -2);

                CompoundTag pending = new CompoundTag();
                this.context.save(pending);
                tag.putCompound(CANDIDATE_VIBRATION_CONTEXT_TAG, pending);
            } else {
                tag.remove(FIRST_CANDIDATE_TICK_TAG);
                tag.remove(CANDIDATE_VIBRATION_CONTEXT_TAG);
            }
        }
    }

    @Data
    public static class VibrationInfo {
        /**
         * float.
         */
        static final String DISTANCE_TAG = "distance";
        /**
         * int.
         */
        static final String POS_X_TAG = "x";
        /**
         * int.
         */
        static final String POS_Y_TAG = "y";
        /**
         * int.
         */
        static final String POS_Z_TAG = "z";
        /**
         * long.
         */
        static final String SOURCE_ID_TAG = "source";
        /**
         * long.
         */
        static final String PROJECTILE_OWNER_ID_TAG = "projectile";
        /**
         * int.
         */
        static final String VIBRATION_TAG = "vibration";

        private float distance;

        private BlockVector3 pos = new BlockVector3();

        /**
         * entity unique id.
         */
        private long source;
        /**
         * entity unique id.
         */
        private long projectile;

        private int vibration;

        public void load(CompoundTag tag) {
            distance = tag.getFloat(DISTANCE_TAG);

            pos.setComponents(tag.getInt(POS_X_TAG), tag.getInt(POS_Y_TAG), tag.getInt(POS_Z_TAG));

            if (tag.contains(SOURCE_ID_TAG)) {
                source = tag.getLong(SOURCE_ID_TAG);
            }
            if (tag.contains(PROJECTILE_OWNER_ID_TAG)) {
                projectile = tag.getLong(PROJECTILE_OWNER_ID_TAG);
            }

            if (tag.contains(VIBRATION_TAG)) {
                vibration = tag.getInt(VIBRATION_TAG);
            }
        }

        public void save(CompoundTag tag) {
            tag.putFloat(DISTANCE_TAG, distance);

            tag.putInt(POS_X_TAG, pos.x);
            tag.putInt(POS_Y_TAG, pos.y);
            tag.putInt(POS_Z_TAG, pos.z);

            if (source != 0) {
                tag.putLong(SOURCE_ID_TAG, source);
            } else {
                tag.remove(SOURCE_ID_TAG);
            }
            if (projectile != 0) {
                tag.putLong(PROJECTILE_OWNER_ID_TAG, projectile);
            } else {
                tag.remove(PROJECTILE_OWNER_ID_TAG);
            }

            if (vibration != 0) {
                tag.putInt(VIBRATION_TAG, vibration);
            } else {
                tag.remove(VIBRATION_TAG);
            }
        }
    }
}
