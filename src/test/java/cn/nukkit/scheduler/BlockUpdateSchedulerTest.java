package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class BlockUpdateSchedulerTest {

    private static Block mockBlock(int id) {
        Block block = mock(Block.class);
        when(block.getId()).thenReturn(id);
        return block;
    }

    private static Level mockLoadedLevel() {
        Level level = mock(Level.class);
        when(level.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        return level;
    }

    @Test
    void multiTickBucketsAndPerformDequeueKeepContainsExact() {
        Level level = mockLoadedLevel();
        Block water = mockBlock(Block.FLOWING_WATER);
        Block lava = mockBlock(Block.FLOWING_LAVA);
        Vector3 posA = new Vector3(1, 64, 2);
        Vector3 posB = new Vector3(3, 64, 4);

        when(level.getBlock(posA)).thenReturn(water);
        when(level.getBlock(posB)).thenReturn(lava);

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        BlockUpdateEntry entryA = new BlockUpdateEntry(posA, water, 5, 0);
        BlockUpdateEntry entryB = new BlockUpdateEntry(posB, lava, 10, 0);

        scheduler.add(entryA);
        scheduler.add(entryB);

        assertTrue(scheduler.contains(new BlockUpdateEntry(posA, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(posB, lava)));
        assertFalse(scheduler.contains(new BlockUpdateEntry(posA, lava)));
        assertFalse(scheduler.contains(null));

        scheduler.tick(5);
        assertFalse(scheduler.contains(new BlockUpdateEntry(posA, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(posB, lava)));
        verify(water).onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
        verify(lava, never()).onUpdate(anyInt());

        scheduler.tick(10);
        assertFalse(scheduler.contains(new BlockUpdateEntry(posB, lava)));
        verify(lava).onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
    }

    @Test
    void performAllowsReentrantRescheduleAndUnloadedChunkReschedule() {
        Level level = mock(Level.class);
        Block water = mockBlock(Block.FLOWING_WATER);
        Block unloadedBlock = mockBlock(Block.FLOWING_LAVA);
        Vector3 loadedPos = new Vector3(1, 64, 1);
        Vector3 unloadedPos = new Vector3(32, 64, 32);

        when(level.isChunkLoaded(0, 0)).thenReturn(true);
        when(level.isChunkLoaded(2, 2)).thenReturn(false);
        when(level.getBlock(loadedPos)).thenReturn(water);

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        AtomicBoolean containedDuringOnUpdate = new AtomicBoolean(true);

        when(water.onUpdate(Level.BLOCK_UPDATE_SCHEDULED)).thenAnswer(invocation -> {
            containedDuringOnUpdate.set(scheduler.contains(new BlockUpdateEntry(loadedPos, water)));
            scheduler.add(new BlockUpdateEntry(loadedPos, water, 20, 0));
            return 0;
        });
        doAnswer(invocation -> {
            Block b = invocation.getArgument(0);
            Vector3 p = invocation.getArgument(1);
            BlockUpdateEntry reEntry = new BlockUpdateEntry(p, b, 5, 0);
            if (!scheduler.contains(reEntry)) {
                scheduler.add(reEntry);
            }
            return null;
        }).when(level).scheduleUpdate(any(Block.class), any(Vector3.class), anyInt());

        scheduler.add(new BlockUpdateEntry(loadedPos, water, 5, 0));
        scheduler.add(new BlockUpdateEntry(unloadedPos, unloadedBlock, 5, 0));

        scheduler.tick(5);

        assertFalse(containedDuringOnUpdate.get(), "Current tick bucket must be dequeued from contains() before onUpdate runs");
        assertTrue(scheduler.contains(new BlockUpdateEntry(loadedPos, water)), "Re-scheduled entry during onUpdate must be indexed");
        assertTrue(scheduler.contains(new BlockUpdateEntry(unloadedPos, unloadedBlock)), "Unloaded chunk reschedule must be indexed");
    }

    @Test
    void samePositionDifferentBlocksAreTrackedIndependently() {
        Level level = mockLoadedLevel();
        Block water = mockBlock(Block.FLOWING_WATER);
        Block kelp = mockBlock(Block.KELP);
        Vector3 pos = new Vector3(10, 60, 10);

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        BlockUpdateEntry waterEntry = new BlockUpdateEntry(pos, water, 5, 0);
        BlockUpdateEntry kelpEntry = new BlockUpdateEntry(pos, kelp, 5, 0);

        scheduler.add(waterEntry);
        scheduler.add(kelpEntry);

        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, kelp)));

        assertTrue(scheduler.remove(new BlockUpdateEntry(pos, water)));
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, kelp)));

        assertFalse(scheduler.remove(new BlockUpdateEntry(pos, water)));
        assertTrue(scheduler.remove(new BlockUpdateEntry(pos, kelp)));
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, kelp)));
    }

    @Test
    void duplicateAddsSameBucketAndMultipleBucketsMaintainExactCount() {
        Level level = mockLoadedLevel();
        Block water = mockBlock(Block.FLOWING_WATER);
        Vector3 pos = new Vector3(4, 64, 4);
        when(level.getBlock(pos)).thenReturn(water);

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        // Same bucket twice -> set deduplicates, count must stay 1
        scheduler.add(new BlockUpdateEntry(pos, water, 5, 0));
        scheduler.add(new BlockUpdateEntry(pos, water, 5, 0));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, water)));

        scheduler.tick(5);
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, water)));

        // Two different buckets -> count is 2
        scheduler.add(new BlockUpdateEntry(pos, water, 10, 0));
        scheduler.add(new BlockUpdateEntry(pos, water, 15, 0));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, water)));

        assertTrue(scheduler.remove(new BlockUpdateEntry(pos, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, water)), "Second bucket must still be present");

        scheduler.tick(15);
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, water)), "Both buckets are now gone");
        assertFalse(scheduler.remove(new BlockUpdateEntry(pos, water)));
    }

    @Test
    void exceptionDuringPerformDoesNotLeakIndexForRemainingBucketEntries() {
        Level level = mockLoadedLevel();
        Block throwingBlock = mockBlock(Block.FLOWING_WATER);
        Block secondBlock = mockBlock(Block.FLOWING_LAVA);
        Vector3 posA = new Vector3(1, 64, 1);
        Vector3 posB = new Vector3(2, 64, 2);

        when(level.getBlock(posA)).thenReturn(throwingBlock);
        when(level.getBlock(posB)).thenReturn(secondBlock);
        when(throwingBlock.onUpdate(Level.BLOCK_UPDATE_SCHEDULED)).thenThrow(new IllegalStateException("boom"));

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        scheduler.add(new BlockUpdateEntry(posA, throwingBlock, 3, 0));
        scheduler.add(new BlockUpdateEntry(posB, secondBlock, 3, 0));

        assertThrows(IllegalStateException.class, () -> scheduler.tick(3));
        assertFalse(scheduler.contains(new BlockUpdateEntry(posA, throwingBlock)));
        assertFalse(scheduler.contains(new BlockUpdateEntry(posB, secondBlock)));
    }

    @Test
    void removeByVector3PreservesAsymmetricEqualsSemanticsWithoutIndexDrift() {
        Level level = mockLoadedLevel();
        Block water = mockBlock(Block.FLOWING_WATER);
        Block kelp = mockBlock(Block.KELP);
        Vector3 pos = new Vector3(7, 64, 8);

        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);
        scheduler.add(new BlockUpdateEntry(pos, water, 5, 0));
        scheduler.add(new BlockUpdateEntry(pos, kelp, 5, 0));

        // Standard Vector3.equals(BlockUpdateEntry) is false, matching ObjectLinkedOpenHashSet.remove(pos)
        assertFalse(scheduler.remove((Vector3) null));
        assertFalse(scheduler.remove(new Vector3(7, 64, 8)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, water)));
        assertTrue(scheduler.contains(new BlockUpdateEntry(pos, kelp)));

        // Custom Vector3 whose equals matches BlockUpdateEntry at the same position removes one exact entry at a time
        Vector3 entryMatchingPos = new Vector3(7, 64, 8) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof BlockUpdateEntry) {
                    return super.equals(((BlockUpdateEntry) obj).pos);
                }
                return super.equals(obj);
            }
        };

        assertTrue(scheduler.remove(entryMatchingPos));
        boolean waterLeft = scheduler.contains(new BlockUpdateEntry(pos, water));
        boolean kelpLeft = scheduler.contains(new BlockUpdateEntry(pos, kelp));
        assertTrue(waterLeft ^ kelpLeft, "Exactly one of the two co-located entries must remain indexed");

        assertTrue(scheduler.remove(entryMatchingPos));
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, water)));
        assertFalse(scheduler.contains(new BlockUpdateEntry(pos, kelp)));
        assertFalse(scheduler.remove(entryMatchingPos));
    }

    @Test
    void containsRemainsFastAcrossManyTickBuckets() {
        Level level = mockLoadedLevel();
        Block water = mockBlock(Block.FLOWING_WATER);
        BlockUpdateScheduler scheduler = new BlockUpdateScheduler(level, 0);

        int bucketCount = 5_000;
        for (int i = 1; i <= bucketCount; i++) {
            scheduler.add(new BlockUpdateEntry(new Vector3(i, 64, 0), water, i, 0));
        }

        BlockUpdateEntry missing = new BlockUpdateEntry(new Vector3(-1, 64, -1), water);
        BlockUpdateEntry last = new BlockUpdateEntry(new Vector3(bucketCount, 64, 0), water);

        long startNanos = System.nanoTime();
        for (int i = 0; i < 20_000; i++) {
            assertFalse(scheduler.contains(missing));
            assertTrue(scheduler.contains(last));
        }
        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        assertTrue(elapsedMs < 2_000L, "contains() should be O(1) across 5,000 tick buckets, took " + elapsedMs + "ms");
    }
}
