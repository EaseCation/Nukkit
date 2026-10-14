package cn.nukkit.network.input;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ServerInputDispatcherTest {
    private static ServerInputDispatcher dispatcher(int tasks, long bytes, int sessionTasks,
                                                    long sessionBytes, AtomicLong clock) {
        return new ServerInputDispatcher(Thread.currentThread(), tasks, bytes, sessionTasks, sessionBytes,
                exception -> fail(exception), clock::get);
    }

    private static ServerInputDispatcher.Session session() {
        return new ServerInputDispatcher.Session(UUID.randomUUID());
    }

    @Test
    void retiredGameplayDoesNotBlockOrInvalidateItsPendingLogout() {
        AtomicLong clock = new AtomicLong();
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, clock);
        ServerInputDispatcher.Session connection = session();
        AtomicBoolean accepting = new AtomicBoolean(true);
        List<String> actions = new ArrayList<>();
        dispatcher.offer(connection, context -> { accepting.set(false); actions.add("transfer"); }, 1, true);
        dispatcher.offer(connection, new ServerInputTask() {
            @Override public boolean isValid() { return accepting.get(); }
            @Override public void run(InboundContext context) { fail("Retired gameplay executed"); }
        }, 1, false);
        dispatcher.offer(connection, context -> { actions.add("logout"); dispatcher.invalidate(connection); }, 1, false);
        assertEquals(1, dispatcher.drain(true, 7, 100));
        assertEquals(List.of("transfer"), actions);
        assertTrue(connection.isActive());
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertEquals(1, dispatcher.snapshot().queuedTasks());
        assertEquals(1, dispatcher.drain(false, 8, 100));
        assertEquals(List.of("transfer", "logout"), actions);
        assertFalse(connection.isActive());
        assertEquals(0, dispatcher.snapshot().queuedTasks());
    }

    @Test
    void validNormalPhaseGameplayStillFormsItsOriginalBarrier() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        List<String> actions = new ArrayList<>();
        dispatcher.offer(session(), context -> actions.add("gameplay"), 1, false);
        dispatcher.offer(session(), context -> actions.add("other-player"), 1, true);
        assertEquals(0, dispatcher.drain(true, 7, 100));
        assertEquals(0, dispatcher.snapshot().discardedTasks());
        assertEquals(2, dispatcher.drain(false, 8, 100));
        assertEquals(List.of("gameplay", "other-player"), actions);
    }

    @Test
    void transportAndEnqueueTimesSurviveASeparateProcessingDeadline() {
        AtomicLong clock = new AtomicLong(300);
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, clock);
        List<InboundContext> seen = new ArrayList<>();
        ServerInputDispatcher.Session session = session();
        dispatcher.offer(session, seen::add, 1, true, 100);
        clock.set(350);
        dispatcher.offer(session, seen::add, 1, true, 100);
        clock.set(600);
        assertEquals(2, dispatcher.drain(true, 7, 1000));
        assertEquals(100, seen.get(0).receivedNanos());
        assertEquals(100, seen.get(1).receivedNanos());
        assertEquals(300, seen.get(0).enqueuedNanos());
        assertEquals(350, seen.get(1).enqueuedNanos());
        assertEquals(7, seen.get(0).serverTick());
        assertNotEquals(seen.get(0).sessionSequence(), seen.get(1).sessionSequence());
        assertNull(dispatcher.getCurrentContext());
    }

    @Test
    void idleBarrierDoesNotLetAnotherPlayerOvertake() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        List<Long> executed = new ArrayList<>();
        dispatcher.offer(session(), context -> executed.add(context.sequence()), 1, false);
        dispatcher.offer(session(), context -> executed.add(context.sequence()), 1, true);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        assertEquals(2, dispatcher.snapshot().queuedTasks());
        assertEquals(2, dispatcher.drain(false, 8, 10));
        assertEquals(List.of(1L, 2L), executed);
        assertEquals(0, dispatcher.snapshot().queuedBytes());
    }

    @Test
    void eachConnectionHasItsOwnSequenceInsideTheGlobalFifo() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        ServerInputDispatcher.Session first = session();
        ServerInputDispatcher.Session second = session();
        List<List<Long>> sequences = new ArrayList<>();
        ServerInputTask task = context -> sequences.add(List.of(context.sequence(), context.sessionSequence()));
        dispatcher.offer(first, task, 1, false);
        dispatcher.offer(second, task, 1, false);
        dispatcher.offer(first, task, 1, false);
        assertEquals(3, dispatcher.drain(false, 7, 10));
        assertEquals(List.of(List.of(1L, 1L), List.of(2L, 1L), List.of(3L, 2L)), sequences);
    }

    @Test
    void stateChangeBeforeConsumptionKeepsTheWholeFifoBehindItsHead() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        AtomicBoolean ready = new AtomicBoolean(true);
        List<Long> executed = new ArrayList<>();
        dispatcher.offer(session(), new ServerInputTask() {
            @Override
            public boolean canRunBetweenTicks() {
                assertSame(Thread.currentThread(), dispatcherConsumer);
                return ready.get();
            }

            private final Thread dispatcherConsumer = Thread.currentThread();

            @Override
            public void run(InboundContext context) {
                executed.add(context.sequence());
            }
        }, 1, true);
        dispatcher.offer(session(), context -> executed.add(context.sequence()), 1, true);
        ready.set(false);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        assertEquals(2, dispatcher.snapshot().queuedTasks());
        assertEquals(2, dispatcher.drain(false, 8, 10));
        assertEquals(List.of(1L, 2L), executed);
    }

    @Test
    void restoredEligibilityExecutesWithoutAdvancingTheWorldTick() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        AtomicBoolean ready = new AtomicBoolean(false);
        List<Long> executed = new ArrayList<>();
        dispatcher.offer(session(), new ServerInputTask() {
            @Override
            public boolean canRunBetweenTicks() {
                return ready.get();
            }

            @Override
            public void run(InboundContext context) {
                assertEquals(7, context.serverTick());
                assertTrue(context.betweenTicks());
                executed.add(context.sequence());
            }
        }, 1, true);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        ready.set(true);
        assertEquals(1, dispatcher.drain(true, 7, 10));
        assertEquals(List.of(1L), executed);
    }

    @Test
    void invalidatedTasksDoNotConsultTheirOldPlayerState() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        ServerInputDispatcher.Session stale = session();
        dispatcher.offer(stale, new ServerInputTask() {
            @Override
            public boolean canRunBetweenTicks() {
                fail("Stale eligibility was evaluated");
                return false;
            }

            @Override
            public void run(InboundContext context) {
                fail("Stale task ran");
            }
        }, 1, true);
        dispatcher.invalidate(stale);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertEquals(0, dispatcher.snapshot().queuedTasks());
    }

    @Test
    void quittingGenerationDiscardsItsStaticBarrierWithoutBlockingTheNewGenerationWithTheSameId() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        UUID id = UUID.randomUUID();
        ServerInputDispatcher.Session oldSession = new ServerInputDispatcher.Session(id);
        ServerInputDispatcher.Session newSession = new ServerInputDispatcher.Session(id);
        dispatcher.offer(oldSession, context -> dispatcher.invalidate(oldSession), 1, true);
        dispatcher.offer(oldSession, context -> fail("Closed generation work ran"), 1, false);
        dispatcher.offer(newSession, context -> {
            assertTrue(context.betweenTicks());
            assertEquals(3, context.sequence());
            assertEquals(1, context.sessionSequence());
            assertEquals(id, context.sessionId());
        }, 1, true);
        assertEquals(2, dispatcher.drain(true, 7, 10));
        assertFalse(oldSession.isActive());
        assertTrue(newSession.isActive());
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertEquals(0, dispatcher.snapshot().betweenTickBarriers());
        assertEquals(0, dispatcher.snapshot().queuedTasks());
        assertNull(dispatcher.getCurrentContext());
    }

    @Test
    void staticBarriersAndTheRegularStageNeverConsultDynamicEligibility() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        ServerInputTask task = new ServerInputTask() {
            @Override
            public boolean canRunBetweenTicks() {
                fail("Eligibility was evaluated outside the candidate idle path");
                return false;
            }

            @Override
            public void run(InboundContext context) {
                assertFalse(context.betweenTicks());
            }
        };
        dispatcher.offer(session(), task, 1, false);
        dispatcher.offer(session(), task, 1, true);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        assertEquals(2, dispatcher.drain(false, 8, 10));
    }

    @Test
    void deadlineEqualityNeverStartsAHandler() {
        AtomicLong clock = new AtomicLong(10);
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, clock);
        dispatcher.offer(session(), context -> fail("A handler started at its deadline"), 1, true);
        assertEquals(0, dispatcher.drain(true, 7, 10));
        assertEquals(1, dispatcher.snapshot().queuedTasks());
    }

    @Test
    void longHandlerFinishesOnceAndStopsTheNextTask() {
        AtomicLong clock = new AtomicLong();
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, clock);
        dispatcher.offer(session(), context -> clock.set(11), 1, true);
        dispatcher.offer(session(), context -> fail("Budget was ignored"), 1, true);
        assertEquals(1, dispatcher.drain(true, 7, 10));
        assertEquals(1, dispatcher.snapshot().queuedTasks());
        assertEquals(1, dispatcher.snapshot().overBudgetTasks());
    }

    @Test
    void nestedDrainDoesNotReenterOrLoseTheCurrentContext() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        List<Long> executed = new ArrayList<>();
        ServerInputDispatcher.Session session = session();
        dispatcher.offer(session, context -> {
            dispatcher.offer(session, next -> executed.add(next.sequence()), 1, true);
            assertEquals(0, dispatcher.drain(true, 7, 10));
            assertSame(context, dispatcher.getCurrentContext());
            executed.add(context.sequence());
        }, 1, true);
        assertEquals(2, dispatcher.drain(false, 7, 10));
        assertEquals(List.of(1L, 2L), executed);
        assertNull(dispatcher.getCurrentContext());
    }

    @Test
    void invalidatedOldConnectionCannotWriteIntoTheNewConnection() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        UUID id = UUID.randomUUID();
        ServerInputDispatcher.Session oldSession = new ServerInputDispatcher.Session(id);
        ServerInputDispatcher.Session newSession = new ServerInputDispatcher.Session(id);
        dispatcher.offer(oldSession, context -> fail("Stale session was executed"), 4, false);
        dispatcher.invalidate(oldSession);
        dispatcher.offer(newSession, context -> assertEquals(id, context.sessionId()), 3, true);
        assertEquals(1, dispatcher.drain(true, 7, 10));
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertEquals(0, dispatcher.snapshot().queuedBytes());
        assertTrue(newSession.isActive());
    }

    @Test
    void discardingInvalidatedBacklogStillChecksTheDeadlinePerEntry() {
        AtomicLong clock = new AtomicLong();
        ServerInputDispatcher dispatcher = new ServerInputDispatcher(Thread.currentThread(), 8, 100, 8, 100,
                exception -> fail(exception), clock::getAndIncrement);
        ServerInputDispatcher.Session stale = session();
        for (int index = 0; index < 4; index++) {
            dispatcher.offer(stale, context -> fail("Stale backlog was executed"), 1, false);
        }
        dispatcher.invalidate(stale);
        clock.set(0);
        assertEquals(0, dispatcher.drain(true, 7, 2));
        assertEquals(2, dispatcher.snapshot().discardedTasks());
        assertEquals(2, dispatcher.snapshot().queuedTasks());
        assertEquals(2, dispatcher.snapshot().queuedBytes());
        clock.set(0);
        assertEquals(0, dispatcher.drain(true, 8, 10));
        assertEquals(4, dispatcher.snapshot().discardedTasks());
        assertEquals(0, dispatcher.snapshot().queuedTasks());
    }

    @Test
    void overloadInvalidatesOnlyItsSessionWithoutFallback() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 2, 5, new AtomicLong());
        ServerInputDispatcher.Session noisy = session();
        ServerInputDispatcher.Session other = session();
        assertEquals(ServerInputDispatcher.OfferResult.ACCEPTED,
                dispatcher.offer(noisy, context -> fail("Overloaded session survived"), 5, true));
        assertEquals(ServerInputDispatcher.OfferResult.OVERLOADED,
                dispatcher.offer(noisy, context -> fail("Rejected task ran"), 1, true));
        assertEquals(ServerInputDispatcher.OfferResult.CLOSED,
                dispatcher.offer(noisy, context -> fail("Closed session ran"), 0, true));
        assertEquals(ServerInputDispatcher.OfferResult.ACCEPTED,
                dispatcher.offer(other, context -> assertTrue(other.isActive()), 1, true));
        assertEquals(1, dispatcher.drain(false, 7, 10));
        assertEquals(0, dispatcher.snapshot().queuedBytes());
        assertEquals(2, dispatcher.snapshot().rejectedTasks());
    }

    @Test
    void globalTaskAndByteLimitsAreExact() {
        ServerInputDispatcher dispatcher = dispatcher(2, 5, 3, 10, new AtomicLong());
        assertEquals(ServerInputDispatcher.OfferResult.ACCEPTED, dispatcher.offer(session(), context -> { }, 3, true));
        assertEquals(ServerInputDispatcher.OfferResult.ACCEPTED, dispatcher.offer(session(), context -> { }, 2, true));
        assertEquals(ServerInputDispatcher.OfferResult.OVERLOADED, dispatcher.offer(session(), context -> fail(), 0, true));
        assertEquals(2, dispatcher.drain(false, 7, 10));
        assertEquals(ServerInputDispatcher.OfferResult.OVERLOADED, dispatcher.offer(session(), context -> fail(), 6, true));
    }

    @Test
    void exceptionDoesNotReplayTheTaskOrLeakItsContext() {
        List<RuntimeException> failures = new ArrayList<>();
        AtomicLong clock = new AtomicLong();
        ServerInputDispatcher dispatcher = new ServerInputDispatcher(Thread.currentThread(), 8, 100, 8, 100,
                failures::add, clock::get);
        dispatcher.offer(session(), context -> { throw new IllegalStateException("Expected failure"); }, 1, true);
        dispatcher.offer(session(), context -> assertSame(context, dispatcher.getCurrentContext()), 1, true);
        assertEquals(2, dispatcher.drain(false, 7, 10));
        assertEquals(1, failures.size());
        assertNull(dispatcher.getCurrentContext());
        assertEquals(2, dispatcher.snapshot().executedTasks());
    }

    @Test
    void closeDiscardsPendingWorkAndRejectsNewOffers() {
        ServerInputDispatcher dispatcher = dispatcher(8, 100, 8, 100, new AtomicLong());
        ServerInputDispatcher.Session session = session();
        dispatcher.offer(session, context -> fail(), 10, true);
        dispatcher.close();
        assertEquals(0, dispatcher.snapshot().queuedTasks());
        assertEquals(0, dispatcher.snapshot().queuedBytes());
        assertEquals(1, dispatcher.snapshot().discardedTasks());
        assertFalse(session.isActive());
        assertEquals(ServerInputDispatcher.OfferResult.CLOSED, dispatcher.offer(session(), context -> fail(), 1, true));
    }

    @Test
    void wakingTheConsumerDoesNotReturnBeforeTheFixedTickDeadline() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch ran = new CountDownLatch(1);
        AtomicReference<ServerInputDispatcher> reference = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicLong completedAt = new AtomicLong();
        AtomicLong deadline = new AtomicLong();
        Thread consumer = new Thread(() -> {
            try {
                ServerInputDispatcher dispatcher = new ServerInputDispatcher(Thread.currentThread(), 8, 100, 8, 100,
                        exception -> failure.set(exception));
                reference.set(dispatcher);
                deadline.set(System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(150));
                ready.countDown();
                dispatcher.awaitUntil(deadline.get(), TimeUnit.MILLISECONDS.toNanos(20), 7);
                completedAt.set(System.nanoTime());
            } catch (Throwable exception) {
                failure.set(exception);
                ready.countDown();
            }
        });
        consumer.start();
        assertTrue(ready.await(2, TimeUnit.SECONDS));
        ServerInputDispatcher dispatcher = reference.get();
        try {
            assertEquals(ServerInputDispatcher.OfferResult.ACCEPTED,
                    dispatcher.offer(session(), context -> {
                        assertTrue(context.betweenTicks());
                        assertEquals(7, context.serverTick());
                        ran.countDown();
                    }, 1, true));
            assertTrue(ran.await(2, TimeUnit.SECONDS));
            consumer.join(2000);
            assertFalse(consumer.isAlive());
            assertNull(failure.get());
            assertTrue(completedAt.get() >= deadline.get());
        } finally {
            dispatcher.close();
            consumer.join(2000);
        }
    }
}
