package cn.nukkit.utils;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import net.openhft.hashing.LongHashFunction;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public final class Hash {
    private static final int FNV1_32_INIT = 0x811c9dc5;
    private static final int FNV1_PRIME_32 = 0x01000193;

    private static final LongHashFunction XXH64 = LongHashFunction.xx();

    public static long xxh64(byte[] buffer) {
        return XXH64.hashBytes(buffer);
    }

    public static long xxh64(byte[] buffer, int offset, int length) {
        return XXH64.hashBytes(buffer, offset, length);
    }

    public static long xxh64(String string) {
        return XXH64.hashChars(string);
    }

    public static long xxh64(byte value) {
        return XXH64.hashByte(value);
    }

    public static long xxh64(short value) {
        return XXH64.hashShort(value);
    }

    public static long xxh64(char value) {
        return XXH64.hashChar(value);
    }

    public static long xxh64(int value) {
        return XXH64.hashInt(value);
    }

    public static long xxh64(long value) {
        return XXH64.hashLong(value);
    }

    public static long xxh64Void() {
        return XXH64.hashVoid();
    }

    private static int fnv1a_32(byte... data) {
        int hash = FNV1_32_INIT;
        for (byte datum : data) {
            hash ^= datum & 0xff;
            hash *= FNV1_PRIME_32;
        }
        return hash;
    }

    public static long hashBlock(CompoundTag block) {
        String name = block.getString("name");

        if ("minecraft:unknown".equals(name)) {
            return -2;
        }

        try {
            return fnv1a_32(NBTIO.write(new CompoundTag(new LinkedHashMap<>())
                    .putString("name", name)
                    .putCompound("states", new CompoundTag(new TreeMap<>(block.getCompound("states").getTagsUnsafe()))), ByteOrder.LITTLE_ENDIAN));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long hashBlockPos(int x, int y, int z) {
        return y + (((long) x & 0x3FFFFFF) << 8) + (((long) z & 0x3FFFFFF) << 34);
    }

    public static int hashBlockPosX(long triple) {
        return (int) ((((triple >> 8) & 0x3FFFFFF) << 38) >> 38);
    }

    public static int hashBlockPosY(long triple) {
        return (int) (triple & 0xFF);
    }

    public static int hashBlockPosZ(long triple) {
        return (int) ((((triple >> 34) & 0x3FFFFFF) << 38) >> 38);
    }

    private Hash() {
        throw new IllegalStateException();
    }
}
