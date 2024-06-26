package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-15.
 */
@ToString
public class InteractPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_NONE = 0;
    public static final int ACTION_INTERACT = 1;
    public static final int ACTION_DAMAGE = 2;
    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;
    public static final int ACTION_OPEN_NPC = 5;
    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;

    public float x;
    public float y;
    public float z;

    @Override
    public void decode() {
        this.action = this.getByte();
        this.target = this.getEntityRuntimeId();

        if (this.action == ACTION_MOUSEOVER) {
            this.x = this.getLFloat();
            this.y = this.getLFloat();
            this.z = this.getLFloat();
        }
    }

    @Override
    public void encode() {
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
