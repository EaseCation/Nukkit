package cn.nukkit.network.input;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/** 单主线程消费的网络输入 FIFO；唤醒不会推进世界 tick。 */
public final class ServerInputDispatcher {
    public enum OfferResult {
        ACCEPTED,
        CLOSED,
        OVERLOADED
    }

    /** 每次连接创建独立实例，不能按 UUID 复用旧连接的计数和存活状态。 */
    public static final class Session {
        private final UUID id;
        private volatile boolean active = true;
        private int queuedTasks;
        private long queuedBytes;
        private long nextSequence;

        public Session(UUID id) {
            this.id = Objects.requireNonNull(id);
        }

        public boolean isActive() {
            return active;
        }

        public UUID getId() {
            return id;
        }
    }

    public record Snapshot(int queuedTasks, long queuedBytes, long executedTasks, long discardedTasks,
                           long rejectedTasks, long betweenTickBarriers, long overBudgetTasks, boolean closed) {
    }

    /** 候选表示入队时允许尝试补充阶段，不表示消费时动态门禁一定通过。 */
    public record SchedulingSnapshot(long sampledNanos, long betweenTickExecutedTasks, long worldTickExecutedTasks,
                                     long candidateBetweenTickTasks, long candidateWorldTickTasks,
                                     long candidateTasksBehindBarrier, long candidateWorldTasksBehindBarrier,
                                     long candidateWorldTasksAtOwnBarrier, long staticBarrierHeads,
                                     long dynamicBarrierHeads, long observedBarrierNanos,
                                     QueueWaitSnapshot candidateQueueWait,
                                     QueueWaitSnapshot candidateWorldBehindBarrierQueueWait) {
    }

    /** 累计排队时长分桶；le 为累计桶，gt250Ms 单独记录超过最大桶的数量。 */
    public record QueueWaitSnapshot(long count, long totalNanos, long le1Ms, long le5Ms, long le10Ms,
                                    long le25Ms, long le50Ms, long le100Ms, long le250Ms, long gt250Ms) {
    }

    /**
     * 每个新阻塞头只计一次；后继数是各阻塞事件的观察量，同一任务可被不同头重复阻塞。
     */
    public record BarrierTypeSummary(String type, boolean dynamic, long heads, long followersAtStart,
                                     long arrivalsWhileBlocked, int maxFollowers, long observedNanos,
                                     long maxObservedHeadNanos) {
    }

    /**
     * 分钟窗口的实际单调时间与有界类型汇总；跨窗头只续记时间，不重计 heads。
     */
    public record BarrierReport(long windowSequence, long fromNanos, long toNanos, int queuedTasks, long queuedBytes,
                                @Nullable String activeType, boolean activeDynamic, long activeSequence,
                                int activeFollowers, List<BarrierTypeSummary> types) {
    }

    @Nullable
    private InputBarrierMetrics barrierMetrics;

    private record Entry(Session session, ServerInputTask task, int bytes, boolean betweenTicks,
                         long sequence, long sessionSequence, long enqueuedNanos, long receivedNanos) {
    }

    private final Object lock = new Object();
    private final Queue<Entry> queue = new ArrayDeque<>();
    private final Thread consumerThread;
    private final int taskLimit;
    private final long byteLimit;
    private final int sessionTaskLimit;
    private final long sessionByteLimit;
    private final Consumer<RuntimeException> failureHandler;
    private final LongSupplier clock;
    private long queuedBytes;
    private long nextSequence;
    private long executedTasks;
    private long discardedTasks;
    private long rejectedTasks;
    private long betweenTickBarriers;
    private long overBudgetTasks;
    private long betweenTickExecutedTasks;
    private long candidateBetweenTickTasks;
    private long candidateWorldTickTasks;
    private long candidateTasksBehindBarrier;
    private long candidateWorldTasksBehindBarrier;
    private long candidateWorldTasksAtOwnBarrier;
    private long staticBarrierHeads;
    private long dynamicBarrierHeads;
    private long lastBarrierSequence;
    private boolean lastBarrierHeadWasFollower;
    private long barrierAffectedThrough;
    private long activeBarrierSequence;
    private long activeBarrierSince;
    private long observedBarrierNanos;
    private final QueueWaitCounter candidateQueueWait = new QueueWaitCounter();
    private final QueueWaitCounter candidateWorldBehindBarrierQueueWait = new QueueWaitCounter();
    private volatile boolean closed;
    private boolean consuming;
    @Nullable
    private InboundContext currentContext;

    public ServerInputDispatcher(Thread consumerThread, int taskLimit, long byteLimit,
                                 int sessionTaskLimit, long sessionByteLimit,
                                 Consumer<RuntimeException> failureHandler) {
        this(consumerThread, taskLimit, byteLimit, sessionTaskLimit, sessionByteLimit,
                failureHandler, System::nanoTime);
    }

    ServerInputDispatcher(Thread consumerThread, int taskLimit, long byteLimit,
                          int sessionTaskLimit, long sessionByteLimit,
                          Consumer<RuntimeException> failureHandler, LongSupplier clock) {
        if (taskLimit < 1 || byteLimit < 1 || sessionTaskLimit < 1 || sessionByteLimit < 1) {
            throw new IllegalArgumentException("Input queue limits must be positive");
        }
        this.consumerThread = Objects.requireNonNull(consumerThread);
        this.taskLimit = taskLimit;
        this.byteLimit = byteLimit;
        this.sessionTaskLimit = sessionTaskLimit;
        this.sessionByteLimit = sessionByteLimit;
        this.failureHandler = Objects.requireNonNull(failureHandler);
        this.clock = Objects.requireNonNull(clock);
    }

    public OfferResult offer(Session session, ServerInputTask task, int bytes, boolean betweenTicks) {
        return offer(session, task, bytes, betweenTicks, 0);
    }

    /** 接入时间只能由本进程网络入口提供，不能来自客户端或其他 JVM 的时钟。 */
    public OfferResult offer(Session session, ServerInputTask task, int bytes, boolean betweenTicks, long receivedNanos) {
        Objects.requireNonNull(session);
        Objects.requireNonNull(task);
        if (bytes < 0) {
            throw new IllegalArgumentException("Input byte count must not be negative");
        }
        OfferResult result;
        synchronized (lock) {
            if (closed || !session.active) {
                rejectedTasks++;
                return OfferResult.CLOSED;
            }
            if (queue.size() >= taskLimit || bytes > byteLimit - queuedBytes
                    || session.queuedTasks >= sessionTaskLimit || bytes > sessionByteLimit - session.queuedBytes) {
                // 调用方必须走原连接关闭出口；不允许失败后改投旧队列。
                session.active = false;
                rejectedTasks++;
                result = OfferResult.OVERLOADED;
            } else {
                queue.add(new Entry(session, task, bytes, betweenTicks, ++nextSequence,
                        ++session.nextSequence, clock.getAsLong(), receivedNanos));
                queuedBytes += bytes;
                session.queuedTasks++;
                session.queuedBytes += bytes;
                // 只扩展已观测屏障覆盖的序号范围，不扫描队列或预读后包的动态资格。
                if (activeBarrierSequence != 0) barrierAffectedThrough = nextSequence;
                result = OfferResult.ACCEPTED;
            }
        }
        // LockSupport 的 permit 覆盖入队发生在 park 前后的两种时序。
        LockSupport.unpark(consumerThread);
        return result;
    }

    public void invalidate(Session session) {
        synchronized (lock) {
            session.active = false;
        }
        LockSupport.unpark(consumerThread);
    }

    /**
     * 正常周期只消费进入本阶段时已有的任务；后续入队不能推迟世界更新。
     * 单消费者且失效任务也占用批次数量，因此无需复制队列或改变原序。
     */
    public int drainWorldTick(int serverTick) {
        int pendingTasks;
        synchronized (lock) {
            pendingTasks = queue.size();
        }
        return drain(false, serverTick, Long.MAX_VALUE, pendingTasks);
    }

    public int drain(boolean betweenTicks, int serverTick, long deadlineNanos) {
        return drain(betweenTicks, serverTick, deadlineNanos, Integer.MAX_VALUE);
    }

    private int drain(boolean betweenTicks, int serverTick, long deadlineNanos, int remainingTasks) {
        requireConsumerThread();
        if (consuming) {
            return 0;
        }
        consuming = true;
        int count = 0;
        try {
            long dequeuedNanos;
            while (remainingTasks-- > 0 && (dequeuedNanos = clock.getAsLong()) < deadlineNanos) {
                Entry entry = poll(betweenTicks, dequeuedNanos);
                if (entry == null) {
                    break;
                }
                if (!entry.session.active || !entry.task.isValid()) {
                    synchronized (lock) {
                        discardedTasks++;
                    }
                    continue;
                }
                long executionNanos = clock.getAsLong();
                currentContext = new InboundContext(entry.session.id, entry.sequence, entry.sessionSequence, entry.enqueuedNanos,
                        serverTick, betweenTicks, entry.receivedNanos);
                try {
                    entry.task.run(currentContext);
                } catch (RuntimeException exception) {
                    failureHandler.accept(exception);
                } finally {
                    currentContext = null;
                    synchronized (lock) {
                        executedTasks++;
                        recordExecution(entry, betweenTicks, executionNanos);
                        if (clock.getAsLong() > deadlineNanos) {
                            overBudgetTasks++;
                        }
                    }
                }
                count++;
            }
        } finally {
            currentContext = null;
            consuming = false;
        }
        return count;
    }

    /** 整段等待共用一个预算与截止；空闲时间不消耗处理预算。 */
    public void awaitUntil(long tickDeadlineNanos, long processingBudgetNanos, int serverTick) {
        requireConsumerThread();
        if (consuming) {
            return;
        }
        long remainingBudget = Math.max(0, processingBudgetNanos);
        while (!closed) {
            long now = clock.getAsLong();
            if (now >= tickDeadlineNanos || Thread.currentThread().isInterrupted()) {
                return;
            }
            int count = 0;
            if (remainingBudget > 0) {
                long processingDeadline = now + Math.min(remainingBudget, tickDeadlineNanos - now);
                count = drain(true, serverTick, processingDeadline);
                remainingBudget = Math.max(0, remainingBudget - Math.max(0, clock.getAsLong() - now));
            }
            if (count == 0 || remainingBudget == 0) {
                long remaining = tickDeadlineNanos - clock.getAsLong();
                if (remaining > 0) {
                    LockSupport.parkNanos(this, remaining);
                }
            }
        }
    }

    @Nullable
    public InboundContext getCurrentContext() {
        requireConsumerThread();
        return currentContext;
    }

    /**
     * 诊断由插件初始化开启，关闭输入模式不会因此创建调度器。
     */
    public void enableBarrierDiagnostics() {
        synchronized (lock) {
            if (barrierMetrics == null) barrierMetrics = new InputBarrierMetrics(clock.getAsLong());
        }
    }

    /**
     * 供低频后台上报消费；只复制至多64类及两种溢出桶，不扫描队列或调用任务准入。
     */
    @Nullable
    public BarrierReport drainBarrierReport() {
        synchronized (lock) {
            return barrierMetrics == null ? null : barrierMetrics.drain(clock.getAsLong(), nextSequence, queue.size(), queuedBytes);
        }
    }

    public Snapshot snapshot() {
        synchronized (lock) {
            return new Snapshot(queue.size(), queuedBytes, executedTasks, discardedTasks,
                    rejectedTasks, betweenTickBarriers, overBudgetTasks, closed);
        }
    }

    /** 全局低频快照；活跃屏障时间也纳入，长卡顿不必等到队头放行才可见。 */
    public SchedulingSnapshot schedulingSnapshot() {
        synchronized (lock) {
            long sampledNanos = clock.getAsLong();
            long blockedNanos = observedBarrierNanos;
            if (activeBarrierSequence != 0) blockedNanos += Math.max(0, sampledNanos - activeBarrierSince);
            return new SchedulingSnapshot(sampledNanos, betweenTickExecutedTasks, executedTasks - betweenTickExecutedTasks,
                    candidateBetweenTickTasks, candidateWorldTickTasks, candidateTasksBehindBarrier,
                    candidateWorldTasksBehindBarrier, candidateWorldTasksAtOwnBarrier,
                    staticBarrierHeads, dynamicBarrierHeads, blockedNanos,
                    candidateQueueWait.snapshot(), candidateWorldBehindBarrierQueueWait.snapshot());
        }
    }

    public void close() {
        synchronized (lock) {
            closed = true;
            if (activeBarrierSequence != 0) endObservedBarrier(clock.getAsLong());
            Entry entry;
            while ((entry = queue.poll()) != null) {
                release(entry);
                discardedTasks++;
                entry.session.active = false;
            }
        }
        LockSupport.unpark(consumerThread);
    }

    @Nullable
    private Entry poll(boolean betweenTicks, long now) {
        synchronized (lock) {
            Entry entry = queue.peek();
            if (entry == null) {
                return null;
            }
            // 失效任务也逐条交回 drain 检查截止，避免断线积压绕过预算。
            if (entry.session.active && entry.task.isValid() && betweenTicks
                    && (!entry.betweenTicks || !entry.task.canRunBetweenTicks())) {
                betweenTickBarriers++;
                if (entry.sequence != lastBarrierSequence) {
                    lastBarrierHeadWasFollower = isObservedFollower(entry.sequence);
                    lastBarrierSequence = entry.sequence;
                    if (entry.betweenTicks) dynamicBarrierHeads++;
                    else staticBarrierHeads++;
                }
                if (activeBarrierSequence == 0) {
                    activeBarrierSequence = entry.sequence;
                    activeBarrierSince = now;
                    if (barrierMetrics != null) {
                        barrierMetrics.begin(entry.task.diagnosticType(), entry.betweenTicks,
                                entry.sequence, nextSequence, queue.size() - 1, now);
                    }
                }
                barrierAffectedThrough = nextSequence;
                return null;
            }
            endObservedBarrier(now);
            queue.remove();
            release(entry);
            return entry;
        }
    }

    /** 调用处已持有队列锁；该区间只声明观察到排在屏障后，不推断反事实可执行性。 */
    private boolean isObservedFollower(long sequence) {
        return sequence > lastBarrierSequence && sequence <= barrierAffectedThrough
                || sequence == lastBarrierSequence && lastBarrierHeadWasFollower;
    }

    private void endObservedBarrier(long now) {
        if (activeBarrierSequence == 0) return;
        observedBarrierNanos += Math.max(0, now - activeBarrierSince);
        if (barrierMetrics != null) barrierMetrics.end(now, nextSequence);
        activeBarrierSequence = 0;
    }

    private void recordExecution(Entry entry, boolean betweenTicks, long executionNanos) {
        if (betweenTicks) betweenTickExecutedTasks++;
        if (!entry.betweenTicks) return;
        long waitNanos = Math.max(0, executionNanos - entry.enqueuedNanos);
        candidateQueueWait.record(waitNanos);
        if (betweenTicks) candidateBetweenTickTasks++;
        else candidateWorldTickTasks++;
        if (isObservedFollower(entry.sequence)) {
            candidateTasksBehindBarrier++;
            if (!betweenTicks) {
                candidateWorldTasksBehindBarrier++;
                candidateWorldBehindBarrierQueueWait.record(waitNanos);
            }
        }
        if (!betweenTicks && entry.sequence == lastBarrierSequence) candidateWorldTasksAtOwnBarrier++;
    }

    private static final class QueueWaitCounter {
        private long count;
        private long totalNanos;
        private final long[] buckets = new long[8];

        private void record(long nanos) {
            count++;
            totalNanos += nanos;
            int bucket = nanos <= 1_000_000L ? 0 : nanos <= 5_000_000L ? 1 : nanos <= 10_000_000L ? 2
                    : nanos <= 25_000_000L ? 3 : nanos <= 50_000_000L ? 4 : nanos <= 100_000_000L ? 5
                    : nanos <= 250_000_000L ? 6 : 7;
            buckets[bucket]++;
        }

        private QueueWaitSnapshot snapshot() {
            long b1 = buckets[0];
            long b5 = b1 + buckets[1];
            long b10 = b5 + buckets[2];
            long b25 = b10 + buckets[3];
            long b50 = b25 + buckets[4];
            long b100 = b50 + buckets[5];
            long b250 = b100 + buckets[6];
            return new QueueWaitSnapshot(count, totalNanos, b1, b5, b10, b25, b50, b100, b250, buckets[7]);
        }
    }

    private void release(Entry entry) {
        queuedBytes -= entry.bytes;
        entry.session.queuedTasks--;
        entry.session.queuedBytes -= entry.bytes;
    }

    private void requireConsumerThread() {
        if (Thread.currentThread() != consumerThread) {
            throw new IllegalStateException("Network input may only be consumed on the server thread");
        }
    }
}
