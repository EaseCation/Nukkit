package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlayerChunkEntityVisibilityTest {
    @Test
    void movingOutOfRangeStillHidesOtherEntitiesWhenNpcClosesItsText() {
        Fixture fixture = new Fixture(10);
        fixture.showExistingEntities();
        fixture.npc.onDespawn = fixture::closeText;

        fixture.player.moveToDestinationChunk();

        fixture.assertExistingEntitiesHidden();
        assertSame(fixture.destination, fixture.player.chunk);
        assertSame(fixture.player, fixture.destination.getEntities().get(900L));
        assertFalse(fixture.source.getEntities().containsKey(900L));
        fixture.assertTextClosed();
    }

    @Test
    void unloadingChunkCompletesVisibilityAndLoaderCleanupWhenNpcClosesItsText() {
        Fixture fixture = new Fixture(1);
        fixture.showExistingEntities();
        fixture.npc.onDespawn = fixture::closeText;

        fixture.player.unloadChunk(0, 0);

        fixture.assertExistingEntitiesHidden();
        assertFalse(fixture.player.usedChunks.containsKey(Level.chunkHash(0, 0)));
        assertFalse(fixture.player.isQueued(0, 0));
        assertTrue(fixture.registeredLoaders.isEmpty());
        fixture.assertTextClosed();
    }

    @Test
    void movingIntoRangeShowsExistingEntitiesWhenNpcAddsTextAndRehashesTheChunkTable() {
        Fixture fixture = new Fixture(1);
        fixture.npc.onSpawn = () -> {
            for (long id = 1_000; id < 1_128; id++) {
                Entity line = fixture.entity(id);
                line.spawnTo(fixture.player);
                fixture.addedText.add(line);
            }
        };

        fixture.player.moveToDestinationChunk();

        assertSame(fixture.player, fixture.npc.getViewers().get(fixture.player.getLoaderId()));
        for (Entity entity : fixture.unaffected) {
            assertSame(fixture.player, entity.getViewers().get(fixture.player.getLoaderId()));
        }
        assertEquals(128, fixture.addedText.size());
        for (Entity line : fixture.addedText) {
            assertSame(line, fixture.source.getEntities().get(line.getId()));
            assertSame(fixture.player, line.getViewers().get(fixture.player.getLoaderId()));
        }
        assertSame(fixture.player, fixture.destination.getEntities().get(900L));
        assertFalse(fixture.source.getEntities().containsKey(900L));
    }

    private static final class Fixture {
        private final ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        private final Level level = mock(Level.class);
        private final BaseFullChunk source = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        private final BaseFullChunk destination = mock(BaseFullChunk.class, CALLS_REAL_METHODS);
        private final Server server = mock(Server.class);
        private final TextOwnerNpc npc = mock(TextOwnerNpc.class, CALLS_REAL_METHODS);
        private final List<Entity> text = new ArrayList<>();
        private final List<Entity> unaffected = new ArrayList<>();
        private final List<Entity> addedText = new ArrayList<>();
        private final Set<Long> registeredLoaders = new HashSet<>();

        private Fixture(int destinationX) {
            source.setX(0);
            source.setZ(0);
            destination.setX(destinationX);
            destination.setZ(0);
            player.configure(server, level, source, destinationX);
            doReturn(73).when(player).getLoaderId();
            doReturn(5).when(player).getEntityViewDistance();
            when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
            when(level.getChunk(destinationX, 0, true)).thenReturn(destination);
            when(level.getChunkEntities(0, 0)).thenAnswer(call -> source.getEntities());
            when(level.getChunkPlayers(anyInt(), anyInt())).thenReturn(Int2ObjectMaps.emptyMap());
            registeredLoaders.add(Level.chunkHash(0, 0));
            doAnswer(call -> {
                registeredLoaders.remove(Level.chunkHash(call.getArgument(1), call.getArgument(2)));
                return null;
            }).when(level).unregisterChunkLoader(eq(player), anyInt(), anyInt());
            configure(npc, 0);
            source.addEntity(npc);
            for (long id = 1; id <= 16; id++) text.add(entity(id));
            unaffected.add(entity(100));
            unaffected.add(entity(200));
            source.addEntity(player);
            assertInstanceOf(Long2ObjectOpenHashMap.class, source.getEntities());
        }

        private void configure(Entity entity, long id) {
            doReturn(id).when(entity).getId();
            entity.server = server;
            entity.level = level;
            entity.chunk = source;
            entity.health = 20;
            entity.hasSpawned = new HashMap<>();
        }

        private Entity entity(long id) {
            Entity entity = mock(Entity.class, CALLS_REAL_METHODS);
            configure(entity, id);
            source.addEntity(entity);
            return entity;
        }

        private void showExistingEntities() {
            // 初始可见关系是场景前置条件，后续显隐由 Entity 的真实方法完成。
            for (Entity entity : source.getEntities().values()) {
                if (entity != player) entity.getViewers().put(player.getLoaderId(), player);
            }
        }

        private void closeText() {
            text.forEach(Entity::close);
        }

        private void assertExistingEntitiesHidden() {
            assertFalse(npc.getViewers().containsKey(player.getLoaderId()));
            for (Entity entity : unaffected) {
                assertFalse(entity.getViewers().containsKey(player.getLoaderId()));
                assertTrue(player.sent.stream().anyMatch(packet -> packet instanceof RemoveEntityPacket
                        && ((RemoveEntityPacket) packet).eid == entity.getId()));
            }
        }

        private void assertTextClosed() {
            for (Entity line : text) {
                assertTrue(line.closed);
                assertFalse(source.getEntities().containsKey(line.getId()));
            }
            assertSame(npc, source.getEntities().get(npc.getId()));
            for (Entity entity : unaffected) assertSame(entity, source.getEntities().get(entity.getId()));
        }
    }

    // 只准备 NPC 回调增删同区块浮空字的条件；区块遍历和可见性管理执行生产代码。
    private abstract static class TextOwnerNpc extends Entity {
        private Runnable onSpawn;
        private Runnable onDespawn;

        private TextOwnerNpc() {
            super(null, null);
        }

        @Override
        public void spawnTo(Player player) {
            super.spawnTo(player);
            if (onSpawn != null) onSpawn.run();
        }

        @Override
        public void despawnFrom(Player player) {
            super.despawnFrom(player);
            if (onDespawn != null) onDespawn.run();
        }
    }

    private static class ProbePlayer extends Player {
        private List<DataPacket> sent;

        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        private void configure(Server server, Level level, BaseFullChunk source, int destinationX) {
            this.server = server;
            this.level = level;
            this.chunk = source;
            doReturn(900L).when(this).getId();
            this.x = destinationX * 16;
            this.health = 20;
            this.hasSpawned = new HashMap<>();
            this.usedChunks = new Long2BooleanOpenHashMap();
            this.usedChunks.put(Level.chunkHash(0, 0), true);
            this.loadQueue = new Long2IntOpenHashMap();
            this.loadQueue.put(Level.chunkHash(0, 0), 1);
            this.sent = new ArrayList<>();
        }

        private void moveToDestinationChunk() {
            super.checkChunks();
        }

        private boolean isQueued(int x, int z) {
            return this.loadQueue.containsKey(Level.chunkHash(x, z));
        }

        @Override
        public boolean isMainThreadInputEnabled() {
            return false;
        }

        @Override
        public boolean dataPacket(DataPacket packet) {
            this.sent.add(packet);
            return true;
        }
    }
}
