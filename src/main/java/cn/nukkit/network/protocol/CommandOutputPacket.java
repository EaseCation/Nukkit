package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import lombok.ToString;

@ToString
public class CommandOutputPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.COMMAND_OUTPUT_PACKET;

    public static final int TYPE_LAST = 1;
    public static final int TYPE_SILENT = 2;
    public static final int TYPE_ALL = 3;
    public static final int TYPE_DATA_SET = 4;

    public CommandOriginData originData;
    public int outputType;
    public int successCount;
    public CommandOutputMessage[] messages;
    public String data;

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

        this.putUnsignedVarInt(originData.type.ordinal());
        this.putUUID(originData.uuid);
        this.putString(originData.requestId);
        if (originData.type == CommandOriginData.Origin.DEV_CONSOLE || originData.type == CommandOriginData.Origin.TEST) {
            this.putEntityUniqueId(originData.playerEntityUniqueId);
        }

        this.putByte((byte) this.outputType);
        this.putUnsignedVarInt(this.successCount);

        this.putUnsignedVarInt(this.messages.length);
        for (CommandOutputMessage message : this.messages) {
            this.putBoolean(message.successful);
            this.putString(message.messageId);

            this.putUnsignedVarInt(message.parameters.length);
            for (String parameter : message.parameters) {
                this.putString(parameter);
            }
        }

        if (outputType == TYPE_DATA_SET) {
            this.putString(this.data);
        }
    }
}
