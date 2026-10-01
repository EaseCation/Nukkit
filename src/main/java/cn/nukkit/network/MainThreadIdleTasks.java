package cn.nukkit.network;

import lombok.extern.log4j.Log4j2;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

/** 实验用途：只在世界 tick 之间执行少量主线程网络任务。 */
@Log4j2
public final class MainThreadIdleTasks {
    private static final int MAX_TASKS_PER_WAIT = 64;
    private static final long MAX_WORK_NANOS = TimeUnit.MILLISECONDS.toNanos(1);
    private final Queue<Runnable> pending = new ArrayBlockingQueue<>(256);
    private final Thread owner;

    public MainThreadIdleTasks(Thread owner) {
        this.owner = owner;
    }

    public boolean offer(Runnable task) {
        if (!pending.offer(task)) {
            return false;
        }
        LockSupport.unpark(owner);
        return true;
    }

    /** 唤醒只消费任务，不推进 tick；截止时间到达后才返回外层循环。 */
    public void awaitUntil(long deadlineNanos, BooleanSupplier running) throws InterruptedException {
        if (Thread.currentThread() != owner) {
            throw new IllegalStateException("Idle tasks must run on the server main thread");
        }
        long workNanos = 0;
        int completed = 0;
        while (running.getAsBoolean()) {
            if (Thread.interrupted()) {
                throw new InterruptedException("Server main thread interrupted");
            }
            long remaining = deadlineNanos - System.nanoTime();
            if (remaining <= 0) {
                return;
            }
            Runnable task = completed < MAX_TASKS_PER_WAIT && workNanos < MAX_WORK_NANOS ? pending.poll() : null;
            if (task == null) {
                LockSupport.parkNanos(this, remaining);
                continue;
            }
            long started = System.nanoTime();
            try {
                task.run();
            } catch (RuntimeException exception) {
                log.error("Could not execute experimental main-thread network task", exception);
            } finally {
                workNanos += System.nanoTime() - started;
                completed++;
            }
        }
    }
}
