package cn.nukkit.nbt.tag;

import java.util.function.Function;

import static cn.nukkit.nbt.tag.Tag.*;

final class Tags {
    static final TagData[] REGISTRY = new TagData[TAG_TYPE_COUNT];

    private static void register(byte type, Function<String, Tag> factory, String name) {
        REGISTRY[type] = new TagData(factory, name);
    }

    static {
        register(TAG_End, name -> EndTag.INSTANCE, "TAG_End");
        register(TAG_Byte, ByteTag::new, "TAG_Byte");
        register(TAG_Short, ShortTag::new, "TAG_Short");
        register(TAG_Int, IntTag::new, "TAG_Int");
        register(TAG_Long, LongTag::new, "TAG_Long");
        register(TAG_Float, FloatTag::new, "TAG_Float");
        register(TAG_Double, DoubleTag::new, "TAG_Double");
        register(TAG_Byte_Array, ByteArrayTag::new, "TAG_Byte_Array");
        register(TAG_Int_Array, IntArrayTag::new, "TAG_Int_Array");
        register(TAG_String, StringTag::new, "TAG_String");
        register(TAG_List, ListTag::new, "TAG_List");
        register(TAG_Compound, CompoundTag::new, "TAG_Compound");
    }

    record TagData(Function<String, Tag> factory, String name) {
    }
}
