package cn.nukkit;

import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.MoveEntityPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MovementPacketSourceTest {
    @Test
    void aHideDuringTheSendEventRejectsTheAlreadySelectedOuterPosition() {
        Fixture fixture = new Fixture();
        doAnswer(invocation -> {
            fixture.actor.getViewers().remove(fixture.recipient.getLoaderId());
            fixture.recipient.visible = false;
            return null;
        }).when(fixture.plugins).callEvent(any(DataPacketSendEvent.class));

        assertFalse(fixture.recipient.dataPacket(fixture.movement()));
        verify(fixture.plugins, times(1)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void replacingTheOuterPacketDoesNotBypassItsInvalidatedSource() {
        Fixture fixture = new Fixture();
        doAnswer(invocation -> {
            fixture.actor.getViewers().remove(fixture.recipient.getLoaderId());
            DataPacketSendEvent event = invocation.getArgument(0);
            MoveEntityPacket replacement = new MoveEntityPacket();
            replacement.eid = fixture.actor.getId();
            event.setPacket(replacement);
            return null;
        }).when(fixture.plugins).callEvent(any(DataPacketSendEvent.class));

        assertFalse(fixture.recipient.dataPacket(fixture.movement()));
    }

    @Test
    void existingCancellationStillRunsOnlyOneSendEvent() {
        Fixture fixture = new Fixture();
        doAnswer(invocation -> {
            DataPacketSendEvent event = invocation.getArgument(0);
            event.setCancelled();
            return null;
        }).when(fixture.plugins).callEvent(any(DataPacketSendEvent.class));

        assertFalse(fixture.recipient.dataPacket(fixture.movement()));
        verify(fixture.plugins, times(1)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void removingOneViewerKeepsTheOtherViewersSourceCurrent() {
        Fixture fixture = new Fixture();
        ProbePlayer other = fixture.player(true, 30, 9);
        fixture.actor.getViewers().put(other.getLoaderId(), other);
        Player.MovementPacketSource first = fixture.recipient.captureMovementPacketSource(fixture.movement());
        Player.MovementPacketSource second = other.captureMovementPacketSource(fixture.movement());
        assertNotNull(first);
        assertNotNull(second);
        assertTrue(first.isCurrent(fixture.recipient));
        assertTrue(second.isCurrent(other));

        fixture.actor.getViewers().remove(fixture.recipient.getLoaderId());
        assertFalse(first.isCurrent(fixture.recipient));
        assertTrue(second.isCurrent(other));
    }

    @Test
    void actorPositionEpochAndBodyOffsetChangesInvalidateTheirPriorSources() {
        Fixture fixture = new Fixture();
        Player.MovementPacketSource source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.actor.x += 8;
        assertFalse(source.isCurrent(fixture.recipient));
        fixture.actor.x -= 8;
        assertTrue(source.isCurrent(fixture.recipient));
        fixture.actor.epoch++;
        assertFalse(source.isCurrent(fixture.recipient));

        source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.actor.baseOffset = 0.54f;
        assertFalse(source.isCurrent(fixture.recipient));
    }

    @Test
    void observerRespawnOrTransferCannotReuseThePriorObservationBoundary() {
        Fixture fixture = new Fixture();
        Player.MovementPacketSource source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.recipient.epoch++;
        assertFalse(source.isCurrent(fixture.recipient));

        source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.recipient.connectionId = UUID.randomUUID();
        assertFalse(source.isCurrent(fixture.recipient));
    }

    @Test
    void changingObserverWorldOrVisibilityRejectsTheOldSource() {
        Fixture fixture = new Fixture();
        Player.MovementPacketSource source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.recipient.visible = false;
        assertFalse(source.isCurrent(fixture.recipient));
        fixture.recipient.visible = true;
        fixture.recipient.changeWorld(mock(Level.class));
        assertFalse(source.isCurrent(fixture.recipient));
    }

    @Test
    void unrelatedRemoveAndSelfPositionPacketsDoNotInheritAnOuterGuard() {
        Fixture fixture = new Fixture();
        RemoveEntityPacket removal = new RemoveEntityPacket();
        removal.eid = fixture.actor.getId();
        assertNull(fixture.recipient.captureMovementPacketSource(removal));
        MovePlayerPacket own = fixture.movement();
        own.eid = fixture.recipient.getId();
        assertNull(fixture.recipient.captureMovementPacketSource(own));
    }

    @Test
    void anIndependentRemovalOrSelfCorrectionReplacementIsNotTheOriginalPositionOperation() {
        Fixture fixture = new Fixture();
        Player.MovementPacketSource source = fixture.recipient.captureMovementPacketSource(fixture.movement());
        assertNotNull(source);
        fixture.actor.getViewers().remove(fixture.recipient.getLoaderId());
        assertFalse(source.isCurrent(fixture.recipient));
        RemoveEntityPacket replacement = new RemoveEntityPacket();
        replacement.eid = fixture.actor.getId();
        assertFalse(source.references(replacement));
        MovePlayerPacket ownCorrection = fixture.movement();
        ownCorrection.eid = fixture.recipient.getLocalEntityId();
        assertFalse(source.references(ownCorrection));
    }

    @Test
    void disabledReceiverDoesNotLookUpAnActorOrAllocateASource() {
        Fixture fixture = new Fixture();
        fixture.recipient.enabled = false;
        clearInvocations(fixture.level);
        assertNull(fixture.recipient.captureMovementPacketSource(fixture.movement()));
        verify(fixture.level, never()).getEntity(anyLong());
    }

    @Test
    void disabledActorAndUnknownEntitiesKeepTheOriginalPath() {
        Fixture fixture = new Fixture();
        fixture.actor.enabled = false;
        assertNull(fixture.recipient.captureMovementPacketSource(fixture.movement()));
        MovePlayerPacket unknown = fixture.movement();
        unknown.eid = 999;
        assertNull(fixture.recipient.captureMovementPacketSource(unknown));
    }

    private static final class Fixture {
        private final Server server = mock(Server.class);
        private final PluginManager plugins = mock(PluginManager.class);
        private final Level level = mock(Level.class);
        private final ProbePlayer actor;
        private final ProbePlayer recipient;

        private Fixture() {
            when(server.isPrimaryThread()).thenReturn(true);
            when(server.getPluginManager()).thenReturn(plugins);
            actor = player(true, 10, 6);
            recipient = player(true, 20, 7);
            actor.getViewers().put(recipient.getLoaderId(), recipient);
            when(level.getEntity(actor.getId())).thenReturn(actor);
        }

        private ProbePlayer player(boolean enabled, long entityId, int loaderId) {
            ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
            player.configure(server, level, enabled, loaderId);
            doReturn(entityId).when(player).getId();
            return player;
        }

        private MovePlayerPacket movement() {
            MovePlayerPacket packet = new MovePlayerPacket();
            packet.eid = actor.getId();
            packet.x = (float) actor.x;
            packet.y = (float) (actor.y + actor.baseOffset);
            packet.z = (float) actor.z;
            return packet;
        }
    }

    // 仅离线测试对象，不创建运行时玩家或真实网络连接。
    private static class ProbePlayer extends Player {
        private boolean enabled;
        private boolean visible;
        private float baseOffset;
        private int testLoaderId;
        private long epoch;
        private UUID connectionId;

        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        private void configure(Server server, Level level, boolean enabled, int loaderId) {
            this.server = server;
            this.level = level;
            this.enabled = enabled;
            this.visible = true;
            this.baseOffset = 1.62f;
            this.testLoaderId = loaderId;
            this.epoch = 1;
            this.connectionId = UUID.randomUUID();
            this.connected = true;
            this.hasSpawned = new Int2ObjectOpenHashMap<>();
        }

        private void changeWorld(Level level) {
            this.level = level;
        }

        @Override public boolean isMainThreadInputEnabled() { return this.enabled; }
        @Override public boolean isOnline() { return this.connected; }
        @Override public boolean isAlive() { return true; }
        @Override public int getLoaderId() { return this.testLoaderId; }
        @Override public int getEntityViewDistance() { return 16; }
        @Override public long getMovementEpoch() { return this.epoch; }
        @Override public UUID getSessionId() { return this.connectionId; }
        @Override public boolean canSee(Player player) { return this.visible; }
        @Override protected float getBaseOffset() { return this.baseOffset; }
    }
}
