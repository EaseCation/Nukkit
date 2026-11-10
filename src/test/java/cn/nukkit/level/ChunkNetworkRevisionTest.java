package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.level.GlobalBlockPaletteInterface.StaticVersion;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkCachedData;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * 验证区块网络版本号：异步序列化期间内容发生变化时，旧的区块网络数据不能进入缓存。
 */
class ChunkNetworkRevisionTest {

    @Test
    void contentChangeAdvancesRevisionAndDropsCachedData() {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        chunk.setCachedData(cachedData(chunk.getNetworkRevision()));
        long before = chunk.getNetworkRevision();

        chunk.setChanged();

        assertTrue(chunk.getNetworkRevision() > before);
        assertNull(chunk.getCachedData());
        assertTrue(chunk.hasChanged());
    }

    @Test
    void invalidatingNetworkCacheDoesNotMarkChunkForSaving() {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        chunk.setCachedData(cachedData(chunk.getNetworkRevision()));
        long before = chunk.getNetworkRevision();

        chunk.invalidateNetworkCache();

        assertTrue(chunk.getNetworkRevision() > before);
        assertNull(chunk.getCachedData());
        assertFalse(chunk.hasChanged());
    }

    @Test
    void savingDoesNotRollBackRevision() {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        chunk.setChanged();
        chunk.setChanged();
        long beforeSave = chunk.getNetworkRevision();

        chunk.setChanged(false);

        assertFalse(chunk.hasChanged());
        assertEquals(beforeSave, chunk.getNetworkRevision());
    }

    @Test
    void differentChunkObjectsNeverShareARevisionAfterInvalidation() {
        BaseFullChunk first = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        BaseFullChunk second = mock(BaseFullChunk.class, CALLS_REAL_METHODS);

        first.invalidateNetworkCache();
        second.invalidateNetworkCache();

        assertNotEquals(first.getNetworkRevision(), second.getNetworkRevision());
    }

    @Test
    void callbackRejectsDataBuiltOnAChunkObjectThatWasReplaced() throws Exception {
        BaseFullChunk unloaded = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        unloaded.invalidateNetworkCache();
        ChunkCachedData data = cachedData(unloaded.getNetworkRevision());

        // 序列化期间区块被卸载并重新加载成了一个新对象
        BaseFullChunk reloaded = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        reloaded.invalidateNetworkCache();
        Level level = levelWithChunk(reloaded);
        level.chunkRequestCallback(data.getNetworkRevision(), 5, 9, data, noPayloads(), new byte[0], new byte[0], noSubChunkPayloads());

        assertNull(reloaded.getCachedData());
    }

    @Test
    void callbackCachesDataBuiltFromCurrentRevision() throws Exception {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        Level level = levelWithChunk(chunk);
        ChunkCachedData data = cachedData(chunk.getNetworkRevision());

        level.chunkRequestCallback(data.getNetworkRevision(), 5, 9, data, noPayloads(), new byte[0], new byte[0], noSubChunkPayloads());

        assertSame(data, chunk.getCachedData());
    }

    @Test
    void callbackRejectsDataWhenBlockEntityChangedDuringSerialization() throws Exception {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        Level level = levelWithChunk(chunk);
        ChunkCachedData data = cachedData(chunk.getNetworkRevision());

        // 序列化期间方块实体把新数据推给了观察者
        chunk.invalidateNetworkCache();
        level.chunkRequestCallback(data.getNetworkRevision(), 5, 9, data, noPayloads(), new byte[0], new byte[0], noSubChunkPayloads());

        assertNull(chunk.getCachedData());
    }

    @Test
    void callbackRejectsStaleDataEvenAfterChunkWasSaved() throws Exception {
        BaseFullChunk chunk = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        Level level = levelWithChunk(chunk);
        ChunkCachedData data = cachedData(chunk.getNetworkRevision());

        // 序列化期间区块被修改，随后存盘把存盘计数归零
        chunk.setChanged();
        chunk.setChanged(false);
        level.chunkRequestCallback(data.getNetworkRevision(), 5, 9, data, noPayloads(), new byte[0], new byte[0], noSubChunkPayloads());

        assertNull(chunk.getCachedData());
    }

    private static ChunkCachedData cachedData(long networkRevision) {
        return new ChunkCachedData(0, new byte[0], new byte[0][], new boolean[0], null, null, networkRevision);
    }

    private static Map<StaticVersion, byte[]> noPayloads() {
        return new EnumMap<>(StaticVersion.class);
    }

    private static Map<StaticVersion, byte[][]> noSubChunkPayloads() {
        return new EnumMap<>(StaticVersion.class);
    }

    private static Level levelWithChunk(BaseFullChunk chunk) throws Exception {
        Level level = mock(Level.class, CALLS_REAL_METHODS);
        setField(level, "provider", mock(LevelProvider.class));
        setField(level, "chunkSendQueue", new Long2ObjectOpenHashMap<Int2ObjectMap<Pair<Player, IntSet>>>());
        setField(level, "subChunkSendQueue", new Long2ObjectOpenHashMap<Int2ObjectMap<Pair<Player, IntSet>>>());
        setField(level, "chunkSendTasks", new LongOpenHashSet());
        doReturn(chunk).when(level).getChunk(5, 9, false);
        return level;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = Level.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
