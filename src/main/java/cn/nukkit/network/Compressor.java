package cn.nukkit.network;

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
        public byte[] decompress(byte[] data) throws IOException {
            if (data.length >= MAX_SIZE) {
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
        public byte[] decompress(byte[] data) throws IOException {
            return Zlib.inflate(data, MAX_SIZE);
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
        public byte[] decompress(byte[] data) throws IOException, DataFormatException {
            return Network.inflateRaw(data, MAX_SIZE);
        }

        @Override
        public byte getAlgorithm() {
            return CompressionAlgorithm.ZLIB;
        }
    },
    ZLIB_UNKNOWN {
        @Override
        public byte[] compress(byte[] data, int level) throws IOException {
            return ZLIB_RAW.compress(data, level);
        }

        @Override
        public byte[] decompress(byte[] data) throws IOException {
            try {
                return ZLIB_RAW.decompress(data);
            } catch (DataLengthException e) {
                throw e;
            } catch (Exception e) {
                try {
                    return ZLIB.decompress(data);
                } catch (Exception ex) {
                    return EMPTY;
                }
            }
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
        public byte[] decompress(byte[] data) throws IOException {
            int inputLength = data.length;
            if (inputLength >= MAX_SIZE) {
                throw new DataLengthException("Input data exceeds maximum size");
            }
            int maxOutputLength = Snappy.uncompressedLength(data);
            if (maxOutputLength >= MAX_SIZE) {
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
            if (outputLength >= MAX_SIZE) {
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
    };

    public static final int MAX_SIZE = 12 * 1024 * 1024; // 12MB

    private static final byte[] EMPTY = new byte[0];

    public abstract byte[] compress(byte[] data, int level) throws IOException;

    public abstract byte[] decompress(byte[] data) throws IOException, DataFormatException;

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
}
