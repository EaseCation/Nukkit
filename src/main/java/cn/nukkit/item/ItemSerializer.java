package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;

public class ItemSerializer {
    private static RuntimeItemSerializer INSTANCE;

    public static void setSerializer(RuntimeItemSerializer serializer) {
        INSTANCE = Objects.requireNonNull(serializer, "serializer");
    }

    public static CompoundTag serializeItem(@Nullable Item item) {
        return INSTANCE.serializeItem(item);
    }

    public static CompoundTag serializeItemStack(@Nullable Item item, int slot) {
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

    public static void registerCustomItem(String fullName, int id, @Nullable BiFunction<Integer, Boolean, CompoundTag> componentsSupplier) {
        INSTANCE.registerCustomItem(fullName, id, componentsSupplier);
    }

    public static void registerCustomBlockItem(String fullName, int itemId) {
        INSTANCE.registerCustomBlockItem(fullName, itemId);
    }

    public static void rebuildRuntimeMapping() {
        INSTANCE.rebuildRuntimeMapping();
    }

    public interface RuntimeItemSerializer {
        CompoundTag serializeItem(@Nullable Item item);

        CompoundTag serializeItemStack(@Nullable Item item, int slot);

        Item deserialize(CompoundTag tag);

        void registerItem(String identifier, int id);

        void registerItem(String identifier, int id, int maxAuxVal);

        void registerCustomItem(String fullName, int id, @Nullable BiFunction<Integer, Boolean, CompoundTag> componentsSupplier);

        void registerCustomBlockItem(String fullName, int itemId);

        void rebuildRuntimeMapping();
    }
}
