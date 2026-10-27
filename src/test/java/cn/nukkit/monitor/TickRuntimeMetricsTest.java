package cn.nukkit.monitor;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class TickRuntimeMetricsTest {
    @Test void normalDenominatorAndLongIntervalsAreIndependentOfWorldWork() {
        TickRuntimeMetrics metrics = new TickRuntimeMetrics();
        metrics.start(1_000_000_000);
        metrics.finish(10_000_000);
        metrics.start(1_050_000_000);
        metrics.finish(20_000_000);
        metrics.start(1_250_000_000);
        metrics.finish(5_000_000);
        TickRuntimeMetrics.Snapshot snapshot = metrics.snapshot();
        assertEquals(2, snapshot.intervals()[6]);
        assertEquals(3, snapshot.work()[6]);
        assertEquals(250_000_000, snapshot.intervalNanos());
        assertEquals(35_000_000, snapshot.workNanos());
        assertEquals(150_000_000, snapshot.overrunNanos());
        assertEquals(1, snapshot.stalls());
        assertEquals(20_000_000, snapshot.lastStallWorkNanos());
        assertEquals(1, snapshot.intervals()[1]);
        assertEquals(1_250_000_000, metrics.lastStartNanos());
    }

    @Test void thresholdEqualityAndCatchupDoNotEraseExistingOverrun() {
        TickRuntimeMetrics metrics = new TickRuntimeMetrics();
        metrics.start(1);
        metrics.finish(100);
        metrics.start(100_000_001);
        metrics.finish(100);
        metrics.start(100_000_002);
        assertEquals(0, metrics.snapshot().stalls());
        assertEquals(50_000_000, metrics.snapshot().overrunNanos());
        assertEquals(2, metrics.snapshot().intervals()[6]);
    }

    @Test void concurrentScrapesNeverExposePartialOrDecreasingCounters() throws Exception {
        TickRuntimeMetrics metrics = new TickRuntimeMetrics();
        AtomicBoolean running = new AtomicBoolean(true);
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 200_000; i++) {
                metrics.start(1 + i * 50_000_000L);
                metrics.finish(1_000_000);
            }
            running.set(false);
        });
        writer.start();
        long previous = 0;
        do {
            TickRuntimeMetrics.Snapshot snapshot = metrics.snapshot();
            assertTrue(snapshot.intervals()[6] >= previous);
            previous = snapshot.intervals()[6];
            assertEquals(previous * 50_000_000L, snapshot.intervalNanos());
            assertEquals(snapshot.work()[6] * 1_000_000L, snapshot.workNanos());
        } while (running.get());
        writer.join();
        assertEquals(199_999, metrics.snapshot().intervals()[6]);
    }
}
