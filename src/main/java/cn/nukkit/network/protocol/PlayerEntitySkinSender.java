package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * 统一玩家实体皮肤注册与刷新逻辑。
 */
public final class PlayerEntitySkinSender {

    private static final String EMPTY_SKIN_ID = "easecation.empty-player-entity-skin";
    private static final Skin EMPTY_PLAYER_LIST_SKIN = createEmptyPlayerListSkin();

    private PlayerEntitySkinSender() {
    }

    public static void sendInitialSkin(Player viewer, UUID uuid, long entityId, String name, Skin skin) {
        sendInitialSkin(viewer, uuid, entityId, name, skin, "");
    }

    public static void sendInitialSkin(Player viewer, UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(skin, "skin");

        if (uuid.equals(viewer.getUniqueId())) {
            sendPlayerListAdd(viewer, uuid, entityId, name, skin, xboxUserId);
            return;
        }

        sendPlayerListAdd(viewer, uuid, entityId, name, EMPTY_PLAYER_LIST_SKIN.clone(), xboxUserId);
        sendSkinUpdate(viewer, uuid, skin);
    }

    public static void sendSkinUpdate(Player viewer, UUID uuid, Skin skin) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(skin, "skin");

        PlayerSkinPacket pk = new PlayerSkinPacket();
        pk.uuid = uuid;
        pk.skin = skin;
        pk.newSkinName = skin.getSkinId();
        pk.oldSkinName = "";
        viewer.dataPacket(pk);
    }

    public static void sendRemove(Player viewer, UUID uuid) {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(uuid, "uuid");

        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        viewer.dataPacket(pk);
    }

    public static void sendRemove(Collection<? extends Player> viewers, UUID uuid) {
        for (Player viewer : viewers) {
            sendRemove(viewer, uuid);
        }
    }

    public static void sendRemove(Player[] viewers, UUID uuid) {
        for (Player viewer : viewers) {
            sendRemove(viewer, uuid);
        }
    }

    public static boolean isEmptyPlayerListSkin(Skin skin) {
        return skin != null && EMPTY_SKIN_ID.equals(skin.getSkinId());
    }

    public static Skin createEmptyPlayerListSkin() {
        Skin skin = new Skin()
                .setSkinId(EMPTY_SKIN_ID)
                .setSkinData(new byte[Skin.SINGLE_SKIN_SIZE])
                .setCapeData(new byte[0])
                .setGeometryName("geometry.humanoid.custom")
                .setPlayerSkin(false);
        if (!skin.isValid()) {
            throw new IllegalStateException("Empty player list skin must be valid");
        }
        return skin;
    }

    private static void sendPlayerListAdd(Player viewer, UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
        viewer.dataPacket(pk);
    }
}
