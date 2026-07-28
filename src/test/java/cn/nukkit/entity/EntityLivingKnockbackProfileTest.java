package cn.nukkit.entity;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerFood;
import cn.nukkit.entity.knockback.KnockbackAlgorithm;
import cn.nukkit.entity.knockback.KnockbackProfile;
import cn.nukkit.entity.knockback.KnockbackSource;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntityLivingKnockbackProfileTest {

    private static final double EPSILON = 1.0E-6D;

    @Test
    void javaEdition1_8PlayerMeleeUsesServerMotionLedgerInsteadOfClientSpeed() {
        KnockbackProfile attackerProfile = new KnockbackProfile("je-attacker")
                .setAlgorithm(KnockbackAlgorithm.JAVA_EDITION_1_8)
                .setBaseH(0.4F)
                .setBaseV(0.4F);
        Player attacker = mock(Player.class);
        PlayerFood attackerFood = mock(PlayerFood.class);
        when(attacker.getKnockbackProfile()).thenReturn(attackerProfile);
        when(attacker.getFoodData()).thenReturn(attackerFood);
        when(attacker.isSprinting()).thenReturn(false);
        when(attacker.getYaw()).thenReturn(0.0D);
        attacker.onGround = true;
        attacker.x = 0.0D;
        attacker.z = 0.0D;

        Player victim = mock(Player.class, CALLS_REAL_METHODS);
        victim.justCreated = true;
        victim.chunk = mock(FullChunk.class);
        victim.x = 4.0D;
        victim.z = 3.0D;
        victim.motionX = 0.6D;
        victim.motionY = -0.2D;
        victim.motionZ = -0.4D;
        victim.lastMotionX = 1.1D;
        victim.lastMotionY = 1.2D;
        victim.lastMotionZ = 1.3D;
        Vector3 clientSpeed = new Vector3(8.0D, -6.0D, 4.0D);
        victim.speed = clientSpeed;

        doReturn(true).when(victim).isAlive();
        doReturn(false).when(victim).isCreativeLike();
        doReturn(new AdventureSettings(victim)).when(victim).getAdventureSettings();
        doReturn(false).when(victim).isBlocking();
        doReturn(null).when(victim).getEffect(anyInt());
        doReturn(0.0F).when(victim).getKnockbackResistance();
        doReturn(true).when(victim).damageEntity0(any(EntityDamageEvent.class));
        doNothing().when(victim).onHurt(any(EntityDamageEvent.class));
        doReturn(true).when(victim).dataPacket(any(DataPacket.class));

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                attacker,
                victim,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                1.0F,
                KnockbackSource.MELEE
        );

        assertTrue(victim.attack(event));

        ArgumentCaptor<DataPacket> packetCaptor = ArgumentCaptor.forClass(DataPacket.class);
        verify(victim, atLeastOnce()).dataPacket(packetCaptor.capture());
        SetEntityMotionPacket motionPacket = null;
        int motionPacketCount = 0;
        for (DataPacket packet : packetCaptor.getAllValues()) {
            if (packet instanceof SetEntityMotionPacket setEntityMotionPacket) {
                motionPacket = setEntityMotionPacket;
                motionPacketCount++;
            }
        }

        assertEquals(1, motionPacketCount);
        assertEquals(victim.getId(), motionPacket.eid);
        assertEquals(0.62D, motionPacket.motionX, EPSILON);
        assertEquals(0.3D, motionPacket.motionY, EPSILON);
        assertEquals(0.04D, motionPacket.motionZ, EPSILON);
        verify(victim, never()).getLevel();

        assertEquals(0.6D, victim.motionX, EPSILON);
        assertEquals(-0.2D, victim.motionY, EPSILON);
        assertEquals(-0.4D, victim.motionZ, EPSILON);
        assertEquals(1.1D, victim.lastMotionX, EPSILON);
        assertEquals(1.2D, victim.lastMotionY, EPSILON);
        assertEquals(1.3D, victim.lastMotionZ, EPSILON);
        assertSame(clientSpeed, victim.speed);
        assertEquals(8.0D, victim.speed.x, EPSILON);
        assertEquals(-6.0D, victim.speed.y, EPSILON);
        assertEquals(4.0D, victim.speed.z, EPSILON);
    }

}
