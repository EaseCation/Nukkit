package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LevelDbChunkHeightMapTest {

    @BeforeAll
    static void initializeBlocks() {
        Block.init();
    }

    @Test
    void recalculateHeightMapMatchesIndependentExpectationsAcrossStandardHeightRange() {
        HeightRange range = HeightRange.blockY(0, 256);
        LevelDbChunk chunk = createChunk(range);
        int[] expectedHighestY = new int[256];
        Arrays.fill(expectedHighestY, Integer.MIN_VALUE);

        // Sparse non-empty subchunks with transparent blocks above opaque blocks, boundaries, slabs, and layer-1 water.
        place(chunk, expectedHighestY, 0, 0, 0, Block.STONE);         // localY=0 in chunkY=0
        place(chunk, expectedHighestY, 1, 15, 0, Block.STONE);        // localY=15 in chunkY=0
        place(chunk, expectedHighestY, 2, 16, 0, Block.STONE);        // localY=0 in chunkY=1
        place(chunk, expectedHighestY, 3, 255, 0, Block.STONE);       // localY=15 in top chunkY=15
        place(chunk, expectedHighestY, 4, 64, 5, Block.STONE);
        setSectionBlock(chunk, 0, 4, 128, 5, Block.GLASS);                  // transparent above opaque in higher subchunk
        setSectionBlock(chunk, 0, 4, 200, 5, Block.TORCH);                  // transparent in even higher subchunk
        setSectionBlock(chunk, 0, 5, 100, 5, Block.GLASS);                  // transparent-only column -> remains empty
        place(chunk, expectedHighestY, 6, 48, 7, Block.STONE_SLAB);   // slab is light-blocking
        setSectionBlock(chunk, 1, 7, 192, 8, Block.WATER);                  // layer-1 only must not affect layer-0 heightmap
        place(chunk, expectedHighestY, 8, 32, 9, Block.DIRT);
        place(chunk, expectedHighestY, 8, 96, 9, Block.STONE);        // higher opaque block overrides lower opaque block

        // Corrupt the cached heightmap so recalculateHeightMap must compute everything from blocks.
        Arrays.fill(chunk.heightmap, (short) 123);
        chunk.heightmapOrBiomesDirty = false;
        chunk.setChanged(false);

        chunk.recalculateHeightMap();

        assertTrue(chunk.isHeightmapOrBiomesDirty());
        assertTrue(chunk.hasChanged());
        assertAllColumnsMatch(chunk, range, expectedHighestY);

        // Re-running when heightmap is already accurate must not mark dirty or changed.
        chunk.heightmapOrBiomesDirty = false;
        chunk.setChanged(false);
        chunk.recalculateHeightMap();
        assertFalse(chunk.isHeightmapOrBiomesDirty());
        assertFalse(chunk.hasChanged());
    }

    @Test
    void recalculateHeightMapHandlesNegativeYRangeAndBoundaries() {
        HeightRange range = HeightRange.blockY(-64, 320);
        LevelDbChunk chunk = createChunk(range);
        int[] expectedHighestY = new int[256];
        Arrays.fill(expectedHighestY, Integer.MIN_VALUE);

        place(chunk, expectedHighestY, 0, -64, 0, Block.BEDROCK);     // minY, chunkY=-4, localY=0
        place(chunk, expectedHighestY, 1, -49, 0, Block.STONE);       // chunkY=-4, localY=15
        place(chunk, expectedHighestY, 2, -1, 0, Block.STONE);        // chunkY=-1, localY=15
        place(chunk, expectedHighestY, 3, 0, 0, Block.STONE);         // chunkY=0, localY=0
        place(chunk, expectedHighestY, 4, 319, 0, Block.STONE);       // maxY-1, chunkY=19, localY=15
        place(chunk, expectedHighestY, 5, -64, 15, Block.STONE);
        setSectionBlock(chunk, 0, 5, 10, 15, Block.GLASS);                  // transparent above negative-Y opaque block
        setSectionBlock(chunk, 0, 6, -10, 15, Block.GLASS);                 // transparent-only negative-Y column

        Arrays.fill(chunk.heightmap, (short) 99);
        chunk.heightmapOrBiomesDirty = false;
        chunk.setChanged(false);

        chunk.recalculateHeightMap();

        assertTrue(chunk.isHeightmapOrBiomesDirty());
        assertTrue(chunk.hasChanged());
        assertAllColumnsMatch(chunk, range, expectedHighestY);
    }

    @Test
    void recalculateHeightMapUsesSingleReadLockPerSubChunkAndStopsEarlyOnceAllColumnsFound() {
        HeightRange range = HeightRange.blockY(-64, 320);
        LevelDbChunk chunk = createChunk(range);
        int[] expectedHighestY = new int[256];
        Arrays.fill(expectedHighestY, Integer.MIN_VALUE);

        // Place blocks in a lower subchunk (chunkY=0) and a complete 16x16 roof in chunkY=4 (y=68).
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                setSectionBlock(chunk, 0, x, 5, z, Block.STONE);
                place(chunk, expectedHighestY, x, 68, z, Block.STONE);
            }
        }
        // Also place a single higher pillar at (3, 100, 7) in chunkY=6.
        place(chunk, expectedHighestY, 3, 100, 7, Block.STONE);

        AtomicInteger[] lockCounts = new AtomicInteger[LevelDbChunk.SECTION_COUNT];
        for (int chunkY = range.getMinChunkY(); chunkY < range.getMaxChunkY(); chunkY++) {
            int idx = Level.subChunkYtoIndex(chunkY);
            LevelDbSubChunk subChunk = (LevelDbSubChunk) chunk.getSection(chunkY);
            AtomicInteger counter = new AtomicInteger();
            lockCounts[idx] = counter;
            subChunk.readLock = new CountingLock(subChunk.readLock, counter);
        }

        Arrays.fill(chunk.heightmap, (short) 0);
        chunk.recalculateHeightMap();

        // Every visited subchunk from top down to chunkY=4 must be read-locked exactly once.
        for (int chunkY = range.getMaxChunkY() - 1; chunkY >= 4; chunkY--) {
            assertEquals(1, lockCounts[Level.subChunkYtoIndex(chunkY)].get(),
                    "Expected single readLock acquisition for chunkY=" + chunkY);
        }
        // All 256 columns were resolved at chunkY=4, so lower subchunks (chunkY < 4) must not be touched at all.
        for (int chunkY = 3; chunkY >= range.getMinChunkY(); chunkY--) {
            assertEquals(0, lockCounts[Level.subChunkYtoIndex(chunkY)].get(),
                    "Expected early termination without locking chunkY=" + chunkY);
        }

        // Single-column uncached lookup must also acquire at most 1 readLock per visited subchunk.
        for (AtomicInteger c : lockCounts) {
            if (c != null) {
                c.set(0);
            }
        }
        assertEquals(100, chunk.getHighestBlockAt(3, 7, false));
        for (int chunkY = range.getMaxChunkY() - 1; chunkY >= 6; chunkY--) {
            assertEquals(1, lockCounts[Level.subChunkYtoIndex(chunkY)].get(),
                    "Expected single readLock in getHighestBlockAt for chunkY=" + chunkY);
        }
        for (int chunkY = 5; chunkY >= range.getMinChunkY(); chunkY--) {
            assertEquals(0, lockCounts[Level.subChunkYtoIndex(chunkY)].get(),
                    "Expected getHighestBlockAt to stop at chunkY=6 without locking chunkY=" + chunkY);
        }

        assertAllColumnsMatch(chunk, range, expectedHighestY);
    }

    private static LevelDbChunk createChunk(HeightRange range) {
        LevelProvider provider = mock(LevelProvider.class);
        when(provider.getHeightRange()).thenReturn(range);
        return new LevelDbChunk(provider, 0, 0);
    }

    private static void setSectionBlock(LevelDbChunk chunk, int layer, int x, int y, int z, int blockId) {
        chunk.getSection(y >> 4).setBlockId(layer, x, y & 0xf, z, blockId);
    }

    private static void place(LevelDbChunk chunk, int[] expectedHighestY, int x, int y, int z, int blockId) {
        setSectionBlock(chunk, 0, x, y, z, blockId);
        int index = LevelDbChunk.index2d(x, z);
        if (Block.lightBlocking[blockId] && y > expectedHighestY[index]) {
            expectedHighestY[index] = y;
        }
    }

    private static void assertAllColumnsMatch(LevelDbChunk chunk, HeightRange range, int[] expectedHighestY) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int expectedY = expectedHighestY[LevelDbChunk.index2d(x, z)];
                int expectedHighest = expectedY != Integer.MIN_VALUE ? expectedY : range.getMinY();
                int expectedHeightMap = expectedY != Integer.MIN_VALUE ? expectedY + 1 : range.getMinY();

                assertEquals(expectedHeightMap, chunk.getHeightMap(x, z),
                        "getHeightMap mismatch at (" + x + ", " + z + ")");
                assertEquals(expectedHighest, chunk.getHighestBlockAt(x, z, true),
                        "cached getHighestBlockAt mismatch at (" + x + ", " + z + ")");
                assertEquals(expectedHighest, chunk.getHighestBlockAt(x, z, false),
                        "uncached getHighestBlockAt mismatch at (" + x + ", " + z + ")");
            }
        }
    }

    private static final class CountingLock implements Lock {
        private final Lock delegate;
        private final AtomicInteger lockCount;

        private CountingLock(Lock delegate, AtomicInteger lockCount) {
            this.delegate = delegate;
            this.lockCount = lockCount;
        }

        @Override
        public void lock() {
            lockCount.incrementAndGet();
            delegate.lock();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            lockCount.incrementAndGet();
            delegate.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            lockCount.incrementAndGet();
            return delegate.tryLock();
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            lockCount.incrementAndGet();
            return delegate.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            delegate.unlock();
        }

        @Override
        public Condition newCondition() {
            return delegate.newCondition();
        }
    }
}
