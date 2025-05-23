package cn.nukkit.level.format.leveldb;

import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.utils.Binary;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;

public final class LevelDbConstants {
    public static final int SUB_CHUNK_2D_SIZE = 16 * 16;
    public static final int SUB_CHUNK_SIZE = 16 * SUB_CHUNK_2D_SIZE;

    public static final byte FINALISATION_NEEDS_INSTATICKING = 0;
    public static final byte FINALISATION_NEEDS_POPULATION = 1;
    public static final byte FINALISATION_DONE = 2;

    public static final byte CURRENT_STORAGE_VERSION = 10; // 1.19.40
    public static final byte CURRENT_LEVEL_CHUNK_VERSION = 40; // 1.18.30
    public static final byte CURRENT_LEVEL_SUBCHUNK_VERSION = 8;

    private static final byte LATEST_STORAGE_VERSION = 10; // 1.19.40
    private static final byte LATEST_LEVEL_CHUNK_VERSION = 41; // 1.21.40
    private static final byte LATEST_LEVEL_SUBCHUNK_VERSION = 9; // 1.18

    public static final byte[] CHUNK_VERSION_SAVE_DATA = new byte[]{CURRENT_LEVEL_CHUNK_VERSION};
    public static final byte[] FINALISATION_GENERATION_SAVE_DATA = Binary.writeLInt(FINALISATION_NEEDS_INSTATICKING);
    public static final byte[] FINALISATION_POPULATION_SAVE_DATA = Binary.writeLInt(FINALISATION_NEEDS_POPULATION);
    public static final byte[] FINALISATION_DONE_SAVE_DATA = Binary.writeLInt(FINALISATION_DONE);

    public static final List<IntTag> CURRENT_COMPATIBLE_CLIENT_VERSION = Collections.unmodifiableList(ObjectArrayList.of(
            new IntTag("", 1), // major
            new IntTag("", 20), // minor
            new IntTag("", 10), // patch
            new IntTag("", 0), // revision
            new IntTag("", 0))); // beta

    private LevelDbConstants() {
        throw new IllegalStateException();
    }
}
