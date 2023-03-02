package cn.nukkit.utils;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import lombok.ToString;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static cn.nukkit.entity.data.Skin.*;

@ToString(exclude = {"data"})
public class SerializedImage {
    public static final SerializedImage EMPTY = new SerializedImage(0, 0, new byte[0]);

    public final int width;
    public final int height;
    public final byte[] data;

    public SerializedImage(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static SerializedImage fromLegacy(byte[] skinData) {
        Objects.requireNonNull(skinData, "skinData");
        switch (skinData.length) {
            case 0:
                return SerializedImage.EMPTY;
            case SINGLE_SKIN_SIZE:
                return new SerializedImage(64, 32, skinData);
            case DOUBLE_SKIN_SIZE:
                return new SerializedImage(64, 64, skinData);
            case SKIN_128_64_SIZE:
                return new SerializedImage(128, 64, skinData);
            case SKIN_128_128_SIZE:
                return new SerializedImage(128, 128, skinData);
        }
        throw new IllegalArgumentException("Unknown legacy skin size");
    }

    public static SerializedImage fromBufferedImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] data = new byte[width * height * 4];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                data[index++] = (byte) ((rgb >> 16) & 0xFF);
                data[index++] = (byte) ((rgb >> 8) & 0xFF);
                data[index++] = (byte) (rgb & 0xFF);
                data[index++] = (byte) ((rgb >> 24) & 0xFF);
            }
        }
        return new SerializedImage(width, height, data);
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_4BYTE_ABGR);
        FastByteArrayInputStream stream = new FastByteArrayInputStream(data);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = stream.read();
                int g = stream.read();
                int b = stream.read();
                int a = stream.read();
                image.setRGB(x, y,
                        ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8)  |
                        ((b & 0xFF))
                );
            }
        }
        return image;
    }

}
