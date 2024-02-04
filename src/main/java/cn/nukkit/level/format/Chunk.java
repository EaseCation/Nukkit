package cn.nukkit.level.format;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Chunk extends FullChunk {
    byte SECTION_COUNT = 16;

    boolean isSectionEmpty(int chunkY);

    ChunkSection getSection(int chunkY);

    boolean setSection(int chunkY, ChunkSection section);

    ChunkSection[] getSections();

    default long getChanges() {
        return 0;
    }
}
