package cn.nukkit.observability;

import cn.nukkit.Player;
import java.util.Map;
import java.util.UUID;
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
    public static void emit(Player player, String type, Map<String, Object> data) {
        Observer current = observer;
        if (current == null) return;
        try { current.accept(player, type, data); }
        catch (RuntimeException exception) { LOG.error("Connection observer failed for {}", type, exception); }
    }
}
