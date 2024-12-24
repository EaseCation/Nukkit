package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCalibratedSculkSensor extends BlockEntitySculkSensor {
    public BlockEntityCalibratedSculkSensor(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.CALIBRATED_SCULK_SENSOR;
    }
}
