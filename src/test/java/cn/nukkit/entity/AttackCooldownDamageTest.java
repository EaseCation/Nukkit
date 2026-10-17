package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AttackCooldownDamageTest {
    @BeforeAll
    static void initializeGameRegistries() {
        Block.init();
        Item.init();
        Effect.init();
    }

    @Test
    void strongerHitAppliesResistanceArmorAndProtectionToOnlyTheDamageDifference() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityHumanType target = target(20, 4);
            doReturn(Effect.getEffect(Effect.RESISTANCE)).when(target).getEffect(Effect.RESISTANCE);
            assertTrue(target.attack(hit(target, 4)));
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);
            long deadline = target.getNextAllowAttack();

            EntityDamageEvent stronger = hit(target, 10);
            assertTrue(target.attack(stronger));

            // 同一冷却窗口合计承受 10 点基础伤害，依次减免 20%、80%、16%。
            assertEquals(18.656f, target.getHealth(), 0.0001f);
            assertEquals(-1.2f, stronger.getDamage(DamageModifier.RESISTANCE), 0.0001f);
            assertEquals(-3.84f, stronger.getDamage(DamageModifier.ARMOR), 0.0001f);
            assertEquals(-0.1536f, stronger.getDamage(DamageModifier.ARMOR_ENCHANTMENTS), 0.0001f);
            assertEquals(deadline, target.getNextAllowAttack());
        }
    }

    @Test
    void absorptionConsumesOnlyTheRemainingShieldsAfterTheFirstHit() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityHumanType target = target(0, 0);
            doReturn(Effect.getEffect(Effect.RESISTANCE)).when(target).getEffect(Effect.RESISTANCE);
            target.setAbsorption(5);
            assertTrue(target.attack(hit(target, 4)));
            assertEquals(1.8f, target.getAbsorption(), 0.0001f);
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);

            assertTrue(target.attack(hit(target, 10)));

            assertEquals(0, target.getAbsorption());
            assertEquals(17, target.getHealth(), 0.0001f);
        }
    }

    @Test
    void repeatedEqualAndLowerHitsAreRejectedAndIncreasingHitsDoNotRenewTheWindow() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityHumanType target = target(0, 0);
            long beforeFirstHit = System.currentTimeMillis();
            assertTrue(target.attack(hit(target, 4)));
            long afterFirstHit = System.currentTimeMillis();
            assertTrue(target.getNextAllowAttack() >= beforeFirstHit + 500);
            assertTrue(target.getNextAllowAttack() <= afterFirstHit + 500);
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);
            long deadline = target.getNextAllowAttack();

            assertFalse(target.attack(hit(target, 4)));
            assertFalse(target.attack(hit(target, 3)));
            assertTrue(target.attack(hit(target, 6)));
            assertTrue(target.attack(hit(target, 10)));

            assertEquals(10, target.getHealth());
            assertEquals(deadline, target.getNextAllowAttack());

            target.setNextAllowAttack(System.currentTimeMillis() - 1);
            long beforeNextWindow = System.currentTimeMillis();
            assertTrue(target.attack(hit(target, 4)));
            long afterNextWindow = System.currentTimeMillis();
            assertEquals(6, target.getHealth());
            assertTrue(target.getNextAllowAttack() >= beforeNextWindow + 500);
            assertTrue(target.getNextAllowAttack() <= afterNextWindow + 500);
        }
    }

    @Test
    void cancelledStrongerHitDoesNotRaiseTheDamageAlreadyAccountedFor() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            PluginManager events = installEventDispatcher(servers);
            EntityHumanType target = target(0, 0);
            assertTrue(target.attack(hit(target, 4)));
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);
            EntityDamageEvent cancelled = hit(target, 10);
            doAnswer(call -> {
                if (call.getArgument(0) == cancelled) cancelled.setCancelled();
                return null;
            }).when(events).callEvent(any(EntityDamageEvent.class));

            assertFalse(target.attack(cancelled));
            assertTrue(target.attack(hit(target, 6)));

            assertEquals(14, target.getHealth());
        }
    }

    @Test
    void strengthAndWeaknessBonusesFollowTheSameDamageDifference() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityHumanType target = target(0, 0);
            EntityDamageEvent first = hit(target, 4);
            first.setDamage(1.2f, DamageModifier.STRENGTH);
            first.setDamage(-0.8f, DamageModifier.WEAKNESS);
            assertTrue(target.attack(first));
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);
            EntityDamageEvent stronger = hit(target, 10);
            stronger.setDamage(3, DamageModifier.STRENGTH);
            stronger.setDamage(-2, DamageModifier.WEAKNESS);

            assertTrue(target.attack(stronger));

            assertEquals(9, target.getHealth(), 0.0001f);
        }
    }

    private static EntityDamageEvent hit(EntityHumanType target, float damage) {
        return new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, damage);
    }

    private static EntityHumanType target(int armorPoints, int protectionFactor) {
        EntityHumanType target = mock(EntityHumanType.class, CALLS_REAL_METHODS);
        target.server = Server.getInstance();
        target.level = mock(Level.class);
        target.health = 20;
        doReturn(20).when(target).getMaxHealth();
        doReturn(null).when(target).getEffect(anyInt());
        doReturn(false).when(target).removeEffect(Effect.ABSORPTION);
        doNothing().when(target).onHurt(any());
        Item armor = mock(Item.class);
        when(armor.getArmorPoints()).thenReturn(armorPoints);
        when(armor.hasEnchantments()).thenReturn(protectionFactor > 0);
        Enchantment protection = mock(Enchantment.class);
        when(protection.getProtectionFactor(any())).thenReturn(protectionFactor);
        when(armor.getEnchantments()).thenReturn(new Enchantment[]{protection});
        target.armorInventory = mock(ArmorInventory.class);
        when(target.armorInventory.getContents()).thenReturn(Int2ObjectMaps.singleton(0, armor));
        when(target.armorInventory.getItem(anyInt())).thenReturn(armor);
        return target;
    }

    private static PluginManager installEventDispatcher(MockedStatic<Server> servers) {
        Server server = mock(Server.class);
        PluginManager events = mock(PluginManager.class);
        servers.when(Server::getInstance).thenReturn(server);
        when(server.getPluginManager()).thenReturn(events);
        return events;
    }
}
