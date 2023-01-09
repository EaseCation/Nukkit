package cn.nukkit.network.protocol;

import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class TextPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.TEXT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public static final byte TYPE_RAW = 0;
    public static final byte TYPE_CHAT = 1;
    public static final byte TYPE_TRANSLATION = 2;
    public static final byte TYPE_POPUP = 3;
    public static final byte TYPE_TIP = 4;
    public static final byte TYPE_SYSTEM = 5;
    public static final byte TYPE_WHISPER = 6;
    public static final byte TYPE_ANNOUNCEMENT = 7;

    public byte type;
    public boolean isLocalized = false;

    public String message = "";
    public String[] parameters = new String[0];
    public String primaryName = "";

    public String sendersXUID = "";
    public String platformIdString = "";

    @Override
    public void decode() {
        this.type = (byte) getByte();
        this.isLocalized = this.getBoolean();
        switch (type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.primaryName = this.getString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.message = this.getString();
                break;

            case TYPE_TRANSLATION:
                this.message = this.getString();
                this.parameters = this.getArray(String.class, BinaryStream::getString);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putBoolean(this.isLocalized);
        switch (this.type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.putString(this.primaryName);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.putString(this.message);
                break;

            case TYPE_TRANSLATION:
                this.putString(this.message);
                this.putUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
    }

}
