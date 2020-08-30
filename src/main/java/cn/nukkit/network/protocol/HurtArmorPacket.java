package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
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
