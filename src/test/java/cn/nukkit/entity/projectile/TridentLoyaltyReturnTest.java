package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TridentLoyaltyReturnTest {

    @Test
    void followsVanillaAccelerationAndDoesNotTeleportAtTwentyTicks() {
        TestTrident trident = returningTrident(2, 100);
        trident.onUpdate(1);
        assertEquals(99.9, trident.x, 1e-9);
        assertEquals(-0.099, trident.motionX, 1e-9);
        trident.onUpdate(2);
        assertEquals(99.70595, trident.x, 1e-9);
        assertEquals(-0.1921095, trident.motionX, 1e-9);
        for (int tick = 3; tick <= 20; tick++) {
            trident.onUpdate(tick);
        }
        assertFalse(trident.closed);
        assertTrue(trident.x > 70);
        verify(trident.shootingEntity, never()).attack(any());
        assertFalse(trident.canCollideWith(mock(Entity.class)));
    }

    @Test
    void higherLoyaltyAndShorterDistanceReturnSooner() {
        assertTrue(ticksUntilReturned(3, 30) < ticksUntilReturned(2, 30));
        assertTrue(ticksUntilReturned(2, 30) < ticksUntilReturned(1, 30));
        assertTrue(ticksUntilReturned(2, 10) < ticksUntilReturned(2, 50));
    }

    @Test
    void checksSweptPickupAndReturnsOnlyOnce() {
        TestTrident trident = returningTrident(2, 2);
        trident.motionX = -6;
        trident.onUpdate(1);
        assertTrue(trident.closed);
        assertTrue(trident.x < -0.8);
        trident.onUpdate(2);
        verify(((Player) trident.shootingEntity).getInventory(), times(1)).setItem(eq(0), same(trident.trident));
    }

    @Test
    void picksUpAGlancingReturnAtTheOwnersCurrentPosition() {
        TestTrident trident = returningTrident(2, 2);
        trident.z = 2.9;
        trident.motionX = -6;
        trident.shootingEntity.z = 4;
        trident.onUpdate(1);
        assertTrue(trident.closed);
        verify(((Player) trident.shootingEntity).getInventory(), times(1)).setItem(eq(0), same(trident.trident));
    }

    @Test
    void doesNotPickUpOutsideTheExpandedRange() {
        TestTrident trident = returningTrident(2, 2);
        trident.z = 2.4;
        trident.motionX = -6;
        trident.shootingEntity.z = 4;
        trident.onUpdate(1);
        assertFalse(trident.closed);
        verify(((Player) trident.shootingEntity).getInventory(), never()).setItem(anyInt(), any(Item.class));
    }

    @Test
    void retargetsMovingOwnerEveryTick() {
        TestTrident trident = returningTrident(2, 30);
        trident.onUpdate(1);
        assertEquals(0, trident.motionZ, 1e-9);
        trident.shootingEntity.z = 10;
        trident.onUpdate(2);
        assertTrue(trident.motionZ > 0);
        trident.shootingEntity.z = -10;
        trident.onUpdate(3);
        assertTrue(trident.motionZ < 0);
    }

    @Test
    void fullInventoryKeepsReturningUntilSpaceIsAvailable() {
        TestTrident trident = returningTrident(2, 0.1);
        PlayerInventory inventory = ((Player) trident.shootingEntity).getInventory();
        Item occupied = mock(Item.class);
        when(inventory.getItem(0)).thenReturn(occupied);
        when(inventory.canAddItem(trident.trident)).thenReturn(false);
        trident.onUpdate(1);
        assertFalse(trident.closed);
        verify(inventory, never()).addItem(any(Item.class));
        when(inventory.canAddItem(trident.trident)).thenReturn(true);
        when(inventory.addItem(trident.trident)).thenReturn(new Item[0]);
        trident.onUpdate(2);
        assertTrue(trident.closed);
    }

    @Test
    void cancelledPickupDoesNotConsumeTheProjectile() {
        TestTrident trident = returningTrident(2, 0.1);
        PluginManager plugins = trident.getServer().getPluginManager();
        doAnswer(invocation -> {
            ((InventoryPickupTridentEvent) invocation.getArgument(0)).setCancelled();
            return null;
        }).when(plugins).callEvent(any(InventoryPickupTridentEvent.class));
        trident.onUpdate(1);
        assertFalse(trident.closed);
        verify(((Player) trident.shootingEntity).getInventory(), never()).setItem(anyInt(), any(Item.class));
    }

    @Test
    void waitsFiveTicksAfterHittingABlock() {
        TestTrident trident = returningTrident(2, 30);
        trident.onHitBlock(null);
        for (int tick = 1; tick < 5; tick++) {
            trident.onUpdate(tick);
            assertEquals(30, trident.x);
        }
        trident.onUpdate(5);
        assertTrue(trident.x < 30);
    }

    @Test
    void ownerInvalidatedByPickupEventCannotReceiveTheItem() {
        TestTrident trident = returningTrident(2, 0.1);
        PluginManager plugins = trident.getServer().getPluginManager();
        doAnswer(invocation -> {
            when(trident.shootingEntity.isAlive()).thenReturn(false);
            return null;
        }).when(plugins).callEvent(any(InventoryPickupTridentEvent.class));
        trident.onUpdate(1);
        verify(((Player) trident.shootingEntity).getInventory(), never()).setItem(anyInt(), any(Item.class));
    }

    @Test
    void deadOwnerDropsOrdinaryTridentAndStopsReturning() {
        TestTrident trident = returningTrident(2, 30);
        when(trident.shootingEntity.isAlive()).thenReturn(false);
        trident.onUpdate(1);
        assertTrue(trident.closed);
        verify(trident.level).dropItem(trident, trident.trident);
    }

    @Test
    void voidReturnIgnoresVoidDamageAndDuplicateTicks() {
        TestTrident trident = returningTrident(2, 30);
        trident.y = -20;
        trident.onUpdate(1);
        assertFalse(trident.attack(new EntityDamageEvent(trident, EntityDamageEvent.DamageCause.VOID, 4)));
        Vector3 position = trident.copyVec();
        trident.onUpdate(1);
        assertEquals(position, trident.copyVec());
        assertFalse(trident.closed);
    }

    private int ticksUntilReturned(int loyalty, double distance) {
        TestTrident trident = returningTrident(loyalty, distance);
        for (int tick = 1; tick <= 200; tick++) {
            trident.onUpdate(tick);
            if (trident.closed) {
                return tick;
            }
        }
        fail("Trident never returned");
        return -1;
    }

    private TestTrident returningTrident(int loyalty, double distance) {
        TestTrident trident = mock(TestTrident.class, CALLS_REAL_METHODS);
        Player owner = mock(Player.class);
        Level level = mock(Level.class);
        when(level.getHeightRange()).thenReturn(HeightRange.MINIMUM);
        owner.level = level;
        when(owner.isOnline()).thenReturn(true);
        when(owner.isAlive()).thenReturn(true);
        when(owner.getEyePosition()).thenAnswer(invocation -> new Vector3(owner.x, owner.y + 2, owner.z));
        when(owner.getBoundingBox()).thenAnswer(invocation ->
            new SimpleAxisAlignedBB(owner.x - 0.3, owner.y, owner.z - 0.3, owner.x + 0.3, owner.y + 2, owner.z + 0.3));
        PlayerInventory inventory = mock(PlayerInventory.class);
        when(owner.getInventory()).thenReturn(inventory);
        Item empty = mock(Item.class);
        when(empty.isNull()).thenReturn(true);
        when(inventory.getItem(0)).thenReturn(empty);
        when(inventory.getSize()).thenReturn(36);
        when(inventory.setItem(eq(0), any(Item.class))).thenReturn(true);
        Item item = mock(Item.class);
        when(item.hasEnchantment(Enchantment.LOYALTY)).thenReturn(true);
        when(item.getEnchantmentLevel(Enchantment.LOYALTY)).thenReturn(loyalty);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        trident.configure(server, level, owner, item, distance);
        doReturn(true).when(trident).entityBaseTick(anyInt());
        doReturn(true).when(trident).setDataFlag(anyInt(), anyBoolean());
        doReturn(Map.of()).when(trident).getViewers();
        doNothing().when(trident).updateMovement();
        doAnswer(invocation -> {
            Vector3 position = invocation.getArgument(0);
            trident.x = position.x;
            trident.y = position.y;
            trident.z = position.z;
            return true;
        }).when(trident).setPosition(any(Vector3.class));
        doAnswer(invocation -> {
            trident.closed = true;
            return null;
        }).when(trident).close();
        trident.finishEntityHit();
        return trident;
    }

    /** 仅设置测试环境，不绕过被测返航和入包流程。 */
    static class TestTrident extends EntityThrownTrident {
        TestTrident(FullChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
        }

        void configure(Server server, Level level, Player owner, Item item, double distance) {
            this.server = server;
            this.level = level;
            shootingEntity = owner;
            trident = item;
            pickupMode = PICKUP_ANY;
            favoredSlot = 0;
            x = distance;
            y = 2;
        }
    }
}
