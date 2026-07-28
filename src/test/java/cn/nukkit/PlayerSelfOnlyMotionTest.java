package cn.nukkit;

import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PlayerSelfOnlyMotionTest {

    private static final double EPSILON = 1.0E-6D;

    @Test
    void sendsMotionOnlyToTargetWithoutChangingTrackingState() {
        TestPlayer player = mock(TestPlayer.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        doReturn(pluginManager).when(server).getPluginManager();
        player.setServerForTest(server);
        player.chunk = mock(FullChunk.class);
        player.motionX = -0.125D;
        player.motionY = 0.25D;
        player.motionZ = 0.375D;
        player.lastMotionX = 1.25D;
        player.lastMotionY = -1.5D;
        player.lastMotionZ = 1.75D;
        Vector3 clientSpeed = new Vector3(3.0D, -4.0D, 5.0D);
        player.speed = clientSpeed;

        Vector3 sentMotion = new Vector3(0.25D, 0.5D, -0.75D);
        doAnswer(invocation -> {
            assertFalse(player.shouldUpdateMovementAfterMotion());
            return true;
        }).when(player).dataPacket(any(DataPacket.class));

        assertTrue(player.setMotionToSelfOnly(sentMotion));

        ArgumentCaptor<DataPacket> packetCaptor = ArgumentCaptor.forClass(DataPacket.class);
        verify(player).dataPacket(packetCaptor.capture());
        SetEntityMotionPacket packet = assertInstanceOf(
                SetEntityMotionPacket.class, packetCaptor.getValue());
        assertEquals(player.getId(), packet.eid);
        assertEquals(sentMotion.x, packet.motionX, EPSILON);
        assertEquals(sentMotion.y, packet.motionY, EPSILON);
        assertEquals(sentMotion.z, packet.motionZ, EPSILON);
        verify(pluginManager).callEvent(any(EntityMotionEvent.class));
        verify(player, never()).updateMovement();
        verify(player, never()).getLevel();

        assertMotion(player, sentMotion.x, sentMotion.y, sentMotion.z);
        assertTrackingAndClientSpeedUnchanged(player, clientSpeed);
        assertTrue(player.shouldUpdateMovementAfterMotion());

    }

    @Test
    void restoresViewerUpdatesWhenSetMotionFails() {
        Player player = mock(Player.class, CALLS_REAL_METHODS);
        doThrow(new IllegalStateException("motion failure"))
                .when(player).setMotion(any(Vector3.class));

        assertThrows(IllegalStateException.class,
                () -> player.setMotionToSelfOnly(Vector3.ZERO));
        assertTrue(player.shouldUpdateMovementAfterMotion());
    }

    @Test
    void reportsRejectedSelfPacketAndKeepsMotionForServerFallback() {
        TestPlayer player = mock(TestPlayer.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        doReturn(pluginManager).when(server).getPluginManager();
        player.setServerForTest(server);
        player.chunk = mock(FullChunk.class);
        player.motionX = -0.25D;
        player.motionY = 0.125D;
        player.motionZ = 0.5D;
        player.lastMotionX = 1.25D;
        player.lastMotionY = -1.5D;
        player.lastMotionZ = 1.75D;
        Vector3 clientSpeed = new Vector3(3.0D, -4.0D, 5.0D);
        player.speed = clientSpeed;
        doReturn(false).when(player).dataPacket(any(DataPacket.class));

        Vector3 requestedMotion = new Vector3(0.4D, 0.5D, -0.1D);
        assertFalse(player.setMotionToSelfOnly(requestedMotion));

        assertMotion(player, requestedMotion.x, requestedMotion.y, requestedMotion.z);
        assertTrackingAndClientSpeedUnchanged(player, clientSpeed);
        assertTrue(player.shouldUpdateMovementAfterMotion());
        verify(pluginManager).callEvent(any(EntityMotionEvent.class));
        verify(player, never()).updateMovement();
        verify(player, never()).getLevel();
    }

    @Test
    void ordinarySetMotionKeepsLegacySuccessWhenSelfPacketIsRejected() {
        TestPlayer player = mock(TestPlayer.class, CALLS_REAL_METHODS);
        Level level = mock(Level.class);
        player.justCreated = true;
        player.chunk = mock(FullChunk.class);
        doReturn(level).when(player).getLevel();
        doReturn(false).when(player).dataPacket(any(DataPacket.class));

        Vector3 requestedMotion = new Vector3(0.2D, 0.3D, -0.4D);
        assertTrue(player.setMotion(requestedMotion));

        assertMotion(player, requestedMotion.x, requestedMotion.y, requestedMotion.z);
        verify(player).dataPacket(any(SetEntityMotionPacket.class));
        verify(player).getLevel();
    }

    @Test
    void reportsMissingChunkAndKeepsMotionForServerFallback() {
        TestPlayer player = mock(TestPlayer.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        doReturn(pluginManager).when(server).getPluginManager();
        player.setServerForTest(server);
        player.chunk = null;
        player.motionX = -0.25D;
        player.motionY = 0.125D;
        player.motionZ = 0.5D;
        player.lastMotionX = 1.25D;
        player.lastMotionY = -1.5D;
        player.lastMotionZ = 1.75D;
        Vector3 clientSpeed = new Vector3(3.0D, -4.0D, 5.0D);
        player.speed = clientSpeed;

        Vector3 requestedMotion = new Vector3(-0.4D, 0.45D, 0.1D);
        assertFalse(player.setMotionToSelfOnly(requestedMotion));

        assertMotion(player, requestedMotion.x, requestedMotion.y, requestedMotion.z);
        assertTrackingAndClientSpeedUnchanged(player, clientSpeed);
        assertTrue(player.shouldUpdateMovementAfterMotion());
        verify(pluginManager).callEvent(any(EntityMotionEvent.class));
        verify(player, never()).dataPacket(any(DataPacket.class));
        verify(player, never()).updateMovement();
        verify(player, never()).getLevel();
    }

    private static void assertMotion(Player player, double x, double y, double z) {
        assertEquals(x, player.motionX, EPSILON);
        assertEquals(y, player.motionY, EPSILON);
        assertEquals(z, player.motionZ, EPSILON);
    }

    private static void assertTrackingAndClientSpeedUnchanged(Player player, Vector3 clientSpeed) {
        assertEquals(1.25D, player.lastMotionX, EPSILON);
        assertEquals(-1.5D, player.lastMotionY, EPSILON);
        assertEquals(1.75D, player.lastMotionZ, EPSILON);
        assertSame(clientSpeed, player.speed);
        assertEquals(3.0D, player.speed.x, EPSILON);
        assertEquals(-4.0D, player.speed.y, EPSILON);
        assertEquals(5.0D, player.speed.z, EPSILON);
    }

    private static class TestPlayer extends Player {

        protected TestPlayer() {
            super(null, 0L, null);
        }

        private void setServerForTest(Server server) {
            this.server = server;
        }
    }
}
