package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;

public class ItemSerializer {
    private static RuntimeItemSerializer INSTANCE;

    public static void setSerializer(RuntimeItemSerializer serializer) {
        INSTANCE = Objects.requireNonNull(serializer, "serializer");
    }

    public static CompoundTag serializeItem(Item item) {
        return INSTANCE.serializeItem(item);
    }

    public static CompoundTag serializeItemStack(Item item, int slot) {
        return INSTANCE.serializeItemStack(item, slot);
    }

    public static Item deserialize(CompoundTag tag) {
        return INSTANCE.deserialize(tag);
    }

    public static void registerItem(String identifier, int id) {
        INSTANCE.registerItem(identifier, id);
    }

    public static void registerItem(String identifier, int id, int maxAuxVal) {
        INSTANCE.registerItem(identifier, id, maxAuxVal);
    }

    public static void registerItemAux(String identifier, int id, int meta) {
        INSTANCE.registerItemAux(identifier, id, meta);
    }

    public static void registerCustomItem(String identifier, int id, CompoundTag component) {
        INSTANCE.registerCustomItem(identifier, id, component);
    }

    public static void rebuildRuntimeMapping() {
        INSTANCE.rebuildRuntimeMapping();
    }

    public interface RuntimeItemSerializer {
        CompoundTag serializeItem(Item item);

        CompoundTag serializeItemStack(Item item, int slot);

        Item deserialize(CompoundTag tag);

        void registerItem(String identifier, int id);

        void registerItem(String identifier, int id, int maxAuxVal);

        void registerItemAux(String identifier, int id, int meta);

        void registerCustomItem(String identifier, int id, CompoundTag component);

        void rebuildRuntimeMapping();
    }
}
