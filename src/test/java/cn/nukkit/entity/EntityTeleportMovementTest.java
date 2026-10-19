package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.MoveEntityPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntityTeleportMovementTest {
    @Test
    void successfulTeleportPublishesTheFlagOnlyForThatMovement() {
        Fixture fixture = new Fixture();
        assertTrue(fixture.entity.teleport(new Location(2, 64, 3), null));
        assertEquals(1, fixture.packets.size());
        assertTrue(fixture.packets.getFirst().teleport);
        assertEquals(2, fixture.packets.getFirst().x);
        fixture.entity.x += 0.2;
        fixture.entity.updateMovement();
        assertEquals(2, fixture.packets.size());
        assertFalse(fixture.packets.getLast().teleport);
    }

    @Test
    void teleportToTheSamePositionStillClearsAClientInterpolationTarget() {
        Fixture fixture = new Fixture();
        assertTrue(fixture.entity.teleport(new Location(0, 0, 0), null));
        assertEquals(1, fixture.packets.size());
        assertTrue(fixture.packets.getFirst().teleport);
    }

    @Test
    void rejectedTeleportDoesNotPublishOrMarkTheNextNormalMovement() {
        Fixture fixture = new Fixture();
        doReturn(false).when(fixture.entity).setPositionAndRotation(any(Vector3.class), anyDouble(), anyDouble());
        assertFalse(fixture.entity.teleport(new Location(2, 64, 3), null));
        assertTrue(fixture.packets.isEmpty());
        fixture.entity.x = 1;
        fixture.entity.updateMovement();
        assertFalse(fixture.packets.getFirst().teleport);
    }

    @Test
    void failedPublicationCannotLeaveTheTeleportFlagSet() {
        Fixture fixture = new Fixture();
        doThrow(new IllegalStateException("publication failure")).when(fixture.entity)
                .addMovement(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        assertThrows(IllegalStateException.class,
                () -> fixture.entity.teleport(new Location(2, 64, 3), null));
        doCallRealMethod().when(fixture.entity)
                .addMovement(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        fixture.entity.x += 1;
        fixture.entity.updateMovement();
        assertFalse(fixture.packets.getFirst().teleport);
    }

    private static final class Fixture {
        private final Entity entity = mock(Entity.class, CALLS_REAL_METHODS);
        private final List<MoveEntityPacket> packets = new ArrayList<>();

        private Fixture() {
            entity.temporalVector = new Vector3();
            final Player viewer = mock(Player.class);
            doReturn(Map.of(1, viewer)).when(entity).getViewers();
            doReturn(true).when(entity).setMotion(any(Vector3.class));
            doNothing().when(entity).resetFallDistance();
            doAnswer(call -> {
                final Vector3 target = call.getArgument(0);
                entity.x = target.x;
                entity.y = target.y;
                entity.z = target.z;
                entity.yaw = call.getArgument(1);
                entity.pitch = call.getArgument(2);
                return true;
            }).when(entity).setPositionAndRotation(any(Vector3.class), anyDouble(), anyDouble());
            doAnswer(call -> {
                final DataPacket packet = call.getArgument(0);
                if (packet instanceof MoveEntityPacket movement) {
                    packets.add(movement);
                }
                return true;
            }).when(viewer).dataPacket(any(DataPacket.class));
        }
    }
}
