package cn.nukkit.entity.knockback;

import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaEdition1_8KnockbackTest {

    private static final double EPSILON = 1.0E-6D;

    @Test
    void appliesRegularKnockbackToStationaryTarget() {
        Vector3 result = JavaEdition1_8Knockback.applyRegular(
                Vector3.ZERO, 1.0D, 0.0D, javaProfile());

        assertMotion(result, 0.4D, 0.4D, 0.0D);
    }

    @Test
    void inheritsHalfOfPreviousMotion() {
        Vector3 result = JavaEdition1_8Knockback.applyRegular(
                new Vector3(0.6D, -0.2D, -0.4D),
                0.0D,
                2.0D,
                javaProfile()
        );

        assertMotion(result, 0.3D, 0.3D, 0.2D);
    }

    @Test
    void capsRegularVerticalMotionAtBaseVertical() {
        Vector3 result = JavaEdition1_8Knockback.applyRegular(
                new Vector3(0.0D, 0.6D, 0.0D),
                1.0D,
                0.0D,
                javaProfile()
        );

        assertMotion(result, 0.4D, 0.4D, 0.0D);
    }

    @Test
    void keepsRegularAndMeleeExtraDirectionsIndependent() {
        KnockbackProfile profile = javaProfile();
        Vector3 regular = JavaEdition1_8Knockback.applyRegular(Vector3.ZERO, 1.0D, 0.0D, profile);
        Vector3 result = JavaEdition1_8Knockback.applyMeleeExtra(regular, 0.0F, 1, profile);

        assertMotion(result, 0.4D, 0.5D, 0.5D);
    }

    @Test
    void addsMeleeVerticalOnlyOnceForMultipleLevels() {
        KnockbackProfile profile = javaProfile();
        Vector3 levelOne = JavaEdition1_8Knockback.applyMeleeExtra(Vector3.ZERO, 90.0F, 1, profile);
        Vector3 levelThree = JavaEdition1_8Knockback.applyMeleeExtra(Vector3.ZERO, 90.0F, 3, profile);

        assertMotion(levelOne, -0.5D, 0.1D, 0.0D);
        assertMotion(levelThree, -1.5D, 0.1D, 0.0D);
    }

    @Test
    void appliesArrowExtraAlongArrowMotion() {
        Vector3 result = JavaEdition1_8Knockback.applyArrowExtra(
                new Vector3(0.2D, 0.3D, 0.4D),
                3.0D,
                4.0D,
                2,
                javaProfile()
        );

        assertMotion(result, 0.92D, 0.4D, 1.36D);
    }

    @Test
    void ignoresArrowExtraWithoutHorizontalMotion() {
        Vector3 result = JavaEdition1_8Knockback.applyArrowExtra(
                new Vector3(0.2D, 0.3D, 0.4D),
                0.0D,
                0.0D,
                2,
                javaProfile()
        );

        assertMotion(result, 0.2D, 0.3D, 0.4D);
    }

    @Test
    void appliesFishingReelAsAdditiveMotion() {
        Vector3 result = JavaEdition1_8Knockback.applyFishingReel(
                new Vector3(0.1D, 0.2D, 0.3D),
                new Vector3(4.0D, 6.0D, 3.0D),
                new Vector3(1.0D, 2.0D, 3.0D),
                javaProfile()
        );

        assertMotion(result, 0.4D, 0.6D + Math.sqrt(5.0D) * 0.08D, 0.3D);
    }

    @Test
    void usesAllOrNothingResistanceCheck() {
        assertFalse(JavaEdition1_8Knockback.shouldApplyKnockback(0.4D, 0.399999D));
        assertTrue(JavaEdition1_8Knockback.shouldApplyKnockback(0.4D, 0.4D));
    }

    @Test
    void appliesMeleeExtraWithoutRegularKnockback() {
        Vector3 extraOnly = JavaEdition1_8Knockback.applyMeleeExtra(
                Vector3.ZERO, 0.0F, 1, javaProfile());
        assertMotion(extraOnly, 0.0D, 0.1D, 0.5D);
    }

    @Test
    void initializesVanillaJavaDefaultsWithoutChangingLegacyDefaults() {
        KnockbackProfile legacy = new KnockbackProfile("legacy");
        KnockbackProfile java = javaProfile();

        assertEquals(0.29F, legacy.getBaseH());
        assertEquals(0.4F, java.getBaseH());
        assertEquals(0.4F, java.getBaseV());
        assertEquals(0.5F, java.getFriction());
        assertEquals(0.5F, java.getEnchantBonusH());
        assertEquals(0.1F, java.getEnchantBonusV());
        assertEquals(0.6F, java.getBowBaseH());
        assertEquals(0.1F, java.getRodBaseH());
        assertEquals(0.08F, java.getRodDistanceLift());
        assertEquals(0.6F, java.getSprintSlowdownH());
        assertTrue(java.isStopSprinting());

        java.setBaseH(0.7F).setAlgorithm(KnockbackAlgorithm.JAVA_EDITION_1_8);
        assertEquals(0.7F, java.getBaseH());
        assertEquals(0.08F, java.copy("copy").getRodDistanceLift());
    }

    @Test
    void appliesCustomJavaProfileValues() {
        KnockbackProfile profile = javaProfile()
                .setBaseH(0.7F)
                .setBaseV(0.2F)
                .setFriction(0.25F)
                .setInheritRatioH(2.0F)
                .setInheritRatioV(3.0F)
                .setVerticalLimit(0.9F)
                .setEnchantBonusH(0.3F)
                .setEnchantBonusV(0.07F)
                .setBowBaseH(0.8F)
                .setBowBaseV(0.2F)
                .setRodBaseH(0.2F)
                .setRodBaseV(0.3F)
                .setRodDistanceLift(0.04F);

        Vector3 regular = JavaEdition1_8Knockback.applyRegular(
                new Vector3(1.0D, 1.0D, 1.0D), 1.0D, 0.0D, profile);
        Vector3 melee = JavaEdition1_8Knockback.applyMeleeExtra(Vector3.ZERO, 0.0F, 2, profile);
        Vector3 arrow = JavaEdition1_8Knockback.applyArrowExtra(
                Vector3.ZERO, 3.0D, 4.0D, 2, profile);
        Vector3 fishing = JavaEdition1_8Knockback.applyFishingReel(
                Vector3.ZERO, new Vector3(4.0D, 6.0D, 3.0D),
                new Vector3(1.0D, 2.0D, 3.0D), profile);

        assertMotion(regular, 1.2D, 0.9D, 0.5D);
        assertMotion(melee, 0.0D, 0.07D, 0.6D);
        assertMotion(arrow, 0.96D, 0.2D, 1.28D);
        assertMotion(fishing, 0.6D, 1.2D + Math.sqrt(5.0D) * 0.04D, 0.0D);
    }

    @Test
    void appliesConfigurableTargetStateMultipliers() {
        KnockbackProfile profile = javaProfile()
                .setGroundMultiplierH(1.5F)
                .setGroundMultiplierV(0.75F)
                .setAirMultiplierH(0.8F)
                .setAirMultiplierV(1.2F);

        Vector3 ground = JavaEdition1_8Knockback.applyTargetStateMultiplier(
                new Vector3(0.4D, 0.4D, -0.2D), true, profile);
        Vector3 air = JavaEdition1_8Knockback.applyTargetStateMultiplier(
                new Vector3(0.4D, 0.4D, -0.2D), false, profile);

        assertMotion(ground, 0.6D, 0.3D, -0.3D);
        assertMotion(air, 0.32D, 0.48D, -0.16D);
    }

    private static KnockbackProfile javaProfile() {
        return new KnockbackProfile("je").setAlgorithm(KnockbackAlgorithm.JAVA_EDITION_1_8);
    }

    private static void assertMotion(Vector3 actual, double expectedX, double expectedY, double expectedZ) {
        assertEquals(expectedX, actual.x, EPSILON);
        assertEquals(expectedY, actual.y, EPSILON);
        assertEquals(expectedZ, actual.z, EPSILON);
    }
}
