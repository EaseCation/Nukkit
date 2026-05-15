package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;

import java.util.UUID;

/**
 * 为玩家实体皮肤发送流程提供按观察者解析的最终皮肤数据。
 */
public interface PlayerEntitySkinSource {

    UUID getPlayerEntitySkinUuid();

    long getPlayerEntitySkinEntityId();

    String getPlayerEntitySkinName(Player viewer);

    Skin getPlayerEntitySkin(Player viewer);

    default String getPlayerEntitySkinXboxUserId(Player viewer) {
        return "";
    }
}
