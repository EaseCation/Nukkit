package cn.nukkit.network;

public interface CompressionAlgorithm {
    /**
     * @since 1.20.60
     */
    byte NONE = -1;
    /**
     * zlib raw
     */
    byte ZLIB = 0;
    /**
     * snappy raw
     */
    byte SNAPPY = 1;
}
