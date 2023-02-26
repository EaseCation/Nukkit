package cn.nukkit.blockentity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@FunctionalInterface
public interface BlockEntityFactory {
    BlockEntity create(FullChunk chunk, CompoundTag nbt);
}
