package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Mth;
import cn.nukkit.utils.BinaryStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("PlaySoundPacket")
class PlaySoundPacketTest {

    private static final int HEADER_LENGTH = 3;

    /**
     * 按改动前的实现（整格坐标乘 8）显式重建期望字节，用于钉住零回归保证。
     */
    private static byte[] legacyEncode(String name, int x, int y, int z, float volume, float pitch) {
        BinaryStream expected = new BinaryStream();
        expected.putByte((byte) ProtocolInfo.PLAY_SOUND_PACKET);
        expected.putShort(0);
        expected.putString(name);
        expected.putBlockVector3(x * 8, y * 8, z * 8);
        expected.putLFloat(volume);
        expected.putLFloat(pitch);
        return expected.getBuffer();
    }

    private static BinaryStream payload(PlaySoundPacket packet) {
        packet.encode();
        return new BinaryStream(packet.getBuffer(), HEADER_LENGTH);
    }

    @DisplayName("未使用精确坐标时与改动前的编码逐字节一致")
    @Test
    void legacyEncodingIsByteIdentical() {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = "note.harp";
        packet.x = 10;
        packet.y = 64;
        packet.z = -3;
        packet.volume = 0.75f;
        packet.pitch = 1.25f;
        packet.encode();

        assertArrayEquals(legacyEncode("note.harp", 10, 64, -3, 0.75f, 1.25f), packet.getBuffer());
    }

    @DisplayName("整数坐标下精确坐标与整格编码结果一致")
    @Test
    void integralExactPositionMatchesLegacyEncoding() {
        PlaySoundPacket exact = new PlaySoundPacket();
        exact.name = "note.bd";
        exact.setExactPosition(10, 64, -3);
        exact.volume = 1f;
        exact.pitch = 1f;
        exact.encode();

        assertArrayEquals(legacyEncode("note.bd", 10, 64, -3, 1f, 1f), exact.getBuffer());
    }

    @DisplayName("精确坐标按 1/8 格定点向下取整编码，并回写整格坐标")
    @Test
    void exactPositionUsesEighthBlockFixedPoint() {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = "note.harp";
        packet.setExactPosition(10.25d, 64.5d, -3.125d);
        packet.volume = 0.5f;
        packet.pitch = 2f;

        // 整格字段被回写为 floor 值，保证读取 x/y/z 的既有逻辑仍然合理
        assertEquals(10, packet.x);
        assertEquals(64, packet.y);
        assertEquals(-4, packet.z);

        BinaryStream in = payload(packet);
        assertEquals("note.harp", in.getString());
        BlockVector3 pos = in.getBlockVector3();
        assertEquals(Mth.floor(10.25d * 8.0d), pos.x);
        assertEquals(Mth.floor(64.5d * 8.0d), pos.y);
        assertEquals(Mth.floor(-3.125d * 8.0d), pos.z);
        assertEquals(0.5f, in.getLFloat());
        assertEquals(2f, in.getLFloat());
    }

    @DisplayName("精确坐标使用向下取整而不是四舍五入")
    @Test
    void exactPositionFloorsInsteadOfRounding() {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = "note.pling";
        packet.setExactPosition(10.2d, 64.2d, 0.1d);
        packet.volume = 1f;
        packet.pitch = 1f;

        BinaryStream in = payload(packet);
        in.getString();
        BlockVector3 pos = in.getBlockVector3();
        // 这三组值在 floor 与 round 下结果不同，用于防止实现被改成四舍五入
        assertEquals(81, pos.x); // floor(10.2 * 8) = 81，四舍五入会得到 82
        assertEquals(513, pos.y); // floor(64.2 * 8) = 513，四舍五入会得到 514
        assertEquals(0, pos.z); // floor(0.1 * 8) = 0，四舍五入会得到 1
    }

    @DisplayName("y 为负时退回整格编码，避免无符号 varint 写坏")
    @Test
    void negativeExactYFallsBackToLegacyEncoding() {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = "note.harp";
        packet.setExactPosition(1.5d, -0.5d, 2.25d);
        packet.volume = 1f;
        packet.pitch = 1f;
        packet.encode();

        // 回写整格后 y = floor(-0.5) = -1，编码退回整格分支
        assertArrayEquals(legacyEncode("note.harp", 1, -1, 2, 1f, 1f), packet.getBuffer());
    }

    @DisplayName("未调用 setExactPosition 时精确字段保持默认值")
    @Test
    void exactFieldsDefaultOff() {
        PlaySoundPacket packet = new PlaySoundPacket();
        assertFalse(packet.exactPosition);
        assertEquals(0.0d, packet.exactX);
        assertEquals(0.0d, packet.exactY);
        assertEquals(0.0d, packet.exactZ);
    }
}
