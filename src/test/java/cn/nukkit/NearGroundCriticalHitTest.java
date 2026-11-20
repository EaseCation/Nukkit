package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.plugin.PluginManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NearGroundCriticalHitTest {
    @BeforeAll
    static void initializeBlocks() {
        Block.init();
        Attribute.init();
    }

    @Test
    void knockbackHopDoesNotTurnTheReturnHitIntoACritical() {
        try (Fixture fixture = new Fixture()) {
            ProbePlayer attacker = fixture.player(0.5, 1);
            ProbePlayer target = fixture.player(2.5, 1);
            attacker.onGround = true;
            assertTrue(attacker.attack(hit(target, attacker, 0, 0.29f)));
            assertEquals(0.29, attacker.motionY, 0.0001);
            attacker.moveVertically(0.125);
            attacker.moveVertically(-0.0625);
            assertFalse(attacker.onGround);

            assertTrue(target.attack(hit(attacker, target, 0, 0)));
            assertEquals(16, target.getHealth(), 0.0001);
            assertEquals(0, attacker.attackCriticalThisJump);
        }
    }

    @Test
    void lowDescentCannotAddCriticalDifferenceDamageDuringHurtCooldown() {
        try (Fixture fixture = new Fixture()) {
            ProbePlayer attacker = fixture.player(0.5, 1);
            ProbePlayer target = fixture.player(2.5, 1);
            attacker.onGround = true;
            assertTrue(target.attack(hit(attacker, target, 10, 0.29f)));
            Vector3 firstMotion = target.getMotion();
            attacker.moveVertically(0.125);
            attacker.moveVertically(-0.0625);

            assertFalse(target.attack(hit(attacker, target, 10, 0.29f)));
            assertEquals(16, target.getHealth(), 0.0001);
            assertEquals(firstMotion, target.getMotion());
        }
    }

    @Test
    void normalJumpDescentStillDealsOneCriticalAndLandingRestoresAllowance() {
        try (Fixture fixture = new Fixture()) {
            ProbePlayer attacker = fixture.player(0.5, 1);
            ProbePlayer target = fixture.player(2.5, 1);
            attacker.onGround = true;
            attacker.moveVertically(1.125);
            attacker.highestPosition = attacker.y;
            assertTrue(target.attack(hit(attacker, target, 0, 0)));
            assertEquals(16, target.getHealth(), 0.0001, "Ascent does not grant a critical");
            attacker.moveVertically(-0.125);
            assertTrue(target.attack(hit(attacker, target, 0, 0)));
            assertEquals(10.8, target.getHealth(), 0.0001);
            assertEquals(1, attacker.attackCriticalThisJump);
            attacker.moveVertically(-0.0625);
            assertTrue(target.attack(hit(attacker, target, 0, 0)));
            assertEquals(6.8, target.getHealth(), 0.0001);
            attacker.moveVertically(-0.9375);
            assertTrue(attacker.onGround);
            assertEquals(0, attacker.attackCriticalThisJump);
        }
    }

    @Test
    void highDescentStillAddsCriticalDifferenceDamageDuringHurtCooldown() {
        try (Fixture fixture = new Fixture()) {
            ProbePlayer attacker = fixture.player(0.5, 1);
            ProbePlayer target = fixture.player(2.5, 1);
            attacker.onGround = true;
            assertTrue(target.attack(hit(attacker, target, 10, 0)));
            attacker.moveVertically(1.125);
            attacker.highestPosition = attacker.y;
            attacker.moveVertically(-0.125);
            assertTrue(target.attack(hit(attacker, target, 10, 0)));
            assertEquals(14.8, target.getHealth(), 0.0001);
        }
    }

    @Test
    void nearbySlabAndCarpetSurfacesExcludeCriticalsWithoutCountingAsLanding() {
        for (int id : new int[]{BlockID.SMOOTH_STONE_SLAB, BlockID.WHITE_CARPET}) {
            try (Fixture fixture = new Fixture()) {
                fixture.block(id, 0, 0, 0);
                double surface = id == BlockID.SMOOTH_STONE_SLAB ? 0.5 : 0.0625;
                ProbePlayer attacker = fixture.player(0.5, surface + 0.125);
                ProbePlayer target = fixture.player(2.5, 1);
                attacker.moveVertically(-0.0625);
                assertFalse(attacker.onGround);
                assertTrue(target.attack(hit(attacker, target, 0, 0)));
                assertEquals(16, target.getHealth(), 0.0001);
            }
        }
    }

    @Test
    void ceilingAndSideWallDoNotReplaceFloorSupportForCriticals() {
        try (Fixture fixture = new Fixture()) {
            fixture.block(BlockID.AIR, 0, 0, 0);
            fixture.block(BlockID.STONE, 0, 3, 0);
            fixture.block(BlockID.STONE, 1, 1, 0);
            ProbePlayer attacker = fixture.player(0.5, 1.2);
            ProbePlayer target = fixture.player(2.5, 1);
            attacker.moveVertically(-0.0625);
            assertTrue(target.attack(hit(attacker, target, 0, 0)));
            assertEquals(14.8, target.getHealth(), 0.0001);
        }
    }

    @Test
    void approachingFloorDoesNotSettleFallDamageUntilActualContact() {
        try (Fixture fixture = new Fixture()) {
            ProbePlayer player = fixture.player(0.5, 5);
            player.highestPosition = 5;
            player.moveVertically(-3.5);
            assertFalse(player.onGround);
            verify(player, never()).fall(anyFloat());
            player.moveVertically(-0.5);
            assertTrue(player.onGround);
            verify(player).fall(4f);
        }
    }

    private static EntityDamageByEntityEvent hit(Player attacker, Player target, int cooldown, float knockback) {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, target,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4f, knockback, knockback);
        event.setAttackCooldown(cooldown);
        return event;
    }

    private static class Fixture implements AutoCloseable {
        final MockedStatic<Server> servers = mockStatic(Server.class);
        final Level level = mock(Level.class, CALLS_REAL_METHODS);
        final Server server = mock(Server.class);
        final Map<String, Block> blocks = new HashMap<>();

        Fixture() {
            when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
            servers.when(Server::getInstance).thenReturn(server);
            // 只提供方块和外部服务，碰撞查询、移动和伤害结算使用生产实现。
            doAnswer(call -> blockAt(call.getArgument(0), call.getArgument(1), call.getArgument(2)))
                    .when(level).getBlock(anyInt(), anyInt(), anyInt(), anyBoolean());
            doAnswer(call -> blockAt(call.getArgument(0), call.getArgument(1), call.getArgument(2)))
                    .when(level).getBlock(anyInt(), anyInt(), anyInt());
            doReturn(HeightRange.MINIMUM).when(level).getHeightRange();
            doNothing().when(level).addChunkPacket(anyInt(), anyInt(), any(DataPacket.class));
            doNothing().when(level).addLevelSoundEvent(any(), anyInt());
            doNothing().when(level).addLevelSoundEvent(any(), anyInt(), anyInt(), anyString(), anyLong());
            for (int x = 0; x <= 3; x++) block(BlockID.STONE, x, 0, 0);
        }

        void block(int id, int x, int y, int z) {
            Block block = Block.get(id);
            block.x = x;
            block.y = y;
            block.z = z;
            block.level = level;
            blocks.put(x + ":" + y + ":" + z, block);
        }

        Block blockAt(int x, int y, int z) {
            return blocks.getOrDefault(x + ":" + y + ":" + z, Block.get(BlockID.AIR));
        }

        ProbePlayer player(double x, double y) {
            ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
            player.level = level;
            player.configure(server);
            player.x = x;
            player.y = y;
            player.z = 0.5;
            player.boundingBox = new SimpleAxisAlignedBB(x - 0.3, y, 0.2, x + 0.3, y + 1.8, 0.8);
            doReturn(false).when(player).isSwimming();
            doReturn(false).when(player).isSprinting();
            doReturn(null).when(player).getEffect(anyInt());
            doReturn(false).when(player).hasEffect(anyInt());
            doReturn(false).when(player).isBlocking();
            doReturn(false).when(player).isInsideOfWater(anyFloat());
            doReturn(false).when(player).isInsideOfWater(anyBoolean());
            doNothing().when(player).fall(anyFloat());
            return player;
        }

        @Override
        public void close() {
            servers.close();
        }
    }

    private static class ProbePlayer extends Player {
        ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        void configure(Server server) {
            this.server = server;
            health = 20;
            setMaxHealth(20);
            spawned = true;
            adventureSettings = mock(AdventureSettings.class);
            foodData = mock(PlayerFood.class);
            armorInventory = mock(ArmorInventory.class);
            when(armorInventory.getContents()).thenReturn(Int2ObjectMaps.emptyMap());
            when(armorInventory.getItem(anyInt())).thenReturn(mock(Item.class));
        }

        void moveVertically(double dy) {
            double previousY = y;
            assertTrue(fastMove(0, dy, 0));
            // 合成位置输入，方向约定来自生产移动链：旧坐标减新坐标。
            speed = new Vector3(0, previousY - y, 0);
        }

        @Override
        protected void onHurt(EntityDamageEvent source) {
        }

        @Override
        protected void onMotionChanged() {
            // 测试不连接网络；实际 motion 仍由击退和 setMotion 写入。
        }

        @Override
        protected void checkChunks() {
        }
    }
}
