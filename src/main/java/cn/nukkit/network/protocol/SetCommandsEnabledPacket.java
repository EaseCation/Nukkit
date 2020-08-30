package cn.nukkit.network.protocol;

public class SetCommandsEnabledPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_COMMANDS_ENABLED_PACKET;

    public boolean enabled;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.enabled);
    }
}
