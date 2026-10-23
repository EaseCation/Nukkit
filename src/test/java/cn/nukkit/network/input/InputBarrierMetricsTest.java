package cn.nukkit.network.input;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class InputBarrierMetricsTest {
    private final AtomicLong clock = new AtomicLong();
    private final ServerInputDispatcher dispatcher = new ServerInputDispatcher(Thread.currentThread(),
            20000, 20000, 20000, 20000, exception -> fail(exception), clock::get);
    private final ServerInputDispatcher.Session first = new ServerInputDispatcher.Session(UUID.randomUUID());
    private final ServerInputDispatcher.Session second = new ServerInputDispatcher.Session(UUID.randomUUID());

    private ServerInputTask task(String type) {
        return new ServerInputTask() {
            @Override public void run(InboundContext context) { }
            @Override public String diagnosticType() { return type; }
        };
    }

    @Test
    void repeatedPollsDoNotDuplicateHeadsAndCrossPlayerArrivalsAreCounted() {
        dispatcher.enableBarrierDiagnostics();
        dispatcher.offer(first, task("InventoryTransaction"), 1, false);
        dispatcher.offer(second, task("Pong"), 1, true);
        for (int index = 0; index < 10000; index++) dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(10);
        dispatcher.offer(second, task("Movement"), 1, true);
        clock.set(50);
        dispatcher.drainWorldTick(2);
        ServerInputDispatcher.BarrierReport report = dispatcher.drainBarrierReport();
        assertNotNull(report);
        ServerInputDispatcher.BarrierTypeSummary summary = report.types().getFirst();
        assertEquals(1, summary.heads());
        assertEquals(1, summary.followersAtStart());
        assertEquals(1, summary.arrivalsWhileBlocked());
        assertEquals(2, summary.maxFollowers());
        assertEquals(50, summary.observedNanos());
        assertEquals(10000, dispatcher.snapshot().betweenTickBarriers());
        assertEquals(3, dispatcher.snapshot().executedTasks());
    }

    @Test
    void longBarrierSplitsTimeAcrossReportsWithoutRecountingHead() {
        dispatcher.enableBarrierDiagnostics();
        dispatcher.offer(first, task("CustomPayload"), 1, false);
        dispatcher.offer(second, task("Pong"), 1, true);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(60);
        ServerInputDispatcher.BarrierReport one = dispatcher.drainBarrierReport();
        assertEquals(1, one.windowSequence());
        assertEquals(1, one.types().getFirst().heads());
        assertEquals(60, one.types().getFirst().observedNanos());
        assertEquals("CustomPayload", one.activeType());
        dispatcher.offer(second, task("AnotherMovement"), 1, true);
        clock.set(120);
        ServerInputDispatcher.BarrierReport two = dispatcher.drainBarrierReport();
        assertEquals(2, two.windowSequence());
        assertEquals(0, two.types().getFirst().heads());
        assertEquals(0, two.types().getFirst().followersAtStart());
        assertEquals(1, two.types().getFirst().arrivalsWhileBlocked());
        assertEquals(2, two.types().getFirst().maxFollowers());
        assertEquals(60, two.types().getFirst().observedNanos());
        assertEquals(120, two.types().getFirst().maxObservedHeadNanos());
        clock.set(140);
        dispatcher.drainWorldTick(2);
        assertEquals(20, dispatcher.drainBarrierReport().types().getFirst().observedNanos());
        assertTrue(dispatcher.drainBarrierReport().types().isEmpty());
    }

    @Test
    void diagnosticsNeverRechecksDynamicGateAndPreservesFifo() {
        dispatcher.enableBarrierDiagnostics();
        AtomicBoolean ready = new AtomicBoolean();
        AtomicInteger checks = new AtomicInteger();
        List<String> order = new ArrayList<>();
        dispatcher.offer(first, new ServerInputTask() {
            @Override public void run(InboundContext context) { order.add("first"); }
            @Override public boolean canRunBetweenTicks() { checks.incrementAndGet(); return ready.get(); }
            @Override public String diagnosticType() { return "Movement"; }
        }, 1, true);
        dispatcher.offer(second, context -> order.add("second"), 1, true);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(10);
        assertTrue(dispatcher.drainBarrierReport().types().getFirst().dynamic());
        assertEquals(1, checks.get());
        ready.set(true);
        clock.set(20);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        assertEquals(List.of("first", "second"), order);
        assertEquals(2, checks.get());
    }

    @Test
    void cardinalityOverflowKeepsExactTotals() {
        dispatcher.enableBarrierDiagnostics();
        for (int index = 0; index < 1000; index++) {
            dispatcher.offer(first, task("Type" + index), 1, false);
            dispatcher.drain(true, 1, Long.MAX_VALUE);
            clock.incrementAndGet();
            dispatcher.drainWorldTick(2);
        }
        ServerInputDispatcher.BarrierReport report = dispatcher.drainBarrierReport();
        assertEquals(InputBarrierMetrics.MAX_TYPES + 1, report.types().size());
        assertEquals(1000, report.types().stream().mapToLong(ServerInputDispatcher.BarrierTypeSummary::heads).sum());
        assertEquals(1000, report.types().stream().mapToLong(ServerInputDispatcher.BarrierTypeSummary::observedNanos).sum());
        assertEquals(936, report.types().stream().filter(s -> s.type().equals("OTHER")).findFirst().orElseThrow().heads());
    }

    @Test
    void invalidatedHeadEndsObservationAndDoesNotExecuteOldTask() {
        dispatcher.enableBarrierDiagnostics();
        dispatcher.offer(first, context -> fail("Invalid task ran"), 1, false);
        dispatcher.offer(second, task("Pong"), 1, true);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        dispatcher.invalidate(first);
        clock.set(5);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertEquals(1, dispatcher.snapshot().executedTasks());
        assertEquals(5, dispatcher.drainBarrierReport().types().getFirst().observedNanos());
    }

    @Test
    void noDiagnosticsMeansNoTypeCallbackAndNoReport() {
        dispatcher.offer(first, new ServerInputTask() {
            @Override public void run(InboundContext context) { }
            @Override public String diagnosticType() { throw new AssertionError("Diagnostics disabled"); }
            @Override public String diagnosticReason() { throw new AssertionError("Diagnostics disabled"); }
        }, 1, false);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        assertNull(dispatcher.drainBarrierReport());
    }

    @Test
    void closeAccountsActiveHeadOnce() {
        dispatcher.enableBarrierDiagnostics();
        dispatcher.offer(first, task("Control"), 1, false);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(20);
        dispatcher.close();
        clock.set(40);
        ServerInputDispatcher.BarrierReport report = dispatcher.drainBarrierReport();
        assertNull(report.activeType());
        assertEquals(20, report.types().getFirst().observedNanos());
        assertTrue(dispatcher.drainBarrierReport().types().isEmpty());
    }
    @Test
    void firstObservedReasonStaysStableAcrossRechecksAndWindowBoundaries() {
        dispatcher.enableBarrierDiagnostics();
        AtomicInteger evaluations = new AtomicInteger();
        AtomicInteger reads = new AtomicInteger();
        dispatcher.offer(first, new ServerInputTask() {
            private String reason;
            @Override public void run(InboundContext context) { }
            @Override public boolean canRunBetweenTicks() {
                reason = evaluations.incrementAndGet() == 1 ? "SLEEPING" : "GLIDING";
                return false;
            }
            @Override public String diagnosticType() { return "Movement"; }
            @Override public String diagnosticReason() { reads.incrementAndGet(); return reason; }
        }, 1, true);
        dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(10);
        assertEquals("SLEEPING", dispatcher.drainBarrierReport().activeReason());
        for (int i = 0; i < 1000; i++) dispatcher.drain(true, 1, Long.MAX_VALUE);
        clock.set(20);
        ServerInputDispatcher.BarrierTypeSummary summary = dispatcher.drainBarrierReport().types().getFirst();
        assertEquals("SLEEPING", summary.reason());
        assertEquals(0, summary.heads());
        assertEquals(10, summary.observedNanos());
        assertEquals(1001, evaluations.get());
        assertEquals(1, reads.get());
        dispatcher.drainWorldTick(2);
    }

    @Test
    void reasonsShareTheExistingCardinalityBoundAndPreserveTotals() {
        dispatcher.enableBarrierDiagnostics();
        for (int i = 0; i < 1000; i++) {
            String reason = "REASON_" + i;
            dispatcher.offer(first, new ServerInputTask() {
                @Override public void run(InboundContext context) { }
                @Override public boolean canRunBetweenTicks() { return false; }
                @Override public String diagnosticType() { return "Movement"; }
                @Override public String diagnosticReason() { return reason; }
            }, 1, true);
            dispatcher.drain(true, 1, Long.MAX_VALUE);
            clock.incrementAndGet();
            dispatcher.drainWorldTick(2);
        }
        ServerInputDispatcher.BarrierReport report = dispatcher.drainBarrierReport();
        assertEquals(65, report.types().size());
        assertEquals(1000, report.types().stream().mapToLong(ServerInputDispatcher.BarrierTypeSummary::heads).sum());
        assertEquals(1000, report.types().stream().mapToLong(ServerInputDispatcher.BarrierTypeSummary::observedNanos).sum());
        assertEquals(936, report.types().stream().filter(t -> t.reason().equals("OTHER")).findFirst().orElseThrow().heads());
    }

    @Test
    void disabledDiagnosticsDoesNotReadReasonEvenForDynamicFailure() {
        AtomicInteger checks = new AtomicInteger();
        dispatcher.offer(first, new ServerInputTask() {
            @Override public void run(InboundContext context) { }
            @Override public boolean canRunBetweenTicks() { checks.incrementAndGet(); return false; }
            @Override public String diagnosticReason() { throw new AssertionError("Diagnostics disabled"); }
        }, 1, true);
        assertEquals(0, dispatcher.drain(true, 1, Long.MAX_VALUE));
        assertEquals(1, checks.get());
        assertNull(dispatcher.drainBarrierReport());
        assertEquals(1, dispatcher.drainWorldTick(2));
        assertEquals(1, checks.get());
    }

}
