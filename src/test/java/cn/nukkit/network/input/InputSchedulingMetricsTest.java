package cn.nukkit.network.input;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class InputSchedulingMetricsTest {
    private final AtomicLong clock = new AtomicLong();
    private final ServerInputDispatcher dispatcher = new ServerInputDispatcher(Thread.currentThread(),
            20000, 20000, 20000, 20000, exception -> fail(exception), clock::get);
    private final ServerInputDispatcher.Session session = new ServerInputDispatcher.Session(UUID.randomUUID());

    private void offer(boolean candidate) {
        dispatcher.offer(session, context -> { }, 1, candidate);
    }

    @Test
    void repeatedWakeupsDeduplicateHeadButIncludeLaterArrivalsWithoutScanning() {
        offer(false);
        offer(true);
        offer(true);
        clock.set(5_000_000);
        assertEquals(0, dispatcher.drain(true, 7, 100_000_000));
        clock.set(10_000_000);
        assertEquals(0, dispatcher.drain(true, 7, 100_000_000));
        clock.set(15_000_000);
        offer(true);
        assertEquals(1, dispatcher.schedulingSnapshot().staticBarrierHeads());
        assertEquals(10_000_000, dispatcher.schedulingSnapshot().observedBarrierNanos());
        assertEquals(2, dispatcher.snapshot().betweenTickBarriers());
        clock.set(50_000_000);
        assertEquals(4, dispatcher.drain(false, 8, 100_000_000));
        ServerInputDispatcher.SchedulingSnapshot value = dispatcher.schedulingSnapshot();
        assertEquals(4, value.worldTickExecutedTasks());
        assertEquals(3, value.candidateWorldTickTasks());
        assertEquals(3, value.candidateWorldTasksBehindBarrier());
        assertEquals(0, value.candidateWorldTasksAtOwnBarrier());
        assertEquals(45_000_000, value.observedBarrierNanos());
        assertEquals(135_000_000, value.candidateWorldBehindBarrierQueueWait().totalNanos());
        assertEquals(3, value.candidateQueueWait().le50Ms());
        assertEquals(0, value.candidateQueueWait().le25Ms());
        offer(true);
        clock.set(51_000_000);
        dispatcher.drain(false, 8, 100_000_000);
        assertEquals(4, dispatcher.schedulingSnapshot().candidateWorldTickTasks());
        assertEquals(3, dispatcher.schedulingSnapshot().candidateWorldTasksBehindBarrier());
    }

    @Test
    void ownDynamicGateIsDifferentFromFollowingAnotherBlockedTask() {
        dispatcher.offer(session, new ServerInputTask() {
            @Override public boolean canRunBetweenTicks() { return false; }
            @Override public void run(InboundContext context) { }
        }, 1, true);
        offer(true);
        dispatcher.drain(true, 1, 100);
        dispatcher.drain(false, 2, 100);
        ServerInputDispatcher.SchedulingSnapshot value = dispatcher.schedulingSnapshot();
        assertEquals(1, value.dynamicBarrierHeads());
        assertEquals(0, value.staticBarrierHeads());
        assertEquals(1, value.candidateWorldTasksAtOwnBarrier());
        assertEquals(1, value.candidateWorldTasksBehindBarrier());
        assertEquals(2, value.candidateWorldTickTasks());
    }

    @Test
    void aFormerFollowerCanLaterBecomeAnOwnDynamicBarrierWithoutLosingExposure() {
        dispatcher.offer(session, context -> clock.set(100), 1, false);
        dispatcher.offer(session, new ServerInputTask() {
            @Override public boolean canRunBetweenTicks() { return false; }
            @Override public void run(InboundContext context) { }
        }, 1, true);
        offer(true);
        dispatcher.drain(true, 1, 50);
        assertEquals(1, dispatcher.drain(false, 2, 50));
        dispatcher.drain(true, 2, 150);
        clock.set(200);
        dispatcher.drain(false, 3, 250);
        ServerInputDispatcher.SchedulingSnapshot value = dispatcher.schedulingSnapshot();
        assertEquals(1, value.staticBarrierHeads());
        assertEquals(1, value.dynamicBarrierHeads());
        assertEquals(2, value.candidateWorldTasksBehindBarrier());
        assertEquals(1, value.candidateWorldTasksAtOwnBarrier());
    }

    @Test
    void restoredGateDoesNotClaimThatExposedFollowersRanInWorldTick() {
        AtomicBoolean ready = new AtomicBoolean();
        dispatcher.offer(session, new ServerInputTask() {
            @Override public boolean canRunBetweenTicks() { return ready.get(); }
            @Override public void run(InboundContext context) { assertTrue(context.betweenTicks()); }
        }, 1, true);
        offer(true);
        dispatcher.drain(true, 1, 100);
        ready.set(true);
        clock.set(20);
        assertEquals(2, dispatcher.drain(true, 1, 100));
        ServerInputDispatcher.SchedulingSnapshot value = dispatcher.schedulingSnapshot();
        assertEquals(2, value.candidateBetweenTickTasks());
        assertEquals(1, value.candidateTasksBehindBarrier());
        assertEquals(0, value.candidateWorldTasksBehindBarrier());
        assertEquals(20, value.observedBarrierNanos());
    }

    @Test
    void observedInvalidatedHeadAndDiscardedFollowersDoNotBecomeExecutedSamples() {
        offer(false);
        offer(true);
        dispatcher.drain(true, 1, 100);
        dispatcher.invalidate(session);
        clock.set(20);
        assertEquals(0, dispatcher.drain(false, 2, 100));
        assertEquals(2, dispatcher.snapshot().discardedTasks());
        assertEquals(0, dispatcher.schedulingSnapshot().candidateQueueWait().count());
        assertEquals(20, dispatcher.schedulingSnapshot().observedBarrierNanos());
        clock.set(40);
        assertEquals(20, dispatcher.schedulingSnapshot().observedBarrierNanos());
    }

    @Test
    void worldPhaseWithoutObservedBarrierIsNotAttributedToHol() {
        for (int i = 0; i < 10000; i++) offer(true);
        assertEquals(10000, dispatcher.drain(false, 1, 100));
        ServerInputDispatcher.SchedulingSnapshot value = dispatcher.schedulingSnapshot();
        assertEquals(10000, value.candidateWorldTickTasks());
        assertEquals(0, value.candidateTasksBehindBarrier());
        assertEquals(0, value.staticBarrierHeads());
        assertEquals(10000, value.candidateQueueWait().le1Ms());
    }

    @Test
    void observationDoesNotEvaluateFollowerEligibilityOrReorderConnections() {
        List<String> order = new ArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        dispatcher.offer(session, context -> order.add("head"), 1, false);
        ServerInputDispatcher.Session second = new ServerInputDispatcher.Session(UUID.randomUUID());
        dispatcher.offer(second, new ServerInputTask() {
            @Override public boolean canRunBetweenTicks() { calls.incrementAndGet(); return true; }
            @Override public void run(InboundContext context) { order.add("follower"); }
        }, 1, true);
        dispatcher.drain(true, 1, 100);
        dispatcher.schedulingSnapshot();
        dispatcher.schedulingSnapshot();
        dispatcher.drain(false, 2, 100);
        assertEquals(0, calls.get());
        assertEquals(List.of("head", "follower"), order);
        assertEquals(1, dispatcher.schedulingSnapshot().candidateWorldTasksBehindBarrier());
    }

    @Test
    void queueWaitHistogramPreservesLongTailAndSnapshotsDoNotMutate() {
        offer(true);
        clock.set(300_000_000L);
        dispatcher.drain(true, 1, 400_000_000L);
        ServerInputDispatcher.QueueWaitSnapshot first = dispatcher.schedulingSnapshot().candidateQueueWait();
        assertEquals(1, first.gt250Ms());
        assertEquals(0, first.le250Ms());
        offer(true);
        dispatcher.drain(true, 1, 400_000_000L);
        assertEquals(1, first.count());
        assertEquals(2, dispatcher.schedulingSnapshot().candidateQueueWait().count());
    }

    @Test
    void sparseAndDenseWorldOnlyTrafficHaveDistinctMeasuredFallbackShares() {
        for (int period : new int[]{1, 10}) {
            AtomicLong time = new AtomicLong();
            ServerInputDispatcher target = new ServerInputDispatcher(Thread.currentThread(), 200, 200,
                    200, 200, exception -> fail(exception), time::get);
            ServerInputDispatcher.Session player = new ServerInputDispatcher.Session(UUID.randomUUID());
            for (int tick = 0; tick < 100; tick++) {
                time.set(tick * 50_000_000L);
                boolean barrier = tick % period == 0;
                if (barrier) target.offer(player, context -> { }, 1, false);
                for (int i = 0; i < 100; i++) target.offer(player, context -> { }, 1, true);
                time.addAndGet(1_000_000);
                target.drain(true, tick, time.get() + 10_000_000);
                time.set((tick + 1) * 50_000_000L);
                target.drain(false, tick + 1, time.get() + 10_000_000);
            }
            ServerInputDispatcher.SchedulingSnapshot value = target.schedulingSnapshot();
            long candidates = value.candidateBetweenTickTasks() + value.candidateWorldTickTasks();
            assertEquals(10000, candidates);
            assertEquals(10000 / period, value.candidateWorldTasksBehindBarrier());
            assertEquals(100 / period, value.staticBarrierHeads());
            assertEquals(1D / period, (double) value.candidateWorldTickTasks() / candidates);
            assertEquals(0, target.snapshot().queuedTasks());
        }
    }

    @Test
    void closeEndsObservedIntervalWithoutInventingWorldExecutions() {
        offer(false);
        offer(true);
        dispatcher.drain(true, 1, 100);
        clock.set(50);
        dispatcher.close();
        clock.set(80);
        assertEquals(50, dispatcher.schedulingSnapshot().observedBarrierNanos());
        assertEquals(0, dispatcher.schedulingSnapshot().candidateWorldTickTasks());
    }
}
