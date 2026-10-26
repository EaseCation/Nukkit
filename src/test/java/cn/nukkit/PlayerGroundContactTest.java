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

class PlayerGroundContactTest {
    @BeforeAll
    static void initializeBlocks() {
        Block.init();
        Attribute.init();
    }

    @Test
    void standingAndCrouchingLowJumpsStayAirborneUntilTheirFeetLand() {
        for (double height : new double[]{1.8, 1.5}) {
            Fixture fixture = new Fixture();
            fixture.block(BlockID.STONE, 0, 0, 0);
            fixture.block(BlockID.STONE, 0, 3, 0);
            ProbePlayer player = fixture.player(0.5, 1, 0.5, height);
            player.onGround = true;

            assertTrue(player.fastMove(0, 0.1, 0));
            assertEquals(1.1, player.y, 1e-9);
            assertFalse(player.onGround, "Feet above the floor must be airborne during ascent");
            assertTrue(player.fastMove(0, -0.05, 0));
            assertEquals(1.05, player.y, 1e-9);
            assertFalse(player.onGround, "Descending near the floor must still be airborne");
            assertTrue(player.fastMove(0, -0.05, 0));
            assertEquals(1, player.y, 1e-9);
            assertTrue(player.onGround, "Only contact with the floor ends the jump");
        }
    }

    @Test
    void ordinaryJumpStaysAirborneOnBothAscentAndDescent() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 1, 0.5, 1.8);
        player.onGround = true;

        player.fastMove(0, 1, 0);
        assertFalse(player.onGround);
        player.fastMove(0, -0.5, 0);
        assertEquals(1.5, player.y, 1e-9);
        assertFalse(player.onGround);
        player.fastMove(0, -0.5, 0);
        assertTrue(player.onGround);
    }

    @Test
    void groundChecksDistinguishNearbyFloorFromFootContact() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer airborne = fixture.player(0.5, 1.05, 0.5, 1.8);
        airborne.checkGroundState(0, -0.05, 0, 0, -0.05, 0);
        assertFalse(airborne.onGround);

        ProbePlayer landed = fixture.player(0.5, 1, 0.5, 1.8);
        landed.checkGroundState(0, -0.05, 0, 0, -0.05, 0);
        assertTrue(landed.onGround);
    }

    @Test
    void overheadAndSideBodyIntersectionsDoNotProvideFootSupport() {
        Fixture ceiling = new Fixture();
        ceiling.block(BlockID.STONE, 0, 3, 0);
        ProbePlayer touchingCeiling = ceiling.player(0.5, 1.2, 0.5, 1.8);
        touchingCeiling.fastMove(0, 0.01, 0);
        assertEquals(1.2, touchingCeiling.y, 1e-9, "Ceiling collision rejects the proposed move");
        assertFalse(touchingCeiling.onGround, "A ceiling touching the body is not a floor");
        touchingCeiling.checkGroundState(0, 0.01, 0, 0, 0.01, 0);
        assertFalse(touchingCeiling.onGround);

        Fixture wall = new Fixture();
        wall.block(BlockID.STONE, 1, 2, 0);
        ProbePlayer besideWall = wall.player(0.8, 1.2, 0.5, 1.8);
        besideWall.fastMove(0.01, 0, 0);
        assertFalse(besideWall.onGround, "A wall intersecting above the feet cannot support them");
        besideWall.checkGroundState(0.01, 0, 0, 0.01, 0, 0);
        assertFalse(besideWall.onGround);
    }

    @Test
    void slabAndStairSurfacesSupportFeetButNotAnAirGap() {
        for (int blockId : new int[]{BlockID.SMOOTH_STONE_SLAB, BlockID.STONE_STAIRS}) {
            Fixture fixture = new Fixture();
            fixture.block(blockId, 0, 0, 0);
            double surface = blockId == BlockID.SMOOTH_STONE_SLAB ? 0.5 : 1;
            ProbePlayer player = fixture.player(0.5, surface + 0.05, 0.5, 1.8);
            player.fastMove(0, -0.01, 0);
            assertFalse(player.onGround, "An air gap above a partial block is not contact");
            player.checkGroundState(0, -0.01, 0, 0, -0.01, 0);
            assertFalse(player.onGround);
            player.fastMove(0, -0.04, 0);
            assertEquals(surface, player.y, 1e-9);
            assertTrue(player.onGround);
            player.checkGroundState(0, -0.04, 0, 0, -0.04, 0);
            assertTrue(player.onGround);
        }
    }

    @Test
    void criticalAllowanceIsRetainedWhileDescendingAndResetByLanding() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 1.1, 0.5, 1.8);
        player.highestPosition = 1.1;
        player.attackCriticalThisJump = 1;

        player.fastMove(0, -0.05, 0);
        assertFalse(player.onGround);
        assertEquals(1, player.attackCriticalThisJump);
        player.fastMove(0, -0.05, 0);
        assertTrue(player.onGround);
        assertEquals(0, player.attackCriticalThisJump);
    }

    @Test
    void sameTickLowJumpLandingResetsCriticalAllowanceWithoutRecordedFallDistance() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 1.05, 0.5, 1.8);
        player.highestPosition = 1;
        player.attackCriticalThisJump = 1;

        player.fastMove(0, -0.05, 0);

        assertTrue(player.onGround);
        assertEquals(0, player.attackCriticalThisJump,
                "A completed jump restores its allowance even when its apex was between server ticks");
    }

    @Test
    void resolvedLandingRestoresCriticalAllowanceWithoutRecordedFallDistance() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 1.0625, 0.5, 1.8);
        player.highestPosition = 1;
        player.attackCriticalThisJump = 1;

        assertTrue(player.applyResolvedMovement(0, -0.0625, 0, true, true, false, true));

        assertEquals(1, player.y, 1e-9);
        assertTrue(player.onGround);
        assertEquals(0, player.attackCriticalThisJump);
    }

    @Test
    void teleportRoundingOffsetStillCountsAsFootContact() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 1.00001, 0.5, 1.8);
        player.fastMove(0.01, 0, 0);
        assertTrue(player.onGround);
        player.checkGroundState(0.01, 0, 0, 0.01, 0, 0);
        assertTrue(player.onGround);
    }

    @Test
    void carpetTopSupportsFeet() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.WHITE_CARPET, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 0.0625, 0.5, 1.8);
        player.fastMove(0.01, 0, 0);
        assertTrue(player.onGround);
        player.checkGroundState(0.01, 0, 0, 0.01, 0, 0);
        assertTrue(player.onGround);
    }

    @Test
    void fenceAndWallTopsSupportFeetAboveTheirOwnBlockCell() {
        for (int blockId : new int[]{BlockID.OAK_FENCE, BlockID.COBBLESTONE_WALL}) {
            Fixture fixture = new Fixture();
            fixture.block(blockId, 0, 0, 0);
            ProbePlayer player = fixture.player(0.5, 1.5, 0.5, 1.8);
            player.fastMove(0.01, 0, 0);
            assertTrue(player.onGround, "Collision shapes extending above their cell still provide support");
            player.checkGroundState(0.01, 0, 0, 0.01, 0, 0);
            assertTrue(player.onGround);
        }
    }

    @Test
    void fallDistanceIsPreservedUntilLanding() {
        Fixture fixture = new Fixture();
        fixture.block(BlockID.STONE, 0, 0, 0);
        ProbePlayer player = fixture.player(0.5, 5, 0.5, 1.8);
        player.highestPosition = 5;

        player.fastMove(0, -3.5, 0);
        assertFalse(player.onGround);
        assertEquals(5, player.highestPosition);
        verify(player, never()).fall(anyFloat());

        player.fastMove(0, -0.5, 0);
        assertTrue(player.onGround);
        verify(player).fall(4f);
        assertEquals(1, player.highestPosition);
    }

    @Test
    void lowJumpDealsOneCriticalHitAndLandingRestoresTheNextJump() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            Server server = mock(Server.class);
            when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
            servers.when(Server::getInstance).thenReturn(server);
            Fixture fixture = new Fixture();
            fixture.block(BlockID.STONE, 0, 0, 0);
            fixture.block(BlockID.STONE, 0, 3, 0);
            ProbePlayer attacker = fixture.player(0.5, 1, 0.5, 1.8);
            ProbePlayer target = fixture.player(0.5, 1, 0.5, 1.8);
            attacker.configureCombat();
            target.configureCombat();
            attacker.onGround = true;

            attacker.fastMove(0, 0.125, 0);
            attacker.fastMove(0, -0.0625, 0);
            // 生产输入链将speed写为旧位置减新位置；这里只提供下降时的已知输入状态。
            attacker.speed = new Vector3(0, 0.0625, 0);
            assertTrue(target.attack(hit(attacker, target)));
            assertEquals(14.8f, target.getHealth(), 0.0001f);
            assertEquals(1, attacker.attackCriticalThisJump);

            attacker.fastMove(0, -0.03125, 0);
            assertTrue(target.attack(hit(attacker, target)));
            assertEquals(10.8f, target.getHealth(), 0.0001f);
            assertEquals(1, attacker.attackCriticalThisJump);

            attacker.fastMove(0, -0.03125, 0);
            assertTrue(attacker.onGround);
            assertEquals(0, attacker.attackCriticalThisJump);
            attacker.fastMove(0, 0.125, 0);
            attacker.fastMove(0, -0.0625, 0);
            assertTrue(target.attack(hit(attacker, target)));
            assertEquals(5.6f, target.getHealth(), 0.0001f);
            assertEquals(1, attacker.attackCriticalThisJump);
        }
    }

    private static EntityDamageByEntityEvent hit(Player attacker, Player target) {
        EntityDamageByEntityEvent hit = new EntityDamageByEntityEvent(attacker, target,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4f, 0f, 0f);
        hit.setAttackCooldown(0);
        return hit;
    }

    private static class Fixture {
        final Level level = mock(Level.class, CALLS_REAL_METHODS);
        final Map<String, Block> blocks = new HashMap<>();

        Fixture() {
            // 仅提供世界方块；移动、碰撞查询和落地状态机均执行生产代码。
            doAnswer(call -> blockAt(call.getArgument(0), call.getArgument(1), call.getArgument(2)))
                    .when(level).getBlock(anyInt(), anyInt(), anyInt(), anyBoolean());
            doAnswer(call -> blockAt(call.getArgument(0), call.getArgument(1), call.getArgument(2)))
                    .when(level).getBlock(anyInt(), anyInt(), anyInt());
            doNothing().when(level).addChunkPacket(anyInt(), anyInt(), any(DataPacket.class));
            doNothing().when(level).addLevelSoundEvent(any(), anyInt());
            doReturn(false).when(level).isNewArmorMechanics();
            doReturn(HeightRange.MINIMUM).when(level).getHeightRange();
            doNothing().when(level).addLevelSoundEvent(any(), anyInt(), anyInt(), anyString(), anyLong());
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
            Block block = blocks.get(x + ":" + y + ":" + z);
            return block != null ? block : Block.get(BlockID.AIR);
        }

        ProbePlayer player(double x, double y, double z, double height) {
            ProbePlayer player = mock(ProbePlayer.class, CALLS_REAL_METHODS);
            player.level = level;
            player.configureServer(mock(Server.class));
            player.x = x;
            player.y = y;
            player.z = z;
            player.boundingBox = new SimpleAxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + height, z + 0.3);
            doReturn(false).when(player).isSwimming();
            doReturn(null).when(player).getEffect(anyInt());
            doReturn(false).when(player).hasEffect(anyInt());
            doReturn(false).when(player).isBlocking();
            doReturn(false).when(player).isInsideOfWater(anyFloat());
            doReturn(false).when(player).isInsideOfWater(anyBoolean());
            // 这些用例只验证支撑与暴击额度，不验证掉落伤害或区块订阅。
            doNothing().when(player).fall(anyFloat());
            return player;
        }
    }

    private static class ProbePlayer extends Player {
        ProbePlayer() {
            super(null, 0L, InetSocketAddress.createUnresolved("localhost", 0));
        }

        void configureCombat() {
            health = 20;
            setMaxHealth(20);
            spawned = true;
            adventureSettings = mock(AdventureSettings.class);
            foodData = mock(PlayerFood.class);
            armorInventory = mock(ArmorInventory.class);
            when(armorInventory.getContents()).thenReturn(Int2ObjectMaps.emptyMap());
            when(armorInventory.getItem(anyInt())).thenReturn(mock(Item.class));
        }

        @Override
        protected void onHurt(EntityDamageEvent source) {
        }

        void configureServer(Server server) {
            this.server = server;
        }

        @Override
        protected void checkChunks() {
        }
    }
}
