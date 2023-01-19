package cn.nukkit.network;

public enum PacketViolationReason {
    UNKNOWN("unknown error"),
    VIOLATION_OVER_THRESHOLD("violation over threshold"),
    RECEIVING_BATCHES_TOO_FAST("receiving batches too fast"),
    RECEIVING_PACKETS_TOO_FAST("receiving packets too fast"),
    TOO_MANY_PACKETS_IN_BATCH("too many packets in batch"),
    TOO_MANY_ARRAY_ELEMENTS_IN_PACKET("too many array elements in packet"),
    NESTED_BATCH("nested batch"),
    MALFORMED_PACKET("malformed packet"),
    CORRUPTED_DATA("corrupted data"),
    IMPOSSIBLE_BEHAVIOR("impossible behavior"),
    ALREADY_LOGGED_IN("already logged in"),
    ;

    public final String str;

    PacketViolationReason(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
