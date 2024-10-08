package cn.nukkit.network.protocol;

import lombok.ToString;

import javax.annotation.Nullable;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
@ToString
public class ChangeDimensionPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public int dimension;

    public float x;
    public float y;
    public float z;

    public boolean respawn;

    @Nullable
    public Integer loadingScreenId;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.dimension);
        this.putVector3f(this.x, this.y, this.z);
        this.putBoolean(this.respawn);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
