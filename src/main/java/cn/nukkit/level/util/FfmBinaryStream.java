package cn.nukkit.level.util;

import cn.nukkit.utils.BinaryStream;

import javax.annotation.Nullable;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.*;

/**
 * 区块存储使用的内存段流。输入借用堆数组或数据库会话的内存，输出由调用方提供的 Arena 持有。
 * 流及其视图不能越过外层生命周期；扩容前的内存统一在外层 Arena 关闭时释放。
 */
public final class FfmBinaryStream extends BinaryStream {
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;
    private static final byte[] EMPTY_BUFFER = new byte[0];

    private static final OfShort SHORT_BE = JAVA_SHORT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN);
    private static final OfShort SHORT_LE = JAVA_SHORT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    private static final OfInt INT_BE = JAVA_INT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN);
    private static final OfInt INT_LE = JAVA_INT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    private static final OfLong LONG_BE = JAVA_LONG_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN);
    private static final OfLong LONG_LE = JAVA_LONG_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);

    @Nullable
    private final Arena arena;
    private MemorySegment segment;

    public FfmBinaryStream(MemorySegment input) {
        super(EMPTY_BUFFER);
        Objects.requireNonNull(input, "input");
        if (input.byteSize() > MAX_CAPACITY) {
            throw new IllegalArgumentException("Input exceeds maximum stream size");
        }
        arena = null;
        segment = input.asReadOnly();
        super.setCount((int) input.byteSize());
    }

    public FfmBinaryStream(Arena arena, int initialCapacity) {
        super(EMPTY_BUFFER);
        this.arena = Objects.requireNonNull(arena, "arena");
        if (initialCapacity < 0 || initialCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Invalid initial capacity");
        }
        segment = arena.allocate(Math.max(initialCapacity, 1), 1);
    }

    /**
     * 返回有效数据的只读借用视图；写入批次同步复制后才可复用输出缓冲。
     */
    public MemorySegment getWrittenSegment() {
        return segment.asSlice(0, getCount()).asReadOnly();
    }

    @Override
    public void reuse() {
        requireWritable();
        super.reuse();
    }

    @Override
    public void reset() {
        reuse();
    }

    @Override
    public void setBuffer(byte[] buffer) {
        throw new UnsupportedOperationException("Native stream cannot replace its backing memory with an array");
    }

    @Override
    public void setCount(int count) {
        requireWritable();
        if (count < 0 || count > segment.byteSize()) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        super.setCount(count);
    }

    @Override
    public void setOffset(int position) {
        if (position < 0 || position > getCount()) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        super.setOffset(position);
    }

    @Override
    public byte[] getBuffer() {
        return getWrittenSegment().toArray(JAVA_BYTE);
    }

    @Override
    public byte[] getBuffer(int from) {
        return getBuffer(from, getCount());
    }

    @Override
    public byte[] getBuffer(int from, int to) {
        Objects.checkFromToIndex(from, to, getCount());
        return segment.asSlice(from, to - from).toArray(JAVA_BYTE);
    }

    @Override
    public byte[] getBufferUnsafe() {
        throw new UnsupportedOperationException("Native stream has no backing array; use a stream or segment view");
    }

    @Override
    public byte[] get(int length) {
        int index = readIndex(length);
        return segment.asSlice(index, length).toArray(JAVA_BYTE);
    }

    @Override
    public void get(byte[] target, int targetOffset, int length) {
        Objects.checkFromIndexSize(targetOffset, length, target.length);
        int index = readIndex(length);
        MemorySegment.copy(segment, index, MemorySegment.ofArray(target), targetOffset, length);
    }

    @Override
    public void put(byte[] source) {
        if (source != null && source.length != 0) {
            put(source, 0, source.length);
        }
    }

    @Override
    public void put(byte[] source, int length) {
        if (source != null && length > 0) {
            put(source, 0, Math.min(length, source.length));
        }
    }

    @Override
    public void put(byte[] source, int sourceOffset, int length) {
        Objects.checkFromIndexSize(sourceOffset, length, source.length);
        int index = writeIndex(length);
        MemorySegment.copy(MemorySegment.ofArray(source), sourceOffset, segment, index, length);
    }

    @Override
    public void putEmptyBytes(int length) {
        if (length > 0) {
            segmentForWrite(length).fill((byte) 0);
        }
    }

    private MemorySegment segmentForWrite(int length) {
        int index = writeIndex(length);
        return segment.asSlice(index, length);
    }

    private int readIndex(int length) {
        if (!isReadable(length)) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        int index = offset;
        offset += length;
        return index;
    }

    private int writeIndex(int length) {
        int index = prepareWrite(length);
        super.setCount(index + length);
        return index;
    }

    private int prepareWrite(int length) {
        requireWritable();
        int count = getCount();
        if (length < 0 || length > MAX_CAPACITY - count) {
            throw new IndexOutOfBoundsException("Output exceeds maximum stream size");
        }
        int required = count + length;
        if (required > segment.byteSize()) {
            long capacity = Math.max(required, Math.min(MAX_CAPACITY, Math.max(32, segment.byteSize() * 2)));
            MemorySegment expanded = arena.allocate(capacity, 1);
            MemorySegment.copy(segment, 0, expanded, 0, count);
            segment = expanded;
        }
        return count;
    }

    private void requireWritable() {
        if (arena == null) {
            throw new UnsupportedOperationException("Native input stream is read-only");
        }
    }

    @Override
    public boolean feof() {
        return offset < 0 || offset >= getCount();
    }

    @Override
    public byte getSignedByte() {
        return segment.get(JAVA_BYTE, readIndex(1));
    }

    @Override
    public int getByte() {
        return getSignedByte() & 0xff;
    }

    @Override
    public void putByte(byte value) {
        int index = writeIndex(1);
        segment.set(JAVA_BYTE, index, value);
    }

    @Override
    public short getSignedShort() {
        return segment.get(SHORT_BE, readIndex(Short.BYTES));
    }

    @Override
    public int getShort() {
        return getSignedShort() & 0xffff;
    }

    @Override
    public short getLSignedShort() {
        return segment.get(SHORT_LE, readIndex(Short.BYTES));
    }

    @Override
    public int getLShort() {
        return getLSignedShort() & 0xffff;
    }

    @Override
    public void putShort(int value) {
        int index = writeIndex(Short.BYTES);
        segment.set(SHORT_BE, index, (short) value);
    }

    @Override
    public void putLShort(int value) {
        int index = writeIndex(Short.BYTES);
        segment.set(SHORT_LE, index, (short) value);
    }

    @Override
    public int getInt() {
        return segment.get(INT_BE, readIndex(Integer.BYTES));
    }

    @Override
    public int getLInt() {
        return segment.get(INT_LE, readIndex(Integer.BYTES));
    }

    @Override
    public void putInt(int value) {
        int index = writeIndex(Integer.BYTES);
        segment.set(INT_BE, index, value);
    }

    @Override
    public void putLInt(int value) {
        int index = writeIndex(Integer.BYTES);
        segment.set(INT_LE, index, value);
    }

    @Override
    public int[] getLInts(int length) {
        if (length < 0 || length > Integer.MAX_VALUE / Integer.BYTES || !isReadable(length * Integer.BYTES)) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        int[] values = new int[length];
        MemorySegment.copy(segment, INT_LE, offset, values, 0, length);
        offset += length * Integer.BYTES;
        return values;
    }

    @Override
    public void putLInts(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length > MAX_CAPACITY / Integer.BYTES) {
            throw new IndexOutOfBoundsException("Output exceeds maximum stream size");
        }
        int byteLength = values.length * Integer.BYTES;
        int index = prepareWrite(byteLength);
        MemorySegment.copy(values, 0, segment, INT_LE, index, values.length);
        super.setCount(index + byteLength);
    }

    @Override
    public long getLong() {
        return segment.get(LONG_BE, readIndex(Long.BYTES));
    }

    @Override
    public long getLLong() {
        return segment.get(LONG_LE, readIndex(Long.BYTES));
    }

    @Override
    public void putLong(long value) {
        int index = writeIndex(Long.BYTES);
        segment.set(LONG_BE, index, value);
    }

    @Override
    public void putLLong(long value) {
        int index = writeIndex(Long.BYTES);
        segment.set(LONG_LE, index, value);
    }

    @Override
    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    @Override
    public float getLFloat() {
        return Float.intBitsToFloat(getLInt());
    }

    @Override
    public void putFloat(float value) {
        putInt(Float.floatToIntBits(value));
    }

    @Override
    public void putLFloat(float value) {
        putLInt(Float.floatToIntBits(value));
    }

    @Override
    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    @Override
    public double getLDouble() {
        return Double.longBitsToDouble(getLLong());
    }

    @Override
    public void putDouble(double value) {
        putLong(Double.doubleToLongBits(value));
    }

    @Override
    public void putLDouble(double value) {
        putLLong(Double.doubleToLongBits(value));
    }

    @Override
    public int getTriad() {
        int index = readIndex(3);
        return ((segment.get(JAVA_BYTE, index) & 0xff) << 16)
                | ((segment.get(JAVA_BYTE, index + 1) & 0xff) << 8)
                | (segment.get(JAVA_BYTE, index + 2) & 0xff);
    }

    @Override
    public int getLTriad() {
        int index = readIndex(3);
        return (segment.get(JAVA_BYTE, index) & 0xff)
                | ((segment.get(JAVA_BYTE, index + 1) & 0xff) << 8)
                | ((segment.get(JAVA_BYTE, index + 2) & 0xff) << 16);
    }

    @Override
    public void putTriad(int value) {
        int index = writeIndex(3);
        segment.set(JAVA_BYTE, index, (byte) (value >>> 16));
        segment.set(JAVA_BYTE, index + 1, (byte) (value >>> 8));
        segment.set(JAVA_BYTE, index + 2, (byte) value);
    }

    @Override
    public void putLTriad(int value) {
        int index = writeIndex(3);
        segment.set(JAVA_BYTE, index, (byte) value);
        segment.set(JAVA_BYTE, index + 1, (byte) (value >>> 8));
        segment.set(JAVA_BYTE, index + 2, (byte) (value >>> 16));
    }
}
