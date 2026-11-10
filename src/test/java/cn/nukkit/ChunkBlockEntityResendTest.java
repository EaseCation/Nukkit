package cn.nukkit;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkCachedData;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 验证区块发送后方块实体数据的补发规则，以及方块实体推送新数据时会让区块网络缓存失效。
 */
class ChunkBlockEntityResendTest {

    @Test
    void pushingBlockEntityDataInvalidatesChunkNetworkCache() {
        BlockEntitySpawnable blockEntity = mock(BlockEntitySpawnable.class, CALLS_REAL_METHODS);
        FullChunk chunk = mock(FullChunk.class);
        Level level = mock(Level.class);
        blockEntity.chunk = chunk;
        blockEntity.level = level;
        when(chunk.getX()).thenReturn(3);
        when(chunk.getZ()).thenReturn(4);
        when(level.getChunkPlayers(3, 4)).thenReturn(new Int2ObjectOpenHashMap<>());

        blockEntity.spawnToAll();

        verify(chunk).invalidateNetworkCache();
    }

    @Test
    void dataBuiltFromCurrentRevisionIsNotStale() {
        Player player = playerIn(levelWithChunkRevision(7));

        assertFalse(player.isChunkNetworkDataStale(3, 4, cachedData(7)));
    }

    @Test
    void dataBuiltFromOlderRevisionIsStale() {
        Player player = playerIn(levelWithChunkRevision(8));

        assertTrue(player.isChunkNetworkDataStale(3, 4, cachedData(7)));
    }

    @Test
    void unloadedChunkIsNotReportedStale() {
        Level level = mock(Level.class);
        when(level.getChunkIfLoaded(3, 4)).thenReturn(null);
        Player player = playerIn(level);

        assertFalse(player.isChunkNetworkDataStale(3, 4, cachedData(7)));
    }

    @Test
    void subChunkResendOnlyCoversRequestedSubChunks() {
        Level level = mock(Level.class);
        Player player = playerIn(level);
        BlockEntitySpawnable inside = mock(BlockEntitySpawnable.class);
        BlockEntitySpawnable outside = mock(BlockEntitySpawnable.class);
        BlockEntity notSpawnable = mock(BlockEntity.class);
        when(inside.getSubChunkY()).thenReturn(2);
        when(outside.getSubChunkY()).thenReturn(5);
        when(notSpawnable.getSubChunkY()).thenReturn(2);
        Map<Long, BlockEntity> blockEntities = new LinkedHashMap<>();
        blockEntities.put(1L, inside);
        blockEntities.put(2L, outside);
        blockEntities.put(3L, notSpawnable);
        when(level.getChunkBlockEntities(3, 4)).thenReturn(blockEntities);

        assertTrue(player.spawnSubChunkBlockEntities(3, 4, IntSet.of(2), null));

        verify(inside).spawnTo(player);
        verify(outside, never()).spawnTo(player);
    }

    @Test
    void fullChunkResendCoversEverySpawnableBlockEntity() {
        Level level = mock(Level.class);
        Player player = playerIn(level);
        BlockEntitySpawnable first = mock(BlockEntitySpawnable.class);
        BlockEntitySpawnable second = mock(BlockEntitySpawnable.class);
        Map<Long, BlockEntity> blockEntities = new LinkedHashMap<>();
        blockEntities.put(1L, first);
        blockEntities.put(2L, mock(BlockEntity.class));
        blockEntities.put(3L, second);
        when(level.getChunkBlockEntities(3, 4)).thenReturn(blockEntities);

        assertTrue(player.spawnChunkBlockEntities(3, 4, null));

        verify(first).spawnTo(player);
        verify(second).spawnTo(player);
    }

    private static Player playerIn(Level level) {
        Player player = mock(Player.class, CALLS_REAL_METHODS);
        player.level = level;
        return player;
    }

    private static Level levelWithChunkRevision(long networkRevision) {
        Level level = mock(Level.class);
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        when(chunk.getNetworkRevision()).thenReturn(networkRevision);
        when(level.getChunkIfLoaded(3, 4)).thenReturn(chunk);
        return level;
    }

    private static ChunkCachedData cachedData(long networkRevision) {
        return new ChunkCachedData(0, new byte[0], new byte[0][], new boolean[0], null, null, networkRevision);
    }
}
