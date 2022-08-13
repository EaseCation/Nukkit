package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.NPC_REQUEST_PACKET;

    public static final int TYPE_SET_ACTION = 0;
    public static final int TYPE_EXECUTE_COMMAND_ACTION = 1;
    public static final int TYPE_EXECUTE_CLOSING_COMMANDS = 2;
    public static final int TYPE_SET_NAME = 3;
    public static final int TYPE_SET_SKIN = 4;
    public static final int TYPE_SET_INTERACTION_TEXT = 5;
    public static final int TYPE_EXECUTE_OPENING_COMMANDS = 6;

    public long entityRuntimeId;
    public int type;
    public String command;
    public int actionIndex;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        entityRuntimeId = getEntityRuntimeId();
        type = getByte();
        command = getString();
        actionIndex = getByte();
    }

    @Override
    public void encode() {
    }
}
