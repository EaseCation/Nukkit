package cn.nukkit.network.input;

import java.util.UUID;

public record InboundContext(UUID sessionId, long sequence, long sessionSequence, long enqueuedNanos, int serverTick,
                             boolean betweenTicks, long receivedNanos) {
    /** 未提供接入时间的内部任务保留原构造入口，零表示未知。 */
    public InboundContext(UUID sessionId, long sequence, long sessionSequence, long enqueuedNanos, int serverTick,
                          boolean betweenTicks) {
        this(sessionId, sequence, sessionSequence, enqueuedNanos, serverTick, betweenTicks, 0);
    }

}
