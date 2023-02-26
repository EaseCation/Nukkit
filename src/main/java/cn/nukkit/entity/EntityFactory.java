package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@FunctionalInterface
public interface EntityFactory {
    Entity create(FullChunk chunk, CompoundTag nbt);
}
