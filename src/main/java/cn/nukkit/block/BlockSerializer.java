package cn.nukkit.block;

import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;

public class BlockSerializer {
    private static BlockInstanceSerializer INSTANCE_SERIALIZER;
    private static RuntimeBlockSerializer RUNTIME_SERIALIZER;

    public static void setInstanceSerializer(BlockInstanceSerializer serializer) {
        Objects.requireNonNull(serializer, "serializer");
        INSTANCE_SERIALIZER = serializer;
    }

    public static void setRuntimeSerializer(RuntimeBlockSerializer serializer) {
        Objects.requireNonNull(serializer, "serializer");
        RUNTIME_SERIALIZER = serializer;
    }

    public static CompoundTag serialize(Block block) {
        return INSTANCE_SERIALIZER.serialize(block);
    }

    public static Block deserialize(CompoundTag tag) {
        return INSTANCE_SERIALIZER.deserialize(tag);
    }

    public static CompoundTag serializeRuntime(int fullId) {
        return RUNTIME_SERIALIZER.serialize(fullId);
    }

    public static int deserializeRuntime(CompoundTag tag) {
        return RUNTIME_SERIALIZER.deserialize(tag);
    }

    public interface BlockInstanceSerializer {
        CompoundTag serialize(Block block);

        Block deserialize(CompoundTag tag);
    }

    public interface RuntimeBlockSerializer {
        CompoundTag serialize(int fullId);

        int deserialize(CompoundTag tag);
    }
}