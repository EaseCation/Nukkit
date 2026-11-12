package cn.nukkit;

import cn.nukkit.level.Level;
import cn.nukkit.level.Dimension;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.data.ServerConfiguration;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QueuedChunkDeliveryTest {
    @Test
    void completedAsyncDeliveryDoesNotConsumeTheNextChunksBudget() {
        Level level = mock(Level.class);
        Server server = mock(Server.class);
        Block.init();
        Item.init();
        Attribute.init();
        when(server.getConfiguration()).thenReturn(mock(ServerConfiguration.class));
        when(server.getDefaultLevel()).thenReturn(level);
        when(level.getDimension()).thenReturn(Dimension.OVERWORLD);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        when(level.populateChunk(anyInt(), anyInt())).thenReturn(true);
        ProbePlayer player;
        try (MockedStatic<Server> global = mockStatic(Server.class)) {
            global.when(Server::getInstance).thenReturn(server);
            player = mock(ProbePlayer.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        }
        player.prepare(server, level);

        assertTrue(player.sendQueuedChunk());

        assertFalse(player.isQueued(0, 0));
        assertFalse(player.isQueued(0, 1));
        verify(level, never()).requestChunk(eq(0), eq(0), any(), anyInt());
        verify(level).requestChunk(eq(0), eq(1), eq(player), anyInt());
    }

    private static class ProbePlayer extends Player {
        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }
        private void prepare(Server server, Level level) {
            this.server = server;
            this.level = level;
            this.connected = true;
            this.spawned = true;
            this.chunksPerTick = 1;
            this.usedChunks = new Long2BooleanOpenHashMap();
            this.loadQueue = new Long2IntOpenHashMap();
            this.loadQueue.put(Level.chunkHash(0, 0), 0);
            this.loadQueue.put(Level.chunkHash(0, 1), 1);
            // 排序时尚未送达，真正消费队列前异步回调已完成。
            this.usedChunks.put(Level.chunkHash(0, 0), true);
        }

        private boolean isQueued(int x, int z) {
            return loadQueue.containsKey(Level.chunkHash(x, z));
        }
    }
}
