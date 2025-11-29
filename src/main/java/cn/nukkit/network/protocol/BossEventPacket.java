package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by CreeperFace on 30. 10. 2016.
 */
@ToString
public class BossEventPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

    /* S2C: Shows the bossbar to the player. */
    public static final int TYPE_SHOW = 0;
    /* C2S: Registers a player to a boss fight. */
    public static final int TYPE_REGISTER_PLAYER = 1;
    /* S2C: Removes the bossbar from the client. */
    public static final int TYPE_HIDE = 2;
    /* C2S: Unregisters a player from a boss fight. */
    public static final int TYPE_UNREGISTER_PLAYER = 3;
    /* S2C: Appears not to be implemented. Currently bar percentage only appears to change in response to the target entity's health. */
    public static final int TYPE_HEALTH_PERCENT = 4;
    /* S2C: Also appears to not be implemented. Title clientside sticks as the target entity's nametag, or their entity type name if not set. */
    public static final int TYPE_TITLE = 5;
    /* S2C: Not sure on this. Includes color and overlay fields, plus an unknown short. */
    public static final int TYPE_UPDATE_PROPERTIES = 6;
    /* S2C: Sets color and overlay of the bar. */
    public static final int TYPE_TEXTURE = 7;
    /**
     * @since 1.18.10
     */
    /* C2S: Client asking the server to resend all boss data. */
    public static final int TYPE_QUERY = 8;

    public long bossEid;
    public int type;
    public long playerEid;
    public float healthPercent;
    public String title = "";
    public int darkenScreen;
    public BossBarColor color = BossBarColor.PINK;
    public int overlay;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.bossEid = this.getEntityUniqueId();
        this.type = (int) this.getUnsignedVarInt();
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                this.playerEid = this.getEntityUniqueId();
                break;
            case TYPE_SHOW:
                this.title = this.getString();
                this.healthPercent = this.getLFloat();
            case TYPE_UPDATE_PROPERTIES:
                this.darkenScreen = this.getLShort();
            case TYPE_TEXTURE:
                this.color = this.getBossBarColor();
                this.overlay = (int) this.getUnsignedVarInt();
                break;
            case TYPE_HEALTH_PERCENT:
                this.healthPercent = this.getLFloat();
                break;
            case TYPE_TITLE:
                this.title = this.getString();
                break;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.bossEid);
        this.putUnsignedVarInt(this.type);
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                this.putEntityUniqueId(this.playerEid);
                break;
            case TYPE_SHOW:
                this.putString(this.title);
                this.putLFloat(this.healthPercent);
            case TYPE_UPDATE_PROPERTIES:
                this.putLShort(this.darkenScreen);
            case TYPE_TEXTURE:
                this.putBossBarColor(this.color);
                this.putUnsignedVarInt(this.overlay);
                break;
            case TYPE_HEALTH_PERCENT:
                this.putLFloat(this.healthPercent);
                break;
            case TYPE_TITLE:
                this.putString(this.title);
                break;
        }
    }

    public enum BossBarColor {
        PINK(0),
        BLUE(1),
        RED(2),
        GREEN(3),
        YELLOW(4),
        PURPLE(5),
        /**
         * @since 1.20.40
         */
        REBECCA_PURPLE(5),
        WHITE(6),
        ;

        static final BossBarColor[] VALUES_OLD = new BossBarColor[]{PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE};
        static final BossBarColor[] VALUES = values();

        private final int oldId;

        BossBarColor(int oldId) {
            this.oldId = oldId;
        }

        public int getOldId() {
            return oldId;
        }

        public static BossBarColor fromOldId(int oldId) {
            return VALUES_OLD[oldId];
        }

        public static BossBarColor[] getValues() {
            return VALUES;
        }
    }
}
