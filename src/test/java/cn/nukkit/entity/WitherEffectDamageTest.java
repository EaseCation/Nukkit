package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.potion.Effect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WitherEffectDamageTest {
    @BeforeAll
    static void initializeEffects() {
        Effect.init();
    }

    @Test
    void repeatedWitherTicksLeaveTheNextOrdinaryAttackAvailable() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityLiving target = target();
            Effect wither = Effect.getEffect(Effect.WITHER);

            for (int tick = 0; tick < 3; tick++) {
                wither.applyEffect(target);
            }

            assertEquals(17, target.getHealth());
            assertEquals(0, target.getNextAllowAttack());
            assertEquals(DamageCause.WITHER, target.getLastDamageCause().getCause());
            assertTrue(target.attack(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 1)));
            assertEquals(16, target.getHealth());
            assertFalse(target.attack(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 1)));
            assertEquals(16, target.getHealth());
        }
    }

    @Test
    void witherDuringMeleeCooldownKeepsTheOrdinaryDamageDifferenceAndDeadline() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityLiving target = target();
            assertTrue(target.attack(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 4)));
            target.setNextAllowAttack(System.currentTimeMillis() + 10_000);
            long deadline = target.getNextAllowAttack();
            Effect wither = Effect.getEffect(Effect.WITHER);

            wither.applyEffect(target);
            wither.applyEffect(target);

            assertEquals(14, target.getHealth());
            assertEquals(deadline, target.getNextAllowAttack());
            assertFalse(target.attack(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 4)));
            assertTrue(target.attack(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 6)));
            assertEquals(12, target.getHealth());
            assertEquals(deadline, target.getNextAllowAttack());
        }
    }

    @Test
    void cancelledWitherLeavesHealthAndExistingMeleeProtectionUntouched() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            PluginManager events = installEventDispatcher(servers);
            EntityLiving target = target();
            EntityDamageEvent melee = new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 4);
            assertTrue(target.attack(melee));
            long deadline = target.getNextAllowAttack();
            doAnswer(call -> {
                EntityDamageEvent damage = call.getArgument(0);
                if (damage.getCause() == DamageCause.WITHER) damage.setCancelled();
                return null;
            }).when(events).callEvent(any(EntityDamageEvent.class));

            Effect.getEffect(Effect.WITHER).applyEffect(target);

            assertEquals(16, target.getHealth());
            assertEquals(deadline, target.getNextAllowAttack());
            assertSame(melee, target.getLastDamageCause());
        }
    }

    @Test
    void witherRespectsExplicitSpawnProtectionAndInvulnerability() {
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            installEventDispatcher(servers);
            EntityLiving target = target();
            target.noDamageTicks = 20;
            target.setLastDamageCause(new EntityDamageEvent(target, DamageCause.ENTITY_ATTACK, 1));

            Effect.getEffect(Effect.WITHER).applyEffect(target);

            assertEquals(20, target.getHealth());
            assertEquals(0, target.getNextAllowAttack());
            target.noDamageTicks = 0;
            target.invulnerable = true;
            Effect.getEffect(Effect.WITHER).applyEffect(target);
            assertEquals(20, target.getHealth());
            assertEquals(0, target.getNextAllowAttack());
        }
    }

    private static EntityLiving target() {
        EntityLiving target = mock(EntityLiving.class, CALLS_REAL_METHODS);
        target.server = Server.getInstance();
        target.health = 20;
        doReturn(20).when(target).getMaxHealth();
        doReturn(null).when(target).getEffect(anyInt());
        doNothing().when(target).onHurt(any());
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
