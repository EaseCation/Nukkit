package cn.nukkit.monitor;

import java.lang.invoke.VarHandle;

/**
 * 单主线程写入，抓取线程有界读取。无逐周期分配、锁、回调或 IO。
 * 间隔按完成时刻计账；未完成停顿通过 lastStartNanos 单独暴露。
 */
public final class TickRuntimeMetrics {
    private static final long[] LIMITS = {50_000_000L, 100_000_000L, 200_000_000L,
            500_000_000L, 1_000_000_000L, 2_000_000_000L, Long.MAX_VALUE};
    private final long[] intervals = new long[LIMITS.length];
    private final long[] work = new long[LIMITS.length];
    private volatile long version;
    private volatile long lastStartNanos;
    private boolean started;
    private long intervalNanos;
    private long workNanos;
    private long overrunNanos;
    private long stalls;
    private long lastStallNanos;
    private long lastStallGapNanos;
    private long previousWorkNanos;
    private long lastStallWorkNanos;
    private volatile Snapshot cached = new Snapshot(new long[7], new long[7], 0, 0, 0, 0, 0, 0, 0, 0);

    public void start(long now) {
        version++;
        if (started) {
            long gap = Math.max(0, now - lastStartNanos);
            record(intervals, gap);
            intervalNanos += gap;
            overrunNanos += Math.max(0, gap - LIMITS[0]);
            if (gap > LIMITS[1]) {
                stalls++;
                lastStallNanos = now;
                lastStallGapNanos = gap;
                lastStallWorkNanos = previousWorkNanos;
            }
        }
        started = true;
        lastStartNanos = now;
        version++;
    }

    public void finish(long elapsed) {
        version++;
        previousWorkNanos = Math.max(0, elapsed);
        workNanos += previousWorkNanos;
        record(work, previousWorkNanos);
        version++;
    }

    private static void record(long[] buckets, long nanos) {
        for (int i = 0; i < LIMITS.length; i++) {
            if (nanos <= LIMITS[i]) buckets[i]++;
        }
    }

    public long lastStartNanos() {
        return lastStartNanos;
    }

    /**
     * 写入撞车最多尝试三次；失败复用已发布快照，不让抓取线程无限自旋。
     */
    public Snapshot snapshot() {
        for (int attempt = 0; attempt < 3; attempt++) {
            long before = version;
            if ((before & 1) != 0) continue;
            Snapshot result = new Snapshot(intervals.clone(), work.clone(), intervalNanos, workNanos,
                    overrunNanos, stalls, lastStallNanos, lastStallGapNanos, lastStallWorkNanos, before);
            VarHandle.acquireFence();
            if (version == before) {
                // 多读取者不覆写缓存，抓取自己的快照即可；缓存只用于争用回退。
                return publish(result);
            }
        }
        return cached;
    }

    private synchronized Snapshot publish(Snapshot result) {
        // 锁只在后台读取者之间使用，游戏线程从不进入。
        if (result.version() > cached.version()) cached = result;
        return cached;
    }

    public record Snapshot(long[] intervals, long[] work, long intervalNanos, long workNanos,
                           long overrunNanos, long stalls, long lastStallNanos,
                           long lastStallGapNanos, long lastStallWorkNanos, long version) {
    }
}
