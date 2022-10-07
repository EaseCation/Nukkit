package cn.nukkit.level.format.leveldb;

import cn.nukkit.utils.Binary;

public final class LevelDbConstants {
    public static final int SUB_CHUNK_2D_SIZE = 16 * 16;
    public static final int SUB_CHUNK_SIZE = 16 * SUB_CHUNK_2D_SIZE;

    public static final byte FINALISATION_NEEDS_INSTATICKING = 0;
    public static final byte FINALISATION_NEEDS_POPULATION = 1;
    public static final byte FINALISATION_DONE = 2;

    public static final byte CURRENT_STORAGE_VERSION = 8;
    public static final byte CURRENT_LEVEL_CHUNK_VERSION = 7; // ensures vanilla auto-fixes stuff we currently don't
    public static final byte CURRENT_LEVEL_SUBCHUNK_VERSION = 8;

    public static final int CURRENT_NUKKIT_DATA_VERSION = 8;
    public static final long NUKKIT_DATA_MAGIC = 0x20221231fe0100ffL;

    public static final byte[] CHUNK_VERSION_SAVE_DATA = new byte[]{CURRENT_LEVEL_CHUNK_VERSION};
    public static final byte[] FINALISATION_GENERATION_SAVE_DATA = Binary.writeLInt(FINALISATION_NEEDS_INSTATICKING);
    public static final byte[] FINALISATION_POPULATION_SAVE_DATA = Binary.writeLInt(FINALISATION_NEEDS_POPULATION);
    public static final byte[] FINALISATION_DONE_SAVE_DATA = Binary.writeLInt(FINALISATION_DONE);

    private LevelDbConstants() {
        throw new IllegalStateException();
    }
}
