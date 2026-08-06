package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkCachedData;
import cn.nukkit.level.format.generic.ChunkPacketCache;
import cn.nukkit.level.format.generic.ChunkRequestTask;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.ServerScheduler;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 验证区块网络请求在成功之外的所有已知终态都不会泄漏列锁。
 */
class SubChunkRequestLivenessTest {

    @Test
    void explicitMissingChunkFailureNeverEntersLevelQueueOrLock() throws Exception {
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();

        setField(level, "provider", provider);
        setField(level, "chunkSendQueue", chunkSendQueue);
        setField(level, "subChunkSendQueue", subChunkSendQueue);
        setField(level, "chunkSendTasks", chunkSendTasks);
        when(player.getLoaderId()).thenReturn(7);
        when(provider.getChunk(52, 29, false)).thenReturn(null);

        assertFalse(level.requestSubChunks(52, 29, player, 0));
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        assertTrue(chunkSendTasks.isEmpty());
    }

    @Test
    void acceptedRequestThenActualUnloadSettlesColumnAndAllowsRetry() throws Exception {
        int chunkX = 4;
        int chunkZ = -9;
        long index = Level.chunkHash(chunkX, chunkZ);

        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        BaseFullChunk firstChunk = mock(BaseFullChunk.class);
        BaseFullChunk secondChunk = mock(BaseFullChunk.class);
        Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
        chunks.put(index, firstChunk);

        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();
        setLevelRuntimeFields(level, server, provider, chunks, chunkSendQueue, subChunkSendQueue, chunkSendTasks);

        when(player.getLoaderId()).thenReturn(17);
        when(provider.getChunk(chunkX, chunkZ, false)).thenAnswer(invocation -> chunks.get(index));
        when(provider.requestChunkTask(chunkX, chunkZ)).thenReturn(null);

        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 0));
        assertTrue(level.unloadChunk(chunkX, chunkZ, true, false));
        assertFalse(chunks.containsKey(index));

        invokeProcessChunkRequest(level);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(provider, times(1)).requestChunkTask(chunkX, chunkZ);
        verify(player, times(1)).onSubChunkRequestFail(0, chunkX, chunkZ);

        chunks.put(index, secondChunk);
        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 0));
        invokeProcessChunkRequest(level);

        verify(provider, times(2)).requestChunkTask(chunkX, chunkZ);
        verify(player, times(2)).onSubChunkRequestFail(0, chunkX, chunkZ);
        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
    }

    @Test
    void repeatedActualUnloadsSettleEveryColumn() throws Exception {
        int width = 12;
        int height = 12;
        int requestedColumns = width * height;

        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();
        setLevelRuntimeFields(level, server, provider, chunks, chunkSendQueue, subChunkSendQueue, chunkSendTasks);

        when(player.getLoaderId()).thenReturn(23);
        when(provider.getChunk(anyInt(), anyInt(), anyBoolean())).thenAnswer(invocation ->
                chunks.get(Level.chunkHash(invocation.getArgument(0), invocation.getArgument(1))));
        when(provider.requestChunkTask(anyInt(), anyInt())).thenReturn(null);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                long index = Level.chunkHash(x, z);
                chunks.put(index, mock(BaseFullChunk.class));
                assertTrue(level.requestSubChunks(x, z, player, 0));
                assertTrue(level.unloadChunk(x, z, true, false));
            }
        }
        assertTrue(chunks.isEmpty());

        invokeProcessChunkRequest(level);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(provider, times(requestedColumns)).requestChunkTask(anyInt(), anyInt());
        verify(player, times(requestedColumns)).onSubChunkRequestFail(eq(0), anyInt(), anyInt());

        invokeProcessChunkRequest(level);
        verify(provider, times(requestedColumns)).requestChunkTask(anyInt(), anyInt());
    }

    @Test
    void failureCallbackSettlesQueuedStateExactlyOnce() throws Exception {
        int chunkX = 11;
        int chunkZ = 13;
        long index = Level.chunkHash(chunkX, chunkZ);
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();

        setField(level, "provider", provider);
        setField(level, "chunkSendQueue", chunkSendQueue);
        setField(level, "subChunkSendQueue", subChunkSendQueue);
        setField(level, "chunkSendTasks", chunkSendTasks);
        when(player.getLoaderId()).thenReturn(29);
        when(provider.getChunk(chunkX, chunkZ, false)).thenReturn(mock(BaseFullChunk.class));

        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 2));
        assertTrue(chunkSendTasks.add(index));

        level.chunkRequestFailureCallback(chunkX, chunkZ);
        level.chunkRequestFailureCallback(chunkX, chunkZ);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(player, times(1)).onSubChunkRequestFail(2, chunkX, chunkZ);
    }

    @Test
    void subChunkOnlySerializationKeepsOwnershipUntilFailureCallback() throws Exception {
        int chunkX = 19;
        int chunkZ = -7;
        long index = Level.chunkHash(chunkX, chunkZ);
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        ServerScheduler scheduler = mock(ServerScheduler.class);
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        ChunkCachedData cachedData = mock(ChunkCachedData.class);
        ChunkPacketCache packetCache = mock(ChunkPacketCache.class);
        AsyncTask<?> task = mock(AsyncTask.class);
        Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
        chunks.put(index, chunk);
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();
        setLevelRuntimeFields(level, server, provider, chunks, chunkSendQueue, subChunkSendQueue, chunkSendTasks);

        when(server.getScheduler()).thenReturn(scheduler);
        when(player.getLoaderId()).thenReturn(31);
        when(player.isConnected()).thenReturn(true);
        when(player.getBlockVersion()).thenReturn(GlobalBlockPaletteInterface.StaticVersion.V1_26_20);
        when(provider.getChunk(chunkX, chunkZ, false)).thenReturn(chunk);
        when(provider.requestChunkTask(chunkX, chunkZ)).thenReturn(task);
        when(chunk.getCachedData()).thenReturn(cachedData);
        when(cachedData.getPacketCache()).thenReturn(packetCache);
        when(packetCache.hasRequested(GlobalBlockPaletteInterface.StaticVersion.V1_26_20)).thenReturn(false);

        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 0));
        invokeProcessChunkRequest(level);

        assertTrue(chunkSendTasks.contains(index));
        assertTrue(chunkSendQueue.containsKey(index));
        assertTrue(subChunkSendQueue.containsKey(index));
        verify(scheduler, times(1)).scheduleAsyncTask(isNull(), same(task));

        level.chunkRequestFailureCallback(chunkX, chunkZ);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(player, times(1)).onSubChunkRequestFail(0, chunkX, chunkZ);
    }

    @Test
    void taskCreationFailureSettlesColumnAndNotifiesWaiter() throws Exception {
        int chunkX = -3;
        int chunkZ = 8;
        long index = Level.chunkHash(chunkX, chunkZ);
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
        chunks.put(index, chunk);
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();
        setLevelRuntimeFields(level, server, provider, chunks, chunkSendQueue, subChunkSendQueue, chunkSendTasks);

        when(player.getLoaderId()).thenReturn(37);
        when(provider.getChunk(chunkX, chunkZ, false)).thenReturn(chunk);
        when(provider.requestChunkTask(chunkX, chunkZ)).thenThrow(new IllegalStateException("forced task creation failure"));

        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 1));
        invokeProcessChunkRequest(level);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(player, times(1)).onSubChunkRequestFail(1, chunkX, chunkZ);
    }

    @Test
    void taskSchedulingFailureSettlesColumnAndNotifiesWaiter() throws Exception {
        int chunkX = 6;
        int chunkZ = 15;
        long index = Level.chunkHash(chunkX, chunkZ);
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        LevelProvider provider = mock(LevelProvider.class);
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        ServerScheduler scheduler = mock(ServerScheduler.class);
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        AsyncTask<?> task = mock(AsyncTask.class);
        Map<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
        chunks.put(index, chunk);
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue = new Long2ObjectOpenHashMap<>();
        LongSet chunkSendTasks = new LongOpenHashSet();
        setLevelRuntimeFields(level, server, provider, chunks, chunkSendQueue, subChunkSendQueue, chunkSendTasks);

        when(server.getScheduler()).thenReturn(scheduler);
        when(player.getLoaderId()).thenReturn(41);
        when(provider.getChunk(chunkX, chunkZ, false)).thenReturn(chunk);
        when(provider.requestChunkTask(chunkX, chunkZ)).thenReturn(task);
        when(scheduler.scheduleAsyncTask(isNull(), same(task)))
                .thenThrow(new IllegalStateException("forced task scheduling failure"));

        assertTrue(level.requestSubChunks(chunkX, chunkZ, player, 2));
        invokeProcessChunkRequest(level);

        assertTrue(chunkSendTasks.isEmpty());
        assertTrue(chunkSendQueue.isEmpty());
        assertTrue(subChunkSendQueue.isEmpty());
        verify(player, times(1)).onSubChunkRequestFail(2, chunkX, chunkZ);
    }

    @Test
    void serializationFailureInvokesFailureCallback() {
        int chunkX = 11;
        int chunkZ = 13;
        Level level = mock(Level.class);
        LevelProvider provider = mock(LevelProvider.class);
        Chunk chunk = mock(Chunk.class);
        HeightRange heightRange = mock(HeightRange.class);

        when(chunk.getX()).thenReturn(chunkX);
        when(chunk.getZ()).thenReturn(chunkZ);
        when(chunk.getHeightRange()).thenReturn(heightRange);
        when(chunk.getBlockEntities()).thenReturn(Collections.emptyMap());
        when(chunk.getHeightmap()).thenReturn(new short[256]);
        when(chunk.getBorders()).thenReturn(new boolean[256]);
        when(chunk.getProvider()).thenReturn(provider);
        when(provider.getLevel()).thenReturn(level);
        when(level.getRequestChunkVersions()).thenReturn(Collections.emptyMap());
        doThrow(new IllegalStateException("forced serialization failure"))
                .when(chunk).writeBiomeTo(any(), anyBoolean(), any());

        ChunkRequestTask task = new ChunkRequestTask(chunk);
        task.onRun();
        task.onCompletion(mock(Server.class));

        verify(level, times(1)).chunkRequestFailureCallback(chunkX, chunkZ);
        verify(level, never()).chunkRequestCallback(
                anyLong(), anyInt(), anyInt(), any(), any(), any(), any(), any());
    }

    private static void setLevelRuntimeFields(
            Level level,
            Server server,
            LevelProvider provider,
            Map<Long, BaseFullChunk> chunks,
            Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> chunkSendQueue,
            Long2ObjectMap<Int2ObjectMap<Pair<Player, IntSet>>> subChunkSendQueue,
            LongSet chunkSendTasks
    ) throws Exception {
        setField(level, "server", server);
        setField(level, "provider", provider);
        setField(level, "chunks", chunks);
        setField(level, "chunkLoaders", new Long2ObjectOpenHashMap<Int2ObjectMap<ChunkLoader>>());
        setField(level, "chunkTickList", new Long2IntOpenHashMap());
        setField(level, "lastChunkPos", new long[4]);
        setField(level, "lastChunk", new BaseFullChunk[4]);
        setField(level, "chunkSendQueue", chunkSendQueue);
        setField(level, "subChunkSendQueue", subChunkSendQueue);
        setField(level, "chunkSendTasks", chunkSendTasks);
        when(server.isPrimaryThread()).thenReturn(false);
    }

    private static void invokeProcessChunkRequest(Level level) throws Exception {
        Method method = Level.class.getDeclaredMethod("processChunkRequest");
        method.setAccessible(true);
        method.invoke(level);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = Level.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
