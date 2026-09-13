package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectileBlockViewTest {
    @BeforeAll
    static void initializeBlocks() { Block.init(); }

    @Test
    void virtualWallStopsFastThrowBeforeHumanAndThenStartsLoyaltyReturn() {
        TestTrident trident = trident();
        Entity human = mock(Entity.class);
        human.boundingBox = new SimpleAxisAlignedBB(4, 0, 0, 4.6, 2, 1);
        when(human.canCollide()).thenReturn(true);
        when(trident.level.getCollidingEntities(any(), same(trident))).thenReturn(new Entity[] {human});
        trident.onUpdate(1);
        assertEquals(3, trident.x, 1e-6);
        assertTrue(trident.hadCollision);
        verify(trident, never()).onCollideWithEntity(any());
        for (int tick = 2; tick <= 5; tick++) {
            assertTrue(trident.onUpdate(tick));
            assertTrue(trident.isCollided);
        }
        assertTrue(trident.onUpdate(6));
        assertFalse(trident.isCollided);
        assertTrue(trident.motionX < 0);
    }

    @Test
    void humanInFrontOfWallCanStillBeHit() {
        TestTrident trident = trident();
        Entity human = mock(Entity.class);
        human.boundingBox = new SimpleAxisAlignedBB(1.5, 0, 0, 2.1, 2, 1);
        when(human.canCollide()).thenReturn(true);
        when(trident.level.getCollidingEntities(any(), same(trident))).thenReturn(new Entity[] {human});
        doReturn(true).when(trident).onCollideWithEntity(human);
        trident.onUpdate(1);
        verify(trident).onCollideWithEntity(human);
        assertFalse(trident.hadCollision);
    }

    private TestTrident trident() {
        TestTrident trident = mock(TestTrident.class, CALLS_REAL_METHODS);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        Level level = mock(Level.class);
        when(level.getHeightRange()).thenReturn(HeightRange.MINIMUM);
        Player owner = mock(Player.class);
        owner.level = level;
        when(owner.getEyePosition()).thenReturn(new Vector3(0, 0.5, 0.5));
        when(owner.getBoundingBox()).thenReturn(new SimpleAxisAlignedBB(-0.3, 0, 0.2, 0.3, 2, 0.8));
        Item item = mock(Item.class);
        when(item.hasEnchantment(Enchantment.LOYALTY)).thenReturn(true);
        when(item.getEnchantmentLevel(Enchantment.LOYALTY)).thenReturn(2);
        trident.configure(server, level, owner, item);
        doReturn(true).when(trident).isAlive();
        doReturn(true).when(trident).entityBaseTick(anyInt());
        doReturn(false).when(trident).isInsideOfLiquid();
        doReturn(true).when(trident).isReturnOwnerValid();
        doReturn(true).when(trident).canCollideWith(any());
        // 模拟出手后的首刻，之后由真实撞墙逻辑设置忠诚等待时间。
        doReturn(false, true).when(trident).usesLoyaltyReturn();
        doReturn(true).when(trident).setDataFlag(anyInt(), anyBoolean());
        doNothing().when(trident).updateMovement();
        doNothing().when(trident).addMovement(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        doAnswer(call -> {
            Vector3 next = call.getArgument(0);
            trident.x = next.x;
            trident.y = next.y;
            trident.z = next.z;
            return true;
        }).when(trident).setPosition(any(Vector3.class));
        return trident;
    }

    static class TestTrident extends EntityThrownTrident {
        private Block wall;

        TestTrident(FullChunk chunk, CompoundTag nbt) { super(chunk, nbt); }

        void configure(Server server, Level level, Player owner, Item item) {
            this.server = server;
            this.level = level;
            shootingEntity = owner;
            trident = item;
            y = z = 0.5;
            motionX = 10;
            boundingBox = new SimpleAxisAlignedBB(-0.1, 0.4, 0.4, 0.1, 0.6, 0.6);
            wall = Block.get(BlockID.STONE);
            wall.x = 3;
        }

        @Override
        protected MovingObjectPosition clipBlocks(Vector3 from, Vector3 to, int flags) {
            return wall.clip(from, to, flags);
        }

        @Override
        protected Block getCollisionBlock(BlockVector3 pos) { return wall; }

        @Override
        public boolean move(double dx, double dy, double dz) {
            x += dx;
            y += dy;
            z += dz;
            boundingBox.offset(dx, dy, dz);
            return true;
        }
    }
}
