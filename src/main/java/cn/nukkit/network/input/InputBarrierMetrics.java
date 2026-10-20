package cn.nukkit.network.input;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 队列锁内维护的有界阻塞分类；只观察原调度判定，不重跑准入、不保留包或玩家引用。
 */
final class InputBarrierMetrics {
    static final int MAX_TYPES = 64;
    private final Map<String, Counters> staticTypes = new HashMap<>();
    private final Map<String, Counters> dynamicTypes = new HashMap<>();
    private final Counters staticOverflow = new Counters("OTHER", false);
    private final Counters dynamicOverflow = new Counters("OTHER", true);
    @Nullable private Counters active;
    private long windowStartedNanos;
    private long windowSequence;
    private long accountedNanos;
    private long activeSinceNanos;
    private long activeSequence;
    private int activeFollowers;

    InputBarrierMetrics(long now) {
        windowStartedNanos = now;
    }

    void begin(String type, boolean dynamic, long sequence, int followers, long now) {
        Map<String, Counters> types = dynamic ? dynamicTypes : staticTypes;
        Counters counters = types.get(type);
        if (counters == null) {
            if (staticTypes.size() + dynamicTypes.size() >= MAX_TYPES || type.length() > 128) {
                counters = dynamic ? dynamicOverflow : staticOverflow;
            } else {
                counters = new Counters(type, dynamic);
                types.put(type, counters);
            }
        }
        active = counters;
        activeSequence = sequence;
        activeSinceNanos = accountedNanos = now;
        activeFollowers = followers;
        counters.heads++;
        counters.followersAtStart += followers;
        counters.maxFollowers = Math.max(counters.maxFollowers, followers);
    }

    void arrived(int followers) {
        if (active == null) return;
        active.arrivals++;
        activeFollowers = followers;
        active.maxFollowers = Math.max(active.maxFollowers, followers);
    }

    void end(long now) {
        account(now);
        active = null;
    }

    private void account(long now) {
        if (active == null) return;
        active.observedNanos += Math.max(0, now - accountedNanos);
        active.maxObservedHeadNanos = Math.max(active.maxObservedHeadNanos, Math.max(0, now - activeSinceNanos));
        active.maxFollowers = Math.max(active.maxFollowers, activeFollowers);
        accountedNanos = now;
    }

    ServerInputDispatcher.BarrierReport drain(long now, int queuedTasks, long queuedBytes) {
        account(now);
        List<ServerInputDispatcher.BarrierTypeSummary> summaries = new ArrayList<>();
        staticTypes.values().forEach(counters -> counters.drainTo(summaries));
        dynamicTypes.values().forEach(counters -> counters.drainTo(summaries));
        staticOverflow.drainTo(summaries);
        dynamicOverflow.drainTo(summaries);
        ServerInputDispatcher.BarrierReport report = new ServerInputDispatcher.BarrierReport(
                ++windowSequence, windowStartedNanos, now, queuedTasks, queuedBytes,
                active == null ? null : active.type, active != null && active.dynamic,
                active == null ? 0 : activeSequence, active == null ? 0 : activeFollowers,
                List.copyOf(summaries));
        windowStartedNanos = now;
        return report;
    }

    private static final class Counters {
        private final String type;
        private final boolean dynamic;
        private long heads;
        private long followersAtStart;
        private long arrivals;
        private long observedNanos;
        private long maxObservedHeadNanos;
        private int maxFollowers;

        private Counters(String type, boolean dynamic) {
            this.type = type;
            this.dynamic = dynamic;
        }

        private void drainTo(List<ServerInputDispatcher.BarrierTypeSummary> target) {
            if (heads != 0 || arrivals != 0 || observedNanos != 0) {
                target.add(new ServerInputDispatcher.BarrierTypeSummary(type, dynamic, heads,
                        followersAtStart, arrivals, maxFollowers, observedNanos, maxObservedHeadNanos));
            }
            heads = followersAtStart = arrivals = observedNanos = maxObservedHeadNanos = 0;
            maxFollowers = 0;
        }
    }
}
