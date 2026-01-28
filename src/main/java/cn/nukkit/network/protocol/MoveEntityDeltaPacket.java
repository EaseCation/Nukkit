package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @since 1.5
 */
@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOVE_ACTOR_DELTA_PACKET;

    @SuppressWarnings("PointlessBitwiseExpression")
    public static final int FLAG_HAS_X = 1 << 0;
    public static final int FLAG_HAS_Y = 1 << 1;
    public static final int FLAG_HAS_Z = 1 << 2;
    public static final int FLAG_HAS_PITCH = 1 << 3;
    public static final int FLAG_HAS_YAW = 1 << 4;
    public static final int FLAG_HAS_HEAD_YAW = 1 << 5;
    public static final int FLAG_GROUND = 1 << 6;
    public static final int FLAG_TELEPORT = 1 << 7;
    public static final int FLAG_FORCE_MOVE_LOCAL_ENTITY = 1 << 8;

    public long entityRuntimeId;
    public int flags;
    public float x;
    public float y;
    public float z;
    public float pitch;
    public float yaw;
    public float headYaw;

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
        this.putEntityRuntimeId(entityRuntimeId);
        this.putByte(flags);
        putCoordinate(FLAG_HAS_X, (int) x);
        putCoordinate(FLAG_HAS_Y, (int) y);
        putCoordinate(FLAG_HAS_Z, (int) z);
        putRotation(FLAG_HAS_PITCH, pitch);
        putRotation(FLAG_HAS_YAW, yaw);
        putRotation(FLAG_HAS_HEAD_YAW, headYaw);
    }

    private void putCoordinate(int flag, int value) {
        if ((flags & flag) != 0) {
            this.putVarInt(value);
        }
    }

    private void putRotation(int flag, float value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / (360f / 256f)));
        }
    }
}
