package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

public class ThreadCache {

    public static final ThreadLocal<int[]> intCache256 = ThreadLocal.withInitial(() -> new int[256]);

    public static final ThreadLocal<FastByteArrayOutputStream> fbaos = ThreadLocal.withInitial(() -> new FastByteArrayOutputStream(1024));
}
