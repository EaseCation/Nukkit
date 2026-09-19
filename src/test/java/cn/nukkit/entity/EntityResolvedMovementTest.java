package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.math.SimpleAxisAlignedBB;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class EntityResolvedMovementTest {

    @Test
    void commitsCallerResolvedStateAndRunsLifecycleHooks() {
        ProbePlayer player = createPlayer();
        player.motionX = 1;
        player.motionY = -1;
        player.motionZ = 0.4;
        player.ySize = 0.5f;

        assertTrue(player.applyResolvedMovement(0.2, 0.5, 0.3, true, true, false, true));

        assertEquals(0.2, player.x, 1.0E-9);
        assertEquals(0.5, player.y, 1.0E-9);
        assertEquals(player.boundingBox.getMinY(), player.y);
        assertEquals(0, player.ySize);
        assertEquals(0, player.motionX);
        assertEquals(0, player.motionY);
        assertEquals(0.4, player.motionZ);
        assertTrue(player.isCollidedHorizontally);
        assertTrue(player.isCollidedVertically);
        assertTrue(player.onGround);
        assertEquals(1, player.chunkUpdates);
        assertEquals(1, player.fallUpdates);
        assertEquals(0.5, player.yAtFallUpdate);
        assertTrue(player.groundAtFallUpdate);
    }

    @Test
    void rejectsInvalidMovementWithoutChangingState() {
        ProbePlayer player = createPlayer();
        assertFalse(player.applyResolvedMovement(Double.NaN, 0, 0, false, false, false, false));
        assertEquals(0, player.x);
        assertEquals(-0.3, player.boundingBox.getMinX());
        assertEquals(0, player.chunkUpdates);
        assertEquals(0, player.fallUpdates);
    }

    @Test
    void rejectsClosedEntitiesAndExposesTheSubclassStepHeight() {
        ProbePlayer player = createPlayer();
        assertEquals(0.6, player.getMovementStepHeight());
        player.closed = true;
        assertFalse(player.applyResolvedMovement(1, 0, 0, false, false, false, false));
        assertEquals(0, player.x);
    }

    private static ProbePlayer createPlayer() {
        ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        player.boundingBox = new SimpleAxisAlignedBB(-0.3, 0, -0.3, 0.3, 1.8, 0.3);
        return player;
    }

    private static class ProbePlayer extends Player {
        private int chunkUpdates;
        private int fallUpdates;
        private double yAtFallUpdate;
        private boolean groundAtFallUpdate;

        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        @Override protected double getStepHeight() { return 0.6; }
        @Override protected void checkChunks() { chunkUpdates++; }
        @Override protected void updateFallState(boolean grounded) {
            fallUpdates++;
            yAtFallUpdate = y;
            groundAtFallUpdate = grounded;
        }
        @Override protected void checkGroundState(double x, double y, double z, double dx, double dy, double dz) {
            throw new AssertionError("Resolved movement must not rerun a platform ground heuristic");
        }
    }
}
