package cn.nukkit.level.format.generic;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class BaseRegionLoader implements AutoCloseable {
    public static final int VERSION = 1;
    public static final byte COMPRESSION_GZIP = 1;
    public static final byte COMPRESSION_ZLIB = 2;
    public static final int MAX_SECTOR_LENGTH = 256 << 12;
    public static final int COMPRESSION_LEVEL = 7;

    protected int x;
    protected int z;
    protected int lastSector;
    protected LevelProvider levelProvider;

    private RandomAccessFile randomAccessFile;

    // TODO: A simple array will perform better and use less memory
    protected final Int2ObjectMap<Integer[]> locationTable = new Int2ObjectOpenHashMap<>();

    public long lastUsed;

    public BaseRegionLoader(LevelProvider level, int regionX, int regionZ, String ext) {
        try {
            this.x = regionX;
            this.z = regionZ;
            this.levelProvider = level;
            String filePath = (this.levelProvider.getPath().endsWith("/") ? this.levelProvider.getPath() : this.levelProvider.getPath() + "/") + "region/r." + regionX + "." + regionZ + "." + ext;
            File file = new File(filePath);
            boolean exists = file.exists();
            if (!exists) {
                file.createNewFile();
            }
            // TODO: buffering is a temporary solution to chunk reading/writing being poorly optimized
            //  - need to fix the code where it reads single bytes at a time from disk
            this.randomAccessFile = new RandomAccessFile(filePath, "rw");
            if (!exists) {
                this.createBlank();
            } else {
                this.loadLocationTable();
            }

            this.lastUsed = System.currentTimeMillis();
        } catch (IOException e) {
            try {
                if (this.randomAccessFile != null) this.randomAccessFile.close();
            } catch (IOException e0) {
                log.warn("Level BaseRegionLoader close error", e);
            }
            throw new RuntimeException("Level BaseRegionLoader throws IOException", e);
        }
    }

    public void compress() {
        // TODO
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    protected abstract boolean isChunkGenerated(int index);

    public abstract BaseFullChunk readChunk(int x, int z) throws IOException;

    protected abstract BaseFullChunk unserializeChunk(byte[] data);

    public abstract boolean chunkExists(int x, int z);

    protected abstract void saveChunk(int x, int z, byte[] chunkData) throws IOException;

    public abstract void removeChunk(int x, int z);

    public abstract void writeChunk(FullChunk chunk) throws Exception;

    public void close() throws IOException {
        if (randomAccessFile != null) randomAccessFile.close();
    }

    protected abstract void loadLocationTable() throws IOException;

    public abstract int doSlowCleanUp() throws Exception;

    protected abstract void writeLocationIndex(int index) throws IOException;

    protected abstract void createBlank() throws IOException;

    public abstract int getX();

    public abstract int getZ();

    public Integer[] getLocationIndexes() {
        return this.locationTable.keySet().toArray(new Integer[0]);
    }

}
