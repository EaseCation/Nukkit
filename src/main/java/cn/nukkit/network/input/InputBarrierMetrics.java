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
    private final Map<String, Map<String, Counters>> staticTypes = new HashMap<>();
    private final Map<String, Map<String, Counters>> dynamicTypes = new HashMap<>();
    private final Counters staticOverflow = new Counters("OTHER", false, "OTHER");
    private final Counters dynamicOverflow = new Counters("OTHER", true, "OTHER");
    private int typeCount;
    @Nullable private Counters active;
    private long windowStartedNanos;
    private long windowSequence;
    private long accountedNanos;
    private long activeSinceNanos;
    private long activeSequence;
    private long accountedSequence;
    private int activeFollowers;

    InputBarrierMetrics(long now) {
        windowStartedNanos = now;
    }

    void begin(String type, boolean dynamic, String reason, long sequence, long tailSequence, int followers, long now) {
        Map<String, Map<String, Counters>> types = dynamic ? dynamicTypes : staticTypes;
        Map<String, Counters> reasons = types.get(type);
        Counters counters = reasons == null ? null : reasons.get(reason);
        if (counters == null) {
            if (typeCount >= MAX_TYPES || type.length() > 128 || reason.length() > 64) {
                counters = dynamic ? dynamicOverflow : staticOverflow;
            } else {
                if (reasons == null) {
                    reasons = new HashMap<>();
                    types.put(type, reasons);
                }
                counters = new Counters(type, dynamic, reason);
                reasons.put(reason, counters);
                typeCount++;
            }
        }
        active = counters;
        activeSequence = sequence;
        accountedSequence = tailSequence;
        activeSinceNanos = accountedNanos = now;
        activeFollowers = followers;
        counters.heads++;
        counters.followersAtStart += followers;
        counters.maxFollowers = Math.max(counters.maxFollowers, followers);
    }

    void end(long now, long tailSequence) {
        account(now, tailSequence);
        active = null;
    }

    private void account(long now, long tailSequence) {
        if (active == null) return;
        // 活跃队头尚未出队，后继只会增加；用已有序号差计数，避免在每次入队增加诊断工作。
        long arrivals = tailSequence - accountedSequence;
        active.arrivals += arrivals;
        activeFollowers += (int) arrivals;
        accountedSequence = tailSequence;
        active.observedNanos += Math.max(0, now - accountedNanos);
        active.maxObservedHeadNanos = Math.max(active.maxObservedHeadNanos, Math.max(0, now - activeSinceNanos));
        active.maxFollowers = Math.max(active.maxFollowers, activeFollowers);
        accountedNanos = now;
    }

    ServerInputDispatcher.BarrierReport drain(long now, long tailSequence, int queuedTasks, long queuedBytes) {
        account(now, tailSequence);
        List<ServerInputDispatcher.BarrierTypeSummary> summaries = new ArrayList<>();
        staticTypes.values().forEach(reasons -> reasons.values().forEach(counters -> counters.drainTo(summaries)));
        dynamicTypes.values().forEach(reasons -> reasons.values().forEach(counters -> counters.drainTo(summaries)));
        staticOverflow.drainTo(summaries);
        dynamicOverflow.drainTo(summaries);
        ServerInputDispatcher.BarrierReport report = new ServerInputDispatcher.BarrierReport(
                ++windowSequence, windowStartedNanos, now, queuedTasks, queuedBytes,
                active == null ? null : active.type, active != null && active.dynamic,
                active == null ? null : active.reason, active == null ? 0 : activeSequence, active == null ? 0 : activeFollowers,
                List.copyOf(summaries));
        windowStartedNanos = now;
        return report;
    }

    private static final class Counters {
        private final String type;
        private final boolean dynamic;
        private final String reason;
        private long heads;
        private long followersAtStart;
        private long arrivals;
        private long observedNanos;
        private long maxObservedHeadNanos;
        private int maxFollowers;

        private Counters(String type, boolean dynamic, String reason) {
            this.type = type;
            this.dynamic = dynamic;
            this.reason = reason;
        }

        private void drainTo(List<ServerInputDispatcher.BarrierTypeSummary> target) {
            if (heads != 0 || arrivals != 0 || observedNanos != 0) {
                target.add(new ServerInputDispatcher.BarrierTypeSummary(type, dynamic, reason, heads,
                        followersAtStart, arrivals, maxFollowers, observedNanos, maxObservedHeadNanos));
            }
            heads = followersAtStart = arrivals = observedNanos = maxObservedHeadNanos = 0;
            maxFollowers = 0;
        }
    }
}
