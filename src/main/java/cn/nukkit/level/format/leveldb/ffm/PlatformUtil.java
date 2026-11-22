package cn.nukkit.level.format.leveldb.ffm;

import io.netty.buffer.ByteBuf;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static java.lang.foreign.MemorySegment.*;
import static java.util.Objects.*;

final class PlatformUtil {

    static MemorySegment requireNative(MemorySegment segment, String name) {
        requireNonNull(segment, name);
        if (!segment.isNative()) {
            throw new IllegalArgumentException(name + " must be backed by native memory");
        }
        return segment;
    }

    static MemorySegment fromByteBuffer(ByteBuffer source, SegmentAllocator allocator) {
        return fromByteBuffer(source, null, allocator);
    }

    static MemorySegment fromByteBuffer(ByteBuffer source, NativeBuffer fallbackBuffer, SegmentAllocator allocator) {
        requireNonNull(source, "source");
        MemorySegment sourceSegment = ofBuffer(source);
        if (sourceSegment.isNative()) {
            return sourceSegment;
        }

        int length = source.remaining();
        MemorySegment target = fallbackBuffer != null ? fallbackBuffer.reserve(length, allocator)
                : allocator.allocate(max(length, 1), 1).asSlice(0, length);
        target.copyFrom(sourceSegment);
        return target;
    }

    static MemorySegment fromByteBuf(ByteBuf source, SegmentAllocator allocator) {
        return fromByteBuf(source, null, allocator);
    }

    static MemorySegment fromByteBuf(ByteBuf source, NativeBuffer fallbackBuffer, SegmentAllocator allocator) {
        requireNonNull(source, "source");
        int index = source.readerIndex();
        int length = source.readableBytes();

        if (source.isDirect() && source.nioBufferCount() == 1) {
            try {
                MemorySegment segment = ofBuffer(source.nioBuffer(index, length));
                if (segment.isNative()) {
                    return segment;
                }
            } catch (UnsupportedOperationException ignored) {
                // Fall back to a contiguous native copy below.
            }
        }

        MemorySegment target = fallbackBuffer != null ? fallbackBuffer.reserve(length, allocator)
                : allocator.allocate(max(length, 1), 1).asSlice(0, length);
        if (length != 0) {
            source.getBytes(index, target.asByteBuffer());
        }
        return target;
    }

    private PlatformUtil() {
        throw new IllegalStateException();
    }

    static final class NativeBuffer {

        private static final long MIN_CAPACITY = 64;

        private MemorySegment segment = NULL;
        private long capacity;

        MemorySegment copyFrom(byte[] source, SegmentAllocator allocator) {
            return copyFrom(source, 0, source.length, allocator);
        }

        MemorySegment copyFrom(byte[] source, int offset, int length, SegmentAllocator allocator) {
            checkFromIndexSize(offset, length, source.length);
            MemorySegment target = reserve(length, allocator);
            if (length != 0) {
                copy(ofArray(source), offset, target, 0, length);
            }
            return target;
        }

        MemorySegment reserve(int size, SegmentAllocator allocator) {
            int requiredCapacity = max(size, 1);
            if (capacity < requiredCapacity) {
                capacity = nextCapacity(requiredCapacity);
                segment = allocator.allocate(capacity, 1);
            }
            return segment.asSlice(0, size);
        }

        private static long nextCapacity(int requiredCapacity) {
            return max(MIN_CAPACITY, (Long.highestOneBit((long) requiredCapacity - 1)) << 1);
        }
    }
}
