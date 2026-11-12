package cn.nukkit;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.Block;
import cn.nukkit.data.ServerConfiguration;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.input.MovementCommitResult;
import cn.nukkit.network.input.PlayerInputProcessor;
import cn.nukkit.network.input.ServerInputDispatcher;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InputMovementTimingTest {
    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
        Attribute.init();
    }

    @Test
    void nativeHighSpeedCatchupCommitsEachClientTickInTheSameServerTick() {
        ProbePlayer player = player();
        // 原生客户端实测的连续补帧位置，单帧约 5.4 格，三帧累计超过 15 格。
        assertEquals(MovementCommitResult.APPLIED, player.input(8147, 5.4002686));
        assertEquals(MovementCommitResult.APPLIED, player.input(8148, 10.8004761));
        assertEquals(MovementCommitResult.APPLIED, player.input(8149, 16.2006836));
        assertEquals(16.2006836, player.z, 1e-7);
        assertEquals(-16.2006836, player.speed.z, 1e-7);
    }

    @Test
    void splittingOneClientTickCannotMultiplyItsMovementAllowance() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 5.4));
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 10.8));
        assertEquals(MovementCommitResult.REJECTED, player.input(100, 16.2));
        assertEquals(10.8, player.z, 1e-7);
    }

    @Test
    void aServerTickBoundaryDoesNotRenewTheSameClientTickAllowance() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 10));
        player.finishTick();
        assertEquals(MovementCommitResult.REJECTED, player.input(100, 16));
        assertEquals(10, player.z);
    }

    @Test
    void aLargeClientTickJumpDoesNotGrantALargerSingleFrameDisplacement() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 5));
        assertEquals(MovementCommitResult.REJECTED, player.input(1000000, 25));
        assertEquals(5, player.z);
    }

    @Test
    void anOlderClientTickCannotResetTheDisplacementAccumulator() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 5));
        assertEquals(MovementCommitResult.REJECTED, player.input(99, 6));
        assertEquals(5, player.z);
    }

    @Test
    void movementWithoutAClientClockRetainsTheServerTickLimit() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.legacyInput(5.4));
        assertEquals(MovementCommitResult.APPLIED, player.legacyInput(10.8));
        assertEquals(MovementCommitResult.REJECTED, player.legacyInput(16.2));
        assertEquals(10.8, player.z, 1e-7);
    }

    @Test
    void aTrustedCorrectionStartsANewPositionEpoch() {
        ProbePlayer player = player();
        assertEquals(MovementCommitResult.APPLIED, player.input(100, 10));
        assertEquals(true, player.applyInputCorrection(new Vector3(0, 0, 200), true));
        assertEquals(MovementCommitResult.APPLIED, player.input(101, 210));
        assertEquals(210, player.z);
    }

    private static ProbePlayer player() {
        Server server = mock(Server.class);
        Level level = mock(Level.class);
        when(server.getConfiguration()).thenReturn(mock(ServerConfiguration.class));
        when(server.getDefaultLevel()).thenReturn(level);
        ProbePlayer player;
        try (MockedStatic<Server> global = mockStatic(Server.class)) {
            global.when(Server::getInstance).thenReturn(server);
            player = mock(ProbePlayer.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        }
        BaseFullChunk chunk = mock(BaseFullChunk.class);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        when(server.getPlayerInputProcessor()).thenReturn(mock(PlayerInputProcessor.class));
        when(server.getInputDispatcher()).thenReturn(mock(ServerInputDispatcher.class));
        when(server.isPrimaryThread()).thenReturn(true);
        when(chunk.isGenerated()).thenReturn(true);
        when(level.getDifficulty()).thenReturn(1);
        when(level.getHeightRange()).thenReturn(mock(HeightRange.class));
        when(level.getBlock(anyInt(), anyInt(), anyInt())).thenReturn(mock(BlockAir.class));
        doReturn(true).when(player).isAlive();
        doReturn(false).when(player).isSleeping();
        doReturn(false).when(player).isFoodEnabled();
        doReturn(false).when(player).isInsideOfWater();
        doReturn(false).when(player).isSprinting();
        doReturn(false).when(player).isSwimming();
        player.configure(server, level, chunk);
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
            this.onGround = true;
            this.scale = 1;
            this.boundingBox = new SimpleAxisAlignedBB(-.3, 0, -.3, .3, 1.8, .3);
        }

        private MovementCommitResult input(long clientTick, double z) {
            this.setPendingMovement(new Vector3(0, 0, z), 0, 0, clientTick);
            return this.commitPendingMovement(1, true);
        }

        private MovementCommitResult legacyInput(double z) {
            this.setPendingMovement(new Vector3(0, 0, z), 0, 0);
            return this.commitPendingMovement(1, true);
        }

        private void finishTick() { this.finishInputMovementTick(); }
        // 区块归属和摔落副作用属于测试世界；fastMove 和最终位置提交执行真实实现。
        @Override protected void checkChunks() { }
        @Override protected void updateFallState(boolean onGround) { }
    }
}
