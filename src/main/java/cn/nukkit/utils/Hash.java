package cn.nukkit.utils;

import net.openhft.hashing.LongHashFunction;

public final class Hash {

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

    public static long hashBlock(int x, int y, int z) {
        return y + (((long) x & 0x3FFFFFF) << 8) + (((long) z & 0x3FFFFFF) << 34);
    }

    public static int hashBlockX(long triple) {
        return (int) ((((triple >> 8) & 0x3FFFFFF) << 38) >> 38);
    }

    public static int hashBlockY(long triple) {
        return (int) (triple & 0xFF);
    }

    public static int hashBlockZ(long triple) {
        return (int) ((((triple >> 34) & 0x3FFFFFF) << 38) >> 38);
    }

    private Hash() {
        throw new IllegalStateException();
    }
}
