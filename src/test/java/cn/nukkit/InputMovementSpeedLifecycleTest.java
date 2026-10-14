package cn.nukkit;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.input.MovementCommitResult;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class InputMovementSpeedLifecycleTest {
    @Test
    void noPendingMovementPreservesTheSpeedReadByTheOriginalCriticalHitRule() {
        ProbePlayer player = player();
        player.speed.setComponents(0, 0.07544708251953125, 0);

        assertEquals(MovementCommitResult.NO_PENDING, player.worldMovementTick());
        assertEquals(0.07544708251953125, player.speed.y);
        assertEquals(MovementCommitResult.NO_PENDING, player.worldMovementTick());
        assertEquals(0.07544708251953125, player.speed.y);
    }

    @Test
    void anActualUnchangedInputStillReplacesThePreviousSpeedWithZero() {
        ProbePlayer player = player();
        player.speed.setComponents(0, 0.07544708251953125, 0);

        assertEquals(MovementCommitResult.UNCHANGED, player.commitUnchangedInput());
        assertTrue(player.speed.y == 0);
        assertEquals(MovementCommitResult.NO_PENDING, player.worldMovementTick());
        assertTrue(player.speed.y == 0);
    }

    private static ProbePlayer player() {
        ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        Level level = mock(Level.class);
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        doReturn(true).when(chunk).isGenerated();
        doReturn(1).when(level).getDifficulty();
        doReturn(true).when(player).isAlive();
        doReturn(false).when(player).isSleeping();
        doReturn(false).when(player).isFoodEnabled();
        doReturn(false).when(player).isInsideOfWater();
        doReturn(false).when(player).isSprinting();
        player.configure(server, level, chunk);
        assertEquals(MovementCommitResult.UNCHANGED, player.commitUnchangedInput());
        player.worldMovementTick();
        return player;
    }

    private static class ProbePlayer extends Player {
        private ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        private void configure(Server server, Level level, BaseFullChunk chunk) {
            this.server = server;
            this.level = level;
            this.chunk = chunk;
            this.connected = true;
            this.spawned = true;
        }

        private MovementCommitResult commitUnchangedInput() {
            this.setPendingMovement(new Vector3(this.x, this.y, this.z), (float) this.yaw, (float) this.pitch);
            return this.commitPendingMovement(1, true);
        }

        private MovementCommitResult worldMovementTick() {
            MovementCommitResult result = this.commitPendingMovement(1, true);
            this.finishInputMovementTick();
            return result;
        }
    }
}
