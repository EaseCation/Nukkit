package cn.nukkit.network;

import cn.nukkit.utils.Binary;
import cn.nukkit.utils.DataLengthException;
import cn.nukkit.utils.Zlib;
import org.xerial.snappy.Snappy;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.zip.DataFormatException;

public enum Compressor {
    NONE {
        @Override
        public byte[] compress(byte[] data, int level) {
            return data;
        }

        @Override
        public byte[] compress(byte[][] data, int level) {
            return Binary.appendBytes(data);
        }

        @Override
        public byte[] decompress(byte[] data, int maxSize) throws IOException {
            if (data.length >= maxSize) {
                throw new DataLengthException("Data exceeds maximum size");
            }
            return data;
        }

        @Override
        public byte getAlgorithm() {
            return CompressionAlgorithm.NONE;
        }
    },
    ZLIB {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return Zlib.deflate(data, level);
        }

        @Override
        public byte[] compress(byte[][] data, int level) throws IOException {
            return Zlib.deflate(data, level);
        }

        @Override
        public byte[] decompress(byte[] data, int maxSize) throws IOException {
            return Zlib.inflate(data, maxSize);
        }

        @Override
        public byte getAlgorithm() {
            return CompressionAlgorithm.ZLIB;
        }
    },
    ZLIB_RAW {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return Network.deflateRaw(data, level);
        }

        @Override
        public byte[] compress(byte[][] data, int level) throws IOException {
            return Network.deflateRaw(data, level);
        }

        @Override
        public byte[] decompress(byte[] data, int maxSize) throws IOException, DataFormatException {
            return Network.inflateRaw(data, maxSize);
        }

        @Override
        public byte getAlgorithm() {
            return CompressionAlgorithm.ZLIB;
        }
    },
    SNAPPY {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            int inputLength = data.length;
            int maxOutputLength = Snappy.maxCompressedLength(inputLength);
            byte[] buffer = new byte[maxOutputLength];
            int outputLength = Snappy.compress(data, 0, inputLength, buffer, 0);
            if (outputLength == maxOutputLength) {
                return buffer;
            }
            byte[] result = new byte[outputLength];
            System.arraycopy(buffer, 0, result, 0, outputLength);
            return result;
        }

        @Override
        public byte[] compress(byte[][] data, int level) throws IOException {
            return compress(Binary.appendBytes(data), level);
        }

        @Override
        public byte[] decompress(byte[] data, int maxSize) throws IOException {
            int inputLength = data.length;
            if (inputLength >= maxSize) {
                throw new DataLengthException("Input data exceeds maximum size");
            }
            int maxOutputLength = Snappy.uncompressedLength(data);
            if (maxOutputLength >= maxSize) {
                throw new DataLengthException("Inflated buffer exceeds maximum size");
            }
//            if (!Snappy.isValidCompressedBuffer(data)) {
//                throw new IOException("Invalid input data");
//            }
            byte[] buffer = new byte[maxOutputLength];
            int outputLength = Snappy.uncompress(data, 0, inputLength, buffer, 0);
            if (outputLength == maxOutputLength) {
                return buffer;
            }
            if (outputLength >= maxSize) {
                throw new DataLengthException("Inflated data exceeds maximum size");
            }
            byte[] result = new byte[outputLength];
            System.arraycopy(buffer, 0, result, 0, outputLength);
            return result;
        }

        @Override
        public byte getAlgorithm() {
            return CompressionAlgorithm.SNAPPY;
        }
    }
    ;

    public static final int MAX_SIZE = 12 * 1024 * 1024; // 12MB

    @Nullable
    private static Compressor DYNAMIC_COMPRESSOR;

    public abstract byte[] compress(byte[] data, int level) throws IOException;

    public abstract byte[] compress(byte[][] data, int level) throws IOException;

    public byte[] decompress(byte[] data) throws IOException, DataFormatException {
        return decompress(data, MAX_SIZE);
    }

    public abstract byte[] decompress(byte[] data, int maxSize) throws IOException, DataFormatException;

    public abstract byte getAlgorithm();

    @Nullable
    public static Compressor get(byte algorithm) {
        return switch (algorithm) {
            case CompressionAlgorithm.ZLIB -> Compressor.ZLIB_RAW;
            case CompressionAlgorithm.SNAPPY -> Compressor.SNAPPY;
            case CompressionAlgorithm.NONE -> Compressor.NONE;
            default -> null;
        };
    }

    public static Compressor byProtocol(int protocol) {
        Compressor compressor;
        if (protocol >= 649 && (compressor = DYNAMIC_COMPRESSOR) != null) {
            return compressor;
        }
        if (protocol >= 554) {
            return SNAPPY;
        }
        if (protocol >= 407) {
            return ZLIB_RAW;
        }
        return ZLIB;
    }

    public static byte getAlgorithmByName(String name) {
        if ("snappy".equalsIgnoreCase(name)) {
            return CompressionAlgorithm.SNAPPY;
        }
        if ("none".equalsIgnoreCase(name)) {
            return CompressionAlgorithm.NONE;
        }
        return CompressionAlgorithm.ZLIB;
    }

    public static void setDynamicCompressor(Compressor compressor) {
        DYNAMIC_COMPRESSOR = compressor;
    }
}
