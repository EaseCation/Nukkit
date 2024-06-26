package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class MovePlayerPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_RESPAWN = 1;
    public static final int MODE_TELEPORT = 2;
    public static final int MODE_ONLY_HEAD_ROT = 3; //facepalm Mojang

    public static final int TELEPORT_CAUSE_UNKNOWN = 0;
    public static final int TELEPORT_CAUSE_PROJECTILE = 1;
    public static final int TELEPORT_CAUSE_CHORUS_FRUIT = 2;
    public static final int TELEPORT_CAUSE_COMMAND = 3;
    public static final int TELEPORT_CAUSE_BEHAVIOR = 4;

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public int mode = MODE_NORMAL;
    public boolean onGround;
    public long ridingEid;
    public int teleportCause = TELEPORT_CAUSE_UNKNOWN;
    public int entityType = 0;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.mode = this.getByte();
        this.onGround = this.getBoolean();
        this.ridingEid = this.getEntityRuntimeId();
        if (this.mode == MODE_TELEPORT) {
            this.teleportCause = this.getLInt();
            this.entityType = this.getLInt();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putByte((byte) this.mode);
        this.putBoolean(this.onGround);
        this.putEntityRuntimeId(this.ridingEid);
        if (this.mode == MODE_TELEPORT) {
            this.putLInt(this.teleportCause);
            this.putLInt(this.entityType);
        }
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

}
