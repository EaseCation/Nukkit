package cn.nukkit.network.protocol;

import cn.nukkit.math.Mth;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString
public class PlaySoundPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PLAY_SOUND_PACKET;

    public String name;
    public int x;
    public int y;
    public int z;
    public float volume;
    public float pitch;
    @Nullable
    public Long serverSoundHandle;

    /**
     * 是否使用 1/8 格定点精确坐标。
     * 默认 false，保证既有调用方（整格坐标，编码时乘 8）的输出字节与改动前完全一致。
     */
    public boolean exactPosition;
    /**
     * 精确坐标（单位：格），仅在 {@link #exactPosition} 为 true 时参与编码。
     * 客户端按 1/8 格定点解析，因此可表达的最小精度为 0.125 格。
     */
    public double exactX;
    public double exactY;
    public double exactZ;

    /**
     * 设置 1/8 格精确坐标。
     * 同时回写整格坐标，使读取 x/y/z 的既有逻辑（如录像、命令回显）仍能得到合理值。
     *
     * @param x x 坐标，单位格
     * @param y y 坐标，单位格
     * @param z z 坐标，单位格
     */
    public void setExactPosition(double x, double y, double z) {
        this.exactPosition = true;
        this.exactX = x;
        this.exactY = y;
        this.exactZ = z;
        this.x = Mth.floor(x);
        this.y = Mth.floor(y);
        this.z = Mth.floor(z);
    }

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
        this.putString(this.name);
        if (this.exactPosition && this.exactY >= 0) {
            // 客户端按 1/8 格定点解析坐标，编码为 floor(坐标 * 8)；取整方向必须是向下取整
            this.putBlockVector3(Mth.floor(this.exactX * 8.0d), Mth.floor(this.exactY * 8.0d), Mth.floor(this.exactZ * 8.0d));
        } else {
            // y 字段走 unsigned varint，负值无法表达，这里退回整格编码
            this.putBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        }
        this.putLFloat(this.volume);
        this.putLFloat(this.pitch);
    }
}
