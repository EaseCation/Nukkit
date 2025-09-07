package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.Getter;

import java.util.List;

import static cn.nukkit.blockentity.BlockEntitySculkCatalyst.SculkChargeCursor.*;

public class BlockEntitySculkCatalyst extends BlockEntity {
    /**
     * compound-list.
     */
    private static final String CURSORS_TAG = "cursors";

    @Getter
    private List<SculkChargeCursor> cursors; // SculkSpreader

    public BlockEntitySculkCatalyst(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.SCULK_CATALYST;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.SCULK_CATALYST;
    }

    @Override
    protected void initBlockEntity() {
        cursors = new ObjectArrayList<>();

        if (namedTag.contains(CURSORS_TAG)) {
            ListTag<CompoundTag> cursors = namedTag.getList(CURSORS_TAG, CompoundTag.class);
            for (CompoundTag tag : cursors.getAllUnsafe()) {
                SculkChargeCursor cursor = new SculkChargeCursor();
                cursor.pos.setComponents(tag.getInt(POS_X_TAG), tag.getInt(POS_Y_TAG), tag.getInt(POS_Z_TAG));
                cursor.charge = tag.getShort(CHARGE_TAG);
                cursor.update = tag.getShort(UPDATE_DELAY_TAG);
                cursor.decay = tag.getShort(DECAY_DELAY_TAG);
                cursor.facing = tag.getShort(FACING_DATA_TAG);
                this.cursors.add(cursor);
            }
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (!cursors.isEmpty()) {
            ListTag<CompoundTag> cursors = new ListTag<>(CURSORS_TAG);
            for (SculkChargeCursor cursor : this.cursors) {
                cursors.add(new CompoundTag()
                        .putInt(POS_X_TAG, cursor.pos.x)
                        .putInt(POS_Y_TAG, cursor.pos.y)
                        .putInt(POS_Z_TAG, cursor.pos.z)
                        .putShort(CHARGE_TAG, cursor.charge)
                        .putShort(UPDATE_DELAY_TAG, cursor.update)
                        .putShort(DECAY_DELAY_TAG, cursor.decay)
                        .putShort(FACING_DATA_TAG, cursor.facing));
            }
            namedTag.putList(cursors);
        } else {
            namedTag.remove(CURSORS_TAG);
        }
    }

    @Data
    public static class SculkChargeCursor {
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
         * short.
         */
        static final String CHARGE_TAG = "charge";
        /**
         * short.
         */
        static final String UPDATE_DELAY_TAG = "update";
        /**
         * short.
         */
        static final String DECAY_DELAY_TAG = "decay";
        /**
         * short.
         */
        static final String FACING_DATA_TAG = "facing";

        private BlockVector3 pos = new BlockVector3();
        private int charge;
        private int update = 0;
        private int decay = 1;
        private int facing = -1;
    }
}
