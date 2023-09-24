package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

import java.util.UUID;

/**
 * 发送玩家列表数据事件
 * 但是需要注意，这里面的player是指接收到的玩家，而不是皮肤和uuid对应的玩家！
 */
public class SendPlayerListDataEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Getter
    private final UUID uuid;

    @Getter
    private final long entityId;

    @Getter
    private String name;

    @Getter
    private Skin skin;

    @Getter
    private String xboxUserId;

    @Getter
    private boolean dirty = false;

    /**
     * 发送玩家列表数据事件
     * @param player 需要注意，这里面的player是指接收到的玩家，而不是皮肤和uuid对应的玩家！
     * @param uuid UUID
     * @param entityId 实体ID
     * @param name 名字
     * @param skin 皮肤
     * @param xboxUserId Xbox用户ID
     */
    public SendPlayerListDataEvent(Player player, UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.name = name;
        this.xboxUserId = xboxUserId;
        this.player = player;
        this.skin = skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        this.dirty = true;
    }

    public void setName(String name) {
        this.name = name;
        this.dirty = true;
    }

    public void setXboxUserId(String xboxUserId) {
        this.xboxUserId = xboxUserId;
        this.dirty = true;
    }

}
