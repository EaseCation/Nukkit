package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleCrawlEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean crawling;

    public PlayerToggleCrawlEvent(Player player, boolean crawling) {
        this.player = player;
        this.crawling = crawling;
    }

    public boolean isCrawling() {
        return this.crawling;
    }
}
