package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerMotionPublicationTest {
    @Test
    void newModePublishesTwoOrderedMotionsOncePerRecipient() {
        ProbePlayer player = player(true);
        Player viewer = mock(Player.class);
        player.hasSpawned.put(1, viewer);

        assertTrue(player.setMotion(new Vector3(0.2, 0.3, 0)));
        assertTrue(player.setMotion(new Vector3(-0.15, 0.18, 0)));
        player.updateMovement();

        assertEquals(2, player.sent.size());
        assertEquals(0.2f, player.sent.get(0).motionX);
        assertEquals(-0.15f, player.sent.get(1).motionX);
        ArgumentCaptor<DataPacket> packets = ArgumentCaptor.forClass(DataPacket.class);
        verify(viewer, times(2)).dataPacket(packets.capture());
        assertEquals(0.2f, ((SetEntityMotionPacket) packets.getAllValues().get(0)).motionX);
        assertEquals(-0.15f, ((SetEntityMotionPacket) packets.getAllValues().get(1)).motionX);
        verify(player.level, never()).addChunkPacket(anyInt(), anyInt(), any());
        verify(player.level, never()).addEntityMotion(anyInt(), anyInt(), anyLong(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void disabledModeKeepsBothLegacyChunkPublicationPaths() {
        ProbePlayer player = player(false);
        Player viewer = mock(Player.class);
        player.hasSpawned.put(1, viewer);

        assertTrue(player.setMotion(new Vector3(0.2, 0.3, 0)));

        assertEquals(1, player.sent.size());
        verify(player.level).addChunkPacket(anyInt(), anyInt(), any(SetEntityMotionPacket.class));
        verify(player.level).addEntityMotion(0, 0, player.getId(), 0.2, 0.3, 0);
        verify(viewer, never()).dataPacket(any());
    }

    @Test
    void repeatedAndSmallMotionsRemainExplicitPublications() {
        ProbePlayer player = player(true);
        Player viewer = mock(Player.class);
        player.hasSpawned.put(1, viewer);
        Vector3 motion = new Vector3(0.01, 0, 0);

        assertTrue(player.setMotion(motion));
        assertTrue(player.setMotion(motion));

        assertEquals(2, player.sent.size());
        verify(viewer, times(2)).dataPacket(any(SetEntityMotionPacket.class));
        assertEquals(0.01, player.lastMotionX);
        verify(player.level, never()).addChunkPacket(anyInt(), anyInt(), any());
    }

    @Test
    void directAddMotionRemainsAvailableToGameplayCallers() {
        ProbePlayer player = player(true);

        player.addMotion(0, 0.1, 0);

        verify(player.level).addChunkPacket(anyInt(), anyInt(), any(SetEntityMotionPacket.class));
        assertTrue(player.sent.isEmpty());
    }

    @Test
    void newlyCreatedPlayerDoesNotReplayItsExplicitMotionOnFirstUpdate() {
        ProbePlayer player = player(true);
        player.justCreated = true;

        assertTrue(player.setMotion(new Vector3(0.2, 0.3, 0)));
        player.justCreated = false;
        player.updateMovement();

        assertEquals(1, player.sent.size());
        assertEquals(0.2, player.lastMotionX);
        verify(player.level, never()).addChunkPacket(anyInt(), anyInt(), any());
    }

    @Test
    void unavailableChunkDoesNotConsumeMotionBeforeFirstPublication() {
        ProbePlayer player = player(true);
        player.justCreated = true;
        player.chunk = null;

        assertTrue(player.setMotion(new Vector3(0.2, 0.3, 0)));

        assertTrue(player.sent.isEmpty());
        assertEquals(0, player.lastMotionX);
        player.chunk = mock(FullChunk.class);
        player.justCreated = false;
        player.updateMovement();
        verify(player.level).addChunkPacket(anyInt(), anyInt(), any(SetEntityMotionPacket.class));
    }

    @Test
    void cancelledMotionDoesNotChangeCachesOrPublish() {
        ProbePlayer player = player(true);
        PluginManager plugins = player.server.getPluginManager();
        doAnswer(invocation -> {
            EntityMotionEvent event = invocation.getArgument(0);
            event.setCancelled();
            return null;
        }).when(plugins).callEvent(any(EntityMotionEvent.class));

        assertFalse(player.setMotion(new Vector3(0.2, 0.3, 0)));

        assertEquals(0, player.motionX);
        assertEquals(0, player.lastMotionX);
        assertTrue(player.sent.isEmpty());
        verify(player.level, never()).addChunkPacket(anyInt(), anyInt(), any());
        verify(player.level, never()).addEntityMotion(anyInt(), anyInt(), anyLong(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void observerCallbackCannotPublishTheOldMotionAfterANewerSetter() {
        ProbePlayer player = player(true);
        when(player.isOnline()).thenReturn(true);
        when(player.server.isPrimaryThread()).thenReturn(true);
        Player viewer = mock(Player.class);
        player.hasSpawned.put(1, viewer);
        List<Float> observerMotions = new ArrayList<>();
        doAnswer(invocation -> {
            SetEntityMotionPacket packet = invocation.getArgument(0);
            if (packet.motionX == 0.02f) {
                player.setMotion(new Vector3(-0.03, 0, 0));
            }
            observerMotions.add(packet.motionX);
            return true;
        }).when(viewer).dataPacket(any(SetEntityMotionPacket.class));

        assertTrue(player.setMotion(new Vector3(0.02, 0, 0)));

        assertEquals(-0.03, player.motionX);
        assertEquals(1, player.sent.size());
        assertEquals(-0.03f, player.sent.getFirst().motionX);
        // 接收者的发包准入负责拒绝自己正在处理的旧包，setter 负责停止后续接收者。
        assertEquals(List.of(-0.03f, 0.02f), observerMotions);
    }

    private static ProbePlayer player(boolean enabled) {
        ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        player.newInputEnabled = enabled;
        player.server = mock(Server.class);
        when(player.server.getPluginManager()).thenReturn(mock(PluginManager.class));
        player.level = mock(Level.class);
        player.chunk = mock(FullChunk.class);
        player.hasSpawned = new Int2ObjectOpenHashMap<>();
        player.sent = new ArrayList<>();
        return player;
    }

    private static class ProbePlayer extends Player {
        private boolean newInputEnabled;
        private List<SetEntityMotionPacket> sent;

        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        @Override
        public boolean isMainThreadInputEnabled() {
            return newInputEnabled;
        }

        @Override
        public boolean dataPacket(DataPacket packet) {
            sent.add((SetEntityMotionPacket) packet);
            return true;
        }
    }
}
