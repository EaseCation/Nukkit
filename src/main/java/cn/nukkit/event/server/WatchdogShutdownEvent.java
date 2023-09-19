package cn.nukkit.event.server;

import cn.nukkit.event.AsyncEvent;
import cn.nukkit.event.HandlerList;

public class WatchdogShutdownEvent extends ServerEvent implements AsyncEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
