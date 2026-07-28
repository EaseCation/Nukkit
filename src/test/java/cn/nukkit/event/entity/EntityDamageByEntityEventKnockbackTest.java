package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.knockback.KnockbackAlgorithm;
import cn.nukkit.entity.knockback.KnockbackProfile;
import cn.nukkit.entity.knockback.KnockbackSource;
import cn.nukkit.entity.projectile.EntityArrow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntityDamageByEntityEventKnockbackTest {

    @Test
    void snapshotsJavaMeleeContextWithoutMarkingLegacyOverride() {
        KnockbackProfile profile = javaProfile();
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(profile);
        when(damager.isSprinting()).thenReturn(true);
        when(damager.getYaw()).thenReturn(90.0D);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);

        assertTrue(event.usesJavaEdition1_8Knockback());
        assertTrue(event.wasAttackerSprinting());
        assertEquals(90.0F, event.getAttackerYaw());
        assertNotSame(profile, event.getKnockbackProfile());
    }

    @Test
    void usesJavaAlgorithmFromAttackerRegardlessOfLegacyTargetProfile() {
        EntityLiving damager = mock(EntityLiving.class);
        EntityLiving target = mock(EntityLiving.class);
        when(damager.getKnockbackProfile()).thenReturn(javaProfile());
        when(target.getKnockbackProfile()).thenReturn(legacyProfile());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);

        assertTrue(event.usesJavaEdition1_8Knockback());
        verify(target, never()).getKnockbackProfile();
    }

    @Test
    void usesLegacyAlgorithmFromAttackerRegardlessOfJavaTargetProfile() {
        EntityLiving damager = mock(EntityLiving.class);
        EntityLiving target = mock(EntityLiving.class);
        when(damager.getKnockbackProfile()).thenReturn(legacyProfile());
        when(target.getKnockbackProfile()).thenReturn(javaProfile());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);

        assertFalse(event.usesJavaEdition1_8Knockback());
        verify(target, never()).getKnockbackProfile();
    }

    @Test
    void onlyExplicitDefaultSourcesEnableJavaAlgorithm() {
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(javaProfile());

        EntityDamageByEntityEvent generic = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F);
        assertFalse(generic.usesJavaEdition1_8Knockback());

        for (KnockbackSource source : KnockbackSource.values()) {
            if (source == KnockbackSource.GENERIC) {
                continue;
            }
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                    damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, source);

            assertTrue(event.usesJavaEdition1_8Knockback(), source.name());
        }

        when(damager.getKnockbackProfile()).thenReturn(legacyProfile());
        for (KnockbackSource source : KnockbackSource.values()) {
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                    damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, source);

            assertFalse(event.usesJavaEdition1_8Knockback(), source.name());
        }
    }

    @Test
    void keepsPerHitSnapshotWhenAttackerProfileChangesAfterEventCreation() {
        KnockbackProfile assignedProfile = javaProfile();
        EntityLiving damager = mock(EntityLiving.class);
        EntityLiving target = mock(EntityLiving.class);
        when(damager.getKnockbackProfile()).thenReturn(assignedProfile);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);
        assignedProfile.setAlgorithm(KnockbackAlgorithm.LEGACY).setBaseH(0.91F).setBaseV(0.73F);

        assertTrue(event.usesJavaEdition1_8Knockback());
        assertEquals(0.4F, event.getKnockbackProfile().getBaseH());
        assertEquals(0.4F, event.getKnockbackProfile().getBaseV());
    }

    @Test
    void keepsOldAbsoluteConstructorOnLegacyPath() {
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(javaProfile());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, 0.8F, 0.5F);

        assertFalse(event.usesJavaEdition1_8Knockback());
        assertTrue(event.willApplyKnockback());
    }

    @Test
    void explicitZeroValuesCanStillGainLegacyEnchantKnockback() {
        KnockbackProfile profile = javaProfile();
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(profile);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, 0.0F, 0.0F);
        event.getKnockbackProfile().setEnchantLevel(1);

        assertFalse(event.usesJavaEdition1_8Knockback());
        assertTrue(event.willApplyKnockback());
        assertEquals(profile.getEnchantBonusH(), event.getKnockBackH());
        assertEquals(profile.getEnchantBonusV(), event.getKnockBackV());
    }

    @Test
    void clearDisablesRegularAndExtraKnockback() {
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(javaProfile());
        when(damager.isSprinting()).thenReturn(true);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);
        event.getKnockbackProfile().setEnchantLevel(2);
        event.clearKnockback();

        assertFalse(event.usesJavaEdition1_8Knockback());
        assertFalse(event.willApplyKnockback());
    }

    @Test
    void clearKeepsLegacyProfileMutationSemantics() {
        EntityLiving damager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(damager.getKnockbackProfile()).thenReturn(legacyProfile());

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F);
        event.clearKnockback();
        event.getKnockbackProfile().setEnchantLevel(1);

        assertFalse(event.usesJavaEdition1_8Knockback());
        assertTrue(event.willApplyKnockback());
    }

    @Test
    void copiesCompletePerHitContextForRedirectedDamage() {
        EntityLiving sourceDamager = mock(EntityLiving.class);
        EntityLiving redirectedDamager = mock(EntityLiving.class);
        Entity target = mock(Entity.class);
        when(sourceDamager.getKnockbackProfile()).thenReturn(javaProfile());
        when(sourceDamager.isSprinting()).thenReturn(true);
        when(sourceDamager.getYaw()).thenReturn(135.0D);
        when(redirectedDamager.getKnockbackProfile()).thenReturn(new KnockbackProfile("legacy"));

        EntityDamageByEntityEvent source = new EntityDamageByEntityEvent(
                sourceDamager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F, KnockbackSource.MELEE);
        source.getKnockbackProfile().setEnchantLevel(2);
        EntityDamageByEntityEvent redirected = new EntityDamageByEntityEvent(
                redirectedDamager, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0F);

        redirected.copyKnockbackProfileFrom(source);

        assertTrue(redirected.usesJavaEdition1_8Knockback());
        assertEquals(KnockbackSource.MELEE, redirected.getKnockbackSource());
        assertEquals(2, redirected.getKnockbackProfile().getEnchantLevel());
        assertTrue(redirected.wasAttackerSprinting());
        assertEquals(135.0F, redirected.getAttackerYaw());
    }

    @Test
    void preservesArrowMotionWhenProjectileDamageIsRedirected() {
        EntityLiving shooter = mock(EntityLiving.class);
        EntityArrow arrow = mock(EntityArrow.class);
        Entity target = mock(Entity.class);
        Entity redirectedTarget = mock(Entity.class);
        when(shooter.getKnockbackProfile()).thenReturn(javaProfile());
        arrow.motionX = 0.25D;
        arrow.motionZ = -0.75D;

        EntityDamageByChildEntityEvent source = new EntityDamageByChildEntityEvent(
                shooter, arrow, target, EntityDamageEvent.DamageCause.PROJECTILE, 1.0F, KnockbackSource.ARROW);
        source.getKnockbackProfile().setEnchantLevel(2);
        EntityDamageByEntityEvent redirected = new EntityDamageByEntityEvent(
                shooter, redirectedTarget, EntityDamageEvent.DamageCause.PROJECTILE, 1.0F);

        redirected.copyKnockbackProfileFrom(source);

        assertTrue(redirected.usesJavaEdition1_8Knockback());
        assertTrue(redirected.hasKnockbackSourceMotion());
        assertEquals(0.25D, redirected.getKnockbackSourceMotionX());
        assertEquals(-0.75D, redirected.getKnockbackSourceMotionZ());
    }

    private static KnockbackProfile javaProfile() {
        return new KnockbackProfile("je")
                .setAlgorithm(KnockbackAlgorithm.JAVA_EDITION_1_8)
                .setBaseH(0.4F)
                .setBaseV(0.4F);
    }

    private static KnockbackProfile legacyProfile() {
        return new KnockbackProfile("legacy")
                .setAlgorithm(KnockbackAlgorithm.LEGACY)
                .setBaseH(0.29F)
                .setBaseV(0.29F);
    }
}
