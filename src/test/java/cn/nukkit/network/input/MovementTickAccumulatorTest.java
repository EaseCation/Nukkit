package cn.nukkit.network.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovementTickAccumulatorTest {
    @Test
    void splittingAnAcceptedPathDoesNotMultiplyJumpOrDistanceExhaustion() {
        for (int frames : new int[]{1, 2, 5}) {
            MovementTickAccumulator statistics = new MovementTickAccumulator();
            for (int index = 0; index < frames; index++) {
                statistics.record(1.0 / frames, 0, 0, true, true, false, index == 0);
            }
            assertEquals(1, statistics.deltaX(), 1e-12);
            assertEquals(0.3f, statistics.exhaustion(), 1e-7f);
            assertTrue(statistics.movedHorizontally());
            statistics.clearTick();
            assertEquals(0, statistics.exhaustion());
            assertEquals(0, statistics.deltaX());
            assertFalse(statistics.movedHorizontally());
        }
    }

    @Test
    void turningBackConsumesDistanceWithoutInventingNetVelocity() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(1, 0, 0, true, true, false, false);
        statistics.record(-1, 0, 0, true, true, false, false);
        assertEquals(0, statistics.deltaX());
        assertEquals(0.2f, statistics.exhaustion());
        assertTrue(statistics.movedHorizontally());
    }

    @Test
    void sprintAndSwimmingSegmentsUseTheirOwnCommittedState() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0.5, 0, 0, true, true, false, false);
        statistics.record(0.5, 0, 0, true, true, true, true);
        statistics.record(0.5, 0, 0, true, false, false, false);
        assertEquals(0.0575f, statistics.exhaustion(), 1e-7f);
    }

    @Test
    void teleportClearsTheOldPositionSegmentButPreservesAlreadyEarnedFoodCost() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0.5, 0.1, 0, true, true, false, false);
        statistics.resetDisplacement();
        assertEquals(0, statistics.deltaX());
        assertEquals(0, statistics.deltaY());
        assertFalse(statistics.movedHorizontally());
        assertEquals(0.05f, statistics.exhaustion());
        statistics.record(0, -0.2, 0.1, true, true, false, false);
        assertEquals(-0.2, statistics.deltaY());
        assertEquals(0.1, statistics.deltaZ());
        assertEquals(0.06f, statistics.exhaustion(), 1e-7f);
    }

    @Test
    void thresholdIsAppliedToTheTickDistanceRatherThanEachSmallFrame() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        for (int index = 0; index < 5; index++) {
            statistics.record(0.02, 0, 0, true, true, false, index == 0);
        }
        assertEquals(0.21f, statistics.exhaustion(), 1e-7f);
    }

    @Test
    void rotationOnlyAndFoodExemptMovementDoNotChargeHunger() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0, 0, 0, true, true, false, false);
        statistics.record(1, 0, 0, false, true, false, true);
        assertEquals(0, statistics.exhaustion());
        assertEquals(1, statistics.deltaX());
    }

    @Test
    void verticalDepartureChargesWithoutHorizontalDistanceOrLaterInputFrames() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0, 0.42, 0, true, false, false, true);
        assertEquals(0.05f, statistics.exhaustion());
        statistics.clearTick();
        statistics.record(0, 0.3, 0, true, false, false, false);
        assertEquals(0, statistics.exhaustion());
    }

    @Test
    void twoConfirmedDeparturesInOneNetworkBurstRemainTwoActions() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0, 0.42, 0, true, true, false, true);
        statistics.record(0, -0.42, 0, true, true, false, false);
        statistics.record(0, 0.42, 0, true, true, false, true);
        assertEquals(0.4f, statistics.exhaustion());
    }

    @Test
    void duplicateAirFramesCannotChargeAnotherDeparture() {
        MovementTickAccumulator statistics = new MovementTickAccumulator();
        statistics.record(0, 0.42, 0, true, true, false, true);
        for (int index = 0; index < 20; index++) {
            statistics.record(0, 0, 0, true, true, false, false);
        }
        assertEquals(0.2f, statistics.exhaustion());
    }

    @Test
    void theExistingFloatDistanceBoundaryIsPreserved() {
        for (int frames : new int[]{1, 5}) {
            MovementTickAccumulator statistics = new MovementTickAccumulator();
            for (int index = 0; index < frames; index++) {
                statistics.record(0.05 / frames, 0, 0, true, true, false, false);
            }
            assertEquals(0.005f, statistics.exhaustion(), 1e-7f);
        }
    }
}
