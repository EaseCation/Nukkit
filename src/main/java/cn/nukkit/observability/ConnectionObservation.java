package cn.nukkit.observability;

import cn.nukkit.Player;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 无业务依赖的观测入口，回调异常不能影响原有连接生命周期。
 */
public final class ConnectionObservation {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionObservation.class);
    public static final String BOOT_ID = UUID.randomUUID().toString();
    private static volatile Observer observer;
    private ConnectionObservation() { }
    @FunctionalInterface
    public interface Observer {
        void accept(Player player, String type, Map<String, Object> data);
    }
    public static void setObserver(Observer value) { observer = value; }
    public static String reasonCode(@Nullable String reason) {
        if (reason == null) return "unclassified_server_close";
        return switch (reason) {
            case "Timed out" -> "transport_timeout";
            case "Disconnected from Server" -> "transport_disconnected";
            case "Sent malformed packet" -> "malformed_packet";
            default -> {
                // 只保留固定协议键，不上传可能包含账号、凭据或地址的任意关闭文案。
                boolean token = reason.length() <= 160 && (reason.startsWith("disconnectionScreen.") || reason.startsWith("disconnect."));
                for (int index = 0; token && index < reason.length(); index++) {
                    char value = reason.charAt(index);
                    token = value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z' || value >= '0' && value <= '9' || value == '.' || value == '_' || value == '-';
                }
                yield token ? reason : "unclassified_server_close";
            }
        };
    }
    public static void emit(Player player, String type, Map<String, Object> data) {
        Observer current = observer;
        if (current == null) return;
        try { current.accept(player, type, data); }
        catch (RuntimeException exception) { LOG.error("Connection observer failed for {}", type, exception); }
    }
}
