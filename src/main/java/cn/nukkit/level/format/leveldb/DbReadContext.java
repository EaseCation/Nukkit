package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.leveldb.ffm.DBIterator;
import cn.nukkit.level.util.FfmBinaryStream;
import cn.nukkit.utils.BinaryStream;
import org.iq80.leveldb.DB;

import javax.annotation.Nullable;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;
import java.util.Arrays;

import static java.lang.foreign.ValueLayout.*;

/**
 * 一次区块读取独占自己的键缓冲和原生会话，嵌套加载不会覆盖外层数据。
 */
final class DbReadContext implements AutoCloseable {
    private static final OfInt INT_LE = JAVA_INT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);

    private final DB database;
    @Nullable
    private final FfmDB.ReadSession nativeSession;
    @Nullable
    private final byte[] key;
    @Nullable
    private final byte[] subchunkKey;
    private final int typeOffset;
    @Nullable
    private final MemorySegment nativeKey;
    @Nullable
    private final MemorySegment nativeSubchunkKey;
    @Nullable
    private DBIterator subchunkCursor;
    private int lastCursorY;
    private boolean cursorRangeEnded;

    DbReadContext(DB database, int chunkX, int chunkZ, int dimension) {
        this.database = database;
        this.typeOffset = dimension == Level.DIMENSION_OVERWORLD ? 2 * Integer.BYTES : 3 * Integer.BYTES;
        if (database instanceof FfmDB ffm) {
            this.key = null;
            this.subchunkKey = null;
            FfmDB.ReadSession session = ffm.openReadSession();
            try {
                MemorySegment buffer = session.allocateKey(typeOffset + 2);
                buffer.set(INT_LE, 0, chunkX);
                buffer.set(INT_LE, Integer.BYTES, chunkZ);
                if (dimension != Level.DIMENSION_OVERWORLD) {
                    buffer.set(INT_LE, 2 * Integer.BYTES, dimension);
                }
                this.nativeKey = buffer.asSlice(0, typeOffset + 1);
                this.nativeSubchunkKey = buffer;
                this.nativeSession = session;
            } catch (RuntimeException | Error failure) {
                try {
                    session.close();
                } catch (RuntimeException | Error cleanupFailure) {
                    failure.addSuppressed(cleanupFailure);
                }
                throw failure;
            }
        } else {
            this.key = LevelDBKey.NEW_VERSION.getKey(chunkX, chunkZ, dimension);
            this.subchunkKey = Arrays.copyOf(key, key.length + 1);
            this.nativeKey = null;
            this.nativeSubchunkKey = null;
            this.nativeSession = null;
        }
    }

    @Nullable
    BinaryStream get(LevelDBKey type) {
        if (nativeSession != null) {
            nativeKey.set(JAVA_BYTE, typeOffset, type.getCode());
            return wrap(nativeSession.get(nativeKey));
        }
        key[typeOffset] = type.getCode();
        byte[] value = database.get(key);
        return value == null ? null : wrap(MemorySegment.ofArray(value));
    }

    @Nullable
    BinaryStream getSubchunk(int chunkY) {
        if (nativeSession != null) {
            nativeSubchunkKey.set(JAVA_BYTE, typeOffset, LevelDBKey.SUBCHUNK.getCode());
            nativeSubchunkKey.set(JAVA_BYTE, typeOffset + 1, (byte) chunkY);
            // 非递增或无法用有符号字节表示的请求保留点查语义，不回退或重置游标。
            if (chunkY < Byte.MIN_VALUE || chunkY > Byte.MAX_VALUE
                    || subchunkCursor != null && chunkY <= lastCursorY) {
                return wrap(nativeSession.get(nativeSubchunkKey));
            }
            int unsignedY = chunkY & 0xff;
            if (subchunkCursor == null) {
                subchunkCursor = nativeSession.openCursor();
                subchunkCursor.seek(nativeSubchunkKey);
            } else if (unsignedY < (lastCursorY & 0xff)) {
                // 原生键按无符号字节排序，-1 到 0 时重新定位一次。
                subchunkCursor.seek(nativeSubchunkKey);
                cursorRangeEnded = false;
            }
            lastCursorY = chunkY;
            if (cursorRangeEnded) {
                return null;
            }
            while (subchunkCursor.isValid()) {
                MemorySegment currentKey = subchunkCursor.keySegment();
                if (currentKey.byteSize() < typeOffset + 1
                        || currentKey.get(JAVA_BYTE, typeOffset) != LevelDBKey.SUBCHUNK.getCode()
                        || MemorySegment.mismatch(currentKey, 0, typeOffset, nativeKey, 0, typeOffset) != -1) {
                    subchunkCursor.checkError();
                    cursorRangeEnded = true;
                    return null;
                }
                if (currentKey.byteSize() != typeOffset + 2) {
                    // 主世界前缀可能与维度 47 的记录交错，错长键不能直接终止扫描。
                    subchunkCursor.moveNext();
                    continue;
                }
                int currentY = currentKey.get(JAVA_BYTE, typeOffset + 1) & 0xff;
                if (currentY < unsignedY) {
                    subchunkCursor.moveNext();
                    continue;
                }
                if (currentY > unsignedY) {
                    return null;
                }
                // 返回时不推进，调用方必须在下次 getSubchunk 前完整解码借用值。
                return wrap(subchunkCursor.valueSegment());
            }
            subchunkCursor.checkError();
            cursorRangeEnded = true;
            return null;
        }
        subchunkKey[typeOffset] = LevelDBKey.SUBCHUNK.getCode();
        subchunkKey[typeOffset + 1] = (byte) chunkY;
        byte[] value = database.get(subchunkKey);
        return value == null ? null : wrap(MemorySegment.ofArray(value));
    }

    @Nullable
    private static BinaryStream wrap(@Nullable MemorySegment value) {
        return value == null ? null : new FfmBinaryStream(value);
    }

    @Override
    public void close() {
        if (nativeSession != null) {
            nativeSession.close();
        }
    }
}
