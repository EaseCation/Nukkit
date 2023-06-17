package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.ToString;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class CommandRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public String command;
    public CommandOriginData data;
    public boolean internal;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();

        CommandOriginData.Origin type = CommandOriginData.Origin.values0()[(int) this.getUnsignedVarInt()];
        UUID uuid = this.getUUID();
        String requestId = this.getString();
        long playerEntityUniqueId = 0;
        if (type == CommandOriginData.Origin.DEV_CONSOLE || type == CommandOriginData.Origin.TEST) {
            playerEntityUniqueId = this.getEntityUniqueId();
        }
        this.data = new CommandOriginData(type, uuid, requestId, playerEntityUniqueId);

        this.internal = this.getBoolean();
    }

    @Override
    public void encode() {
    }

}
