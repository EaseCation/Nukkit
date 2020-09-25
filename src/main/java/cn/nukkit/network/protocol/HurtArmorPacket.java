package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class HurtArmorPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.HURT_ARMOR_PACKET;

    public int health;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.health);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

}
