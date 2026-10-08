package cn.nukkit.network.input;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Objects;
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

    public int drain(boolean betweenTicks, int serverTick, long deadlineNanos) {
        requireConsumerThread();
        if (consuming) {
            return 0;
        }
        consuming = true;
        int count = 0;
        try {
            while (clock.getAsLong() < deadlineNanos) {
                Entry entry = poll(betweenTicks);
                if (entry == null) {
                    break;
                }
                if (!entry.session.active || !entry.task.isValid()) {
                    synchronized (lock) {
                        discardedTasks++;
                    }
                    continue;
                }
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

    public Snapshot snapshot() {
        synchronized (lock) {
            return new Snapshot(queue.size(), queuedBytes, executedTasks, discardedTasks,
                    rejectedTasks, betweenTickBarriers, overBudgetTasks, closed);
        }
    }

    public void close() {
        synchronized (lock) {
            closed = true;
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
    private Entry poll(boolean betweenTicks) {
        synchronized (lock) {
            Entry entry = queue.peek();
            if (entry == null) {
                return null;
            }
            // 失效任务也逐条交回 drain 检查截止，避免断线积压绕过预算。
            if (entry.session.active && entry.task.isValid() && betweenTicks
                    && (!entry.betweenTicks || !entry.task.canRunBetweenTicks())) {
                betweenTickBarriers++;
                return null;
            }
            queue.remove();
            release(entry);
            return entry;
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
