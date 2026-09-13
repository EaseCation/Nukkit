package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.utils.DataLengthException;
import cn.nukkit.utils.VarInt;
import io.netty.buffer.ByteBufInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTInputStream implements DataInput, AutoCloseable {
    private final DataInputStream stream;
    private final ByteOrder endianness;
    private final boolean network;
    private final long maxReadSize;
    private long arrayReadSize;

    public NBTInputStream(InputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness, boolean network) {
        this(stream, endianness, network, getDefaultMaxReadSize(stream, network));
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness, boolean network, long maxReadSize) {
        Objects.requireNonNull(stream, "stream");
        if (maxReadSize < 0) {
            throw new IllegalArgumentException("Maximum read size cannot be negative");
        }
        this.stream = stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream);
        this.endianness = endianness;
        this.network = network;
        this.maxReadSize = maxReadSize;
    }

    private static long getDefaultMaxReadSize(InputStream stream, boolean network) {
        Objects.requireNonNull(stream, "stream");
        if (stream instanceof ByteArrayInputStream || stream instanceof FastByteArrayInputStream || stream instanceof ByteBufInputStream) {
            try {
                long available = stream.available();
                return network ? available * Integer.BYTES : available;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to determine NBT input size", e);
            }
        }
        return NBTIO.MAX_READ_SIZE;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    public boolean isNetwork() {
        return network;
    }

    public void tryReadArray(int length, int elementSize) throws DataLengthException {
        if (length < 0) {
            throw new DataLengthException("Negative array length: " + length);
        }
        if (elementSize <= 0) {
            throw new DataLengthException("Invalid array element size: " + elementSize);
        }
        if (maxReadSize == 0) {
            return;
        }

        long byteSize = (long) length * elementSize;
        if (arrayReadSize > maxReadSize || byteSize > maxReadSize - arrayReadSize) {
            throw new DataLengthException("NBT array allocation exceeds limit: requested=" + byteSize + ", allocated=" + arrayReadSize + ", limit=" + maxReadSize);
        }
        arrayReadSize += byteSize;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.stream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short s = this.stream.readShort();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s);
        }
        return s;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    @Override
    public char readChar() throws IOException {
        char c = this.stream.readChar();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c);
        }
        return c;
    }

    @Override
    public int readInt() throws IOException {
        if (network) {
            try {
                return VarInt.readVarInt(this.stream);
            } catch (IllegalArgumentException e) {
                throw new DataLengthException("Invalid network VarInt", e);
            }
        }
        int i = this.stream.readInt();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return i;
    }

    @Override
    public long readLong() throws IOException {
        if (network) {
            try {
                return VarInt.readVarLong(this.stream);
            } catch (IllegalArgumentException e) {
                throw new DataLengthException("Invalid network VarLong", e);
            }
        }
        long l = this.stream.readLong();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        int i = this.stream.readInt();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return Float.intBitsToFloat(i);
    }

    @Override
    public double readDouble() throws IOException {
        long l = this.stream.readLong();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return Double.longBitsToDouble(l);
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int length = readUTFLength();
        tryReadArray(length, Byte.BYTES);
        byte[] bytes = new byte[length];
        this.stream.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String readUTF(int maxLen) throws IOException {
        int length = readUTFLength();
        if (length > maxLen) {
            throw new DataLengthException("Input too long: " + length + " > " + maxLen);
        }
        tryReadArray(length, Byte.BYTES);
        byte[] bytes = new byte[length];
        this.stream.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private int readUTFLength() throws IOException {
        long length;
        try {
            length = network ? VarInt.readUnsignedVarInt(stream) : this.readUnsignedShort();
        } catch (IllegalArgumentException e) {
            throw new DataLengthException("Invalid network string length", e);
        }
        if (length > Integer.MAX_VALUE) {
            throw new DataLengthException("String length exceeds integer range: " + length);
        }
        return (int) length;
    }

    public int available() throws IOException {
        return this.stream.available();
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
