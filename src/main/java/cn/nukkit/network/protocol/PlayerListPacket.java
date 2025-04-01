package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerListPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putUnsignedVarInt(this.entries.length);
        for (Entry entry : this.entries) {
            if (type == TYPE_ADD) {
                this.putUUID(entry.uuid);
                this.putVarLong(entry.entityId);
                this.putString(entry.name);
                this.putSkinLegacy(entry.skin);
                this.putString(entry.xboxUserId);
            } else {
                this.putUUID(entry.uuid);
            }
        }

    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @ToString
    public static class Entry {

        public final UUID uuid;
        public long entityId;
        public String name = "";
        public String xboxUserId = "";
        public String platformChatId = "";
        public int buildPlatform = -1;
        public Skin skin;
        public boolean isTeacher;
        public boolean isHost;
        public boolean isSubClient;
        public int locatorIconColor = 0xffededed;

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "");
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
        }

        public Entry copy() {
            return new Entry(uuid, entityId, name, skin, xboxUserId);
        }
    }

}
