package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MoveEntityPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOVE_ACTOR_ABSOLUTE_PACKET;

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public boolean onGround;
    public boolean teleport;
    public boolean forceMoveLocalEntity;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getByte() * (360f / 256f);
        this.yaw = this.getByte() * (360f / 256f);
        this.headYaw = this.getByte() * (360f / 256f);
        this.onGround = this.getBoolean();
        this.teleport = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVector3f(this.x, this.y, this.z);
        this.putByte((byte) (this.pitch / (360f / 256f)));
        this.putByte((byte) (this.yaw / (360f / 256f)));
        this.putByte((byte) (this.headYaw / (360f / 256f)));
        this.putBoolean(this.onGround);
        this.putBoolean(this.teleport);
    }
}
