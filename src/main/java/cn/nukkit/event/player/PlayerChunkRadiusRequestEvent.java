package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 当客户端通过设置-视频主动请求改变视距时触发
 * 在实际应用设置之前触发，支持取消和修改最终视距
 */
public class PlayerChunkRadiusRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final int currentRadius;      // 当前视距
    private final int requestedRadius;    // 客户端请求的视距
    private int radius;                   // 最终应用的视距（可由插件修改）

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerChunkRadiusRequestEvent(Player player, int currentRadius, int requestedRadius) {
        this.player = player;
        this.currentRadius = currentRadius;
        this.requestedRadius = requestedRadius;
        this.radius = requestedRadius;    // 默认使用请求的值
    }

    /**
     * 获取当前视距（变更前）
     */
    public int getCurrentRadius() {
        return currentRadius;
    }

    /**
     * 获取客户端请求的视距
     */
    public int getRequestedRadius() {
        return requestedRadius;
    }

    /**
     * 获取最终应用的视距
     */
    public int getRadius() {
        return radius;
    }

    /**
     * 设置最终应用的视距（由插件控制）
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }
}
