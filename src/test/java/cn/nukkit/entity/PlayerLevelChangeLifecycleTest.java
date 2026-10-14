package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerLevelChangeLifecycleTest {
    @Test
    void loginEventCanInitializeWorldBeforeThePlayerIsOnline() {
        Fixture fixture = new Fixture();
        assertFalse(fixture.player.isOnline());
        assertFalse(fixture.player.spawned);
        assertTrue(fixture.player.isLevelSwitchStateCurrent(fixture.origin, 0));
        assertFalse(fixture.player.isTeleportStateCurrent(fixture.origin, 0));

        assertTrue(fixture.player.setPositionAndRotation(fixture.destination, 90, 15));

        assertSame(fixture.target, fixture.player.getLevel());
        assertEquals(128, fixture.player.x);
        assertEquals(64, fixture.player.y);
        assertEquals(256, fixture.player.z);
        assertEquals(90, fixture.player.yaw);
        assertEquals(15, fixture.player.pitch);
        assertEquals(1, fixture.player.getMovementEpoch());
        verify(fixture.plugins).callEvent(any(EntityLevelChangeEvent.class));
        verify(fixture.origin).removeEntity(fixture.player);
        verify(fixture.target).addEntity(fixture.player);
        verify(fixture.player, never()).dataPacket(any(DataPacket.class));
    }

    @Test
    void teleportStillRequiresCompletedLogin() {
        Fixture fixture = new Fixture();
        assertFalse(fixture.player.teleport(fixture.destination, PlayerTeleportEvent.TeleportCause.PLUGIN));
        assertSame(fixture.origin, fixture.player.getLevel());
        verifyNoInteractions(fixture.plugins);
    }

    @Test
    void completedLoginKeepsBothLifecycleGuardsCurrent() {
        Fixture fixture = new Fixture();
        fixture.player.loggedIn = true;
        assertTrue(fixture.player.isOnline());
        assertTrue(fixture.player.isLevelSwitchStateCurrent(fixture.origin, 0));
        assertTrue(fixture.player.isTeleportStateCurrent(fixture.origin, 0));
        assertTrue(fixture.player.setPositionAndRotation(fixture.destination, 0, 0));
        assertFalse(fixture.player.isLevelSwitchStateCurrent(fixture.origin, 0));
        assertFalse(fixture.player.isTeleportStateCurrent(fixture.origin, 0));
    }

    @Test
    void cancelledLoginWorldChangePreservesTheOriginalWorld() {
        Fixture fixture = new Fixture();
        doAnswer(call -> {
            ((EntityLevelChangeEvent) call.getArgument(0)).setCancelled();
            return null;
        }).when(fixture.plugins).callEvent(any(EntityLevelChangeEvent.class));
        fixture.assertRejected();
        verify(fixture.player, never()).onLevelChangeAccepted(any(Level.class));
    }

    @Test
    void closedPlayerInTheEventCannotChangeWorld() {
        assertInvalidated(player -> player.closed = true, false);
    }

    @Test
    void disconnectedPlayerInTheEventCannotChangeWorld() {
        assertInvalidated(player -> player.disconnect(), false);
    }

    @Test
    void retiredSessionOrCommittedHandoffInTheEventCannotChangeWorld() {
        assertInvalidated(player -> player.acceptingInput = false, false);
    }

    @Test
    void closedPlayerInTheAcceptedHookCannotChangeWorld() {
        assertInvalidated(player -> player.closed = true, true);
    }

    @Test
    void disconnectedPlayerInTheAcceptedHookCannotChangeWorld() {
        assertInvalidated(player -> player.disconnect(), true);
    }

    @Test
    void retiredSessionOrCommittedHandoffInTheAcceptedHookCannotChangeWorld() {
        assertInvalidated(player -> player.acceptingInput = false, true);
    }

    @Test
    void nestedWorldChangeInTheEventStopsTheOuterWorldChange() {
        assertNestedWorldChange(false);
    }

    @Test
    void nestedWorldChangeInTheAcceptedHookStopsTheOuterWorldChange() {
        assertNestedWorldChange(true);
    }

    private void assertInvalidated(Consumer<ProbePlayer> invalidate, boolean acceptedHook) {
        Fixture fixture = new Fixture();
        if (acceptedHook) {
            doAnswer(call -> {
                invalidate.accept(fixture.player);
                return null;
            }).when(fixture.player).onLevelChangeAccepted(fixture.target);
        } else {
            doAnswer(call -> {
                invalidate.accept(fixture.player);
                return null;
            }).when(fixture.plugins).callEvent(any(EntityLevelChangeEvent.class));
        }
        fixture.assertRejected();
        verify(fixture.player, acceptedHook ? times(1) : never()).onLevelChangeAccepted(fixture.target);
    }

    private void assertNestedWorldChange(boolean acceptedHook) {
        Fixture fixture = new Fixture();
        Level nested = mock(Level.class);
        Location nestedPosition = new Location(16, 80, 32, 30, 45, nested);
        if (acceptedHook) {
            doAnswer(call -> {
                assertTrue(fixture.player.setPositionAndRotation(nestedPosition, 30, 45));
                return null;
            }).when(fixture.player).onLevelChangeAccepted(fixture.target);
        } else {
            doAnswer(call -> {
                EntityLevelChangeEvent event = call.getArgument(0);
                if (event.getTarget() == fixture.target) {
                    assertTrue(fixture.player.setPositionAndRotation(nestedPosition, 30, 45));
                }
                return null;
            }).when(fixture.plugins).callEvent(any(EntityLevelChangeEvent.class));
        }

        assertFalse(fixture.player.setPositionAndRotation(fixture.destination, 90, 15));

        assertSame(nested, fixture.player.getLevel());
        assertEquals(16, fixture.player.x);
        assertEquals(80, fixture.player.y);
        assertEquals(32, fixture.player.z);
        assertEquals(30, fixture.player.yaw);
        assertEquals(45, fixture.player.pitch);
        assertEquals(1, fixture.player.getMovementEpoch());
        verify(fixture.origin).removeEntity(fixture.player);
        verify(fixture.target, never()).addEntity(fixture.player);
        verify(nested).addEntity(fixture.player);
    }

    private static final class Fixture {
        private final ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        private final Server server = mock(Server.class);
        private final PluginManager plugins = mock(PluginManager.class);
        private final Level origin = mock(Level.class);
        private final Level target = mock(Level.class);
        private final Location destination = new Location(128, 64, 256, 0, 0, target);

        private Fixture() {
            player.configure(server, origin);
            when(server.getPluginManager()).thenReturn(plugins);
            doNothing().when(player).despawnFromAll();
            doNothing().when(player).recalculateBoundingBox(false);
        }

        private void assertRejected() {
            assertFalse(player.setPositionAndRotation(destination, 90, 15));
            assertSame(origin, player.getLevel());
            assertEquals(0, player.x);
            assertEquals(0, player.y);
            assertEquals(0, player.z);
            assertEquals(0, player.getMovementEpoch());
            verify(origin, never()).removeEntity(player);
            verify(target, never()).addEntity(player);
        }
    }

    private static class ProbePlayer extends Player {
        private boolean acceptingInput;

        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        private void configure(Server server, Level level) {
            this.server = server;
            this.level = level;
            this.connected = true;
            this.acceptingInput = true;
            this.usedChunks = new Long2BooleanOpenHashMap();
        }

        private void disconnect() {
            this.connected = false;
        }

        @Override
        public boolean isMainThreadInputEnabled() {
            return true;
        }

        @Override
        public boolean isAcceptingInputPackets() {
            return this.acceptingInput;
        }

        @Override
        protected void checkChunks() {
        }

        @Override
        protected boolean onLevelSwitch(Level oldLevel, Level newLevel) {
            return true;
        }
    }
}
