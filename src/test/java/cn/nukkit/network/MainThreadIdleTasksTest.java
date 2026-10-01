package cn.nukkit.network;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

class MainThreadIdleTasksTest {
    @Test
    void wakingRunsOnOwnerWithoutAdvancingDeadline() throws Exception {
        AtomicReference<MainThreadIdleTasks> queue = new AtomicReference<>();
        AtomicReference<Thread> executionThread = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        AtomicLong deadline = new AtomicLong();
        AtomicLong returned = new AtomicLong();
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch handled = new CountDownLatch(1);
        Thread owner = new Thread(() -> {
            try {
                deadline.set(System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(300));
                ready.countDown();
                queue.get().awaitUntil(deadline.get(), () -> true);
                returned.set(System.nanoTime());
            } catch (Throwable exception) {
                error.set(exception);
            }
        });
        queue.set(new MainThreadIdleTasks(owner));
        owner.start();
        try {
            assertTrue(ready.await(2, TimeUnit.SECONDS));
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(20));
            assertTrue(queue.get().offer(() -> {
                executionThread.set(Thread.currentThread());
                handled.countDown();
            }));
            assertTrue(handled.await(200, TimeUnit.MILLISECONDS));
        } finally {
            owner.join(2000);
        }
        assertFalse(owner.isAlive());
        assertNull(error.get());
        assertSame(owner, executionThread.get());
        assertTrue(returned.get() >= deadline.get());
    }

    @Test
    void workBudgetDefersRemainingTaskUntilNextWait() throws Exception {
        MainThreadIdleTasks queue = new MainThreadIdleTasks(Thread.currentThread());
        AtomicLong calls = new AtomicLong();
        assertTrue(queue.offer(() -> {
            calls.incrementAndGet();
            try {
                Thread.sleep(5);
            } catch (InterruptedException exception) {
                throw new IllegalStateException(exception);
            }
        }));
        assertTrue(queue.offer(calls::incrementAndGet));
        queue.awaitUntil(System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(30), () -> true);
        assertEquals(1, calls.get());
        queue.awaitUntil(System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(30), () -> true);
        assertEquals(2, calls.get());
    }

    @Test
    void fullQueueRejectsAndExpiredDeadlineDoesNotExecute() throws Exception {
        MainThreadIdleTasks queue = new MainThreadIdleTasks(Thread.currentThread());
        AtomicLong calls = new AtomicLong();
        for (int index = 0; index < 256; index++) assertTrue(queue.offer(calls::incrementAndGet));
        assertFalse(queue.offer(calls::incrementAndGet));
        queue.awaitUntil(System.nanoTime() - 1, () -> true);
        assertEquals(0, calls.get());
    }
}
