package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NibbleArray implements Cloneable {
    private byte[] data;

    public NibbleArray(int length) {
        data = new byte[length / 2];
    }

    public NibbleArray(byte[] array) {
        data = array;
    }

    public byte get(int index) {
        Preconditions.checkElementIndex(index, data.length * 2);
        byte val = data[index / 2];
        if ((index & 1) == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >>> 4);
        }
    }

    public void set(int index, byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        Preconditions.checkElementIndex(index, data.length * 2);
        value &= 0xf;
        int half = index / 2;
        byte previous = data[half];
        if ((index & 1) == 0) {
            data[half] = (byte) (previous & 0xf0 | value);
        } else {
            data[half] = (byte) (previous & 0x0f | value << 4);
        }
    }

    public void fill(byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        value &= 0xf;
        Arrays.fill(data, (byte) ((value << 4) | value));
    }

    public void fillMin() {
        Arrays.fill(data, (byte) 0x00);
    }

    public void fillMax() {
        Arrays.fill(data, (byte) 0xff);
    }

    public void copyFrom(byte[] bytes) {
        Preconditions.checkNotNull(bytes, "bytes");
        Preconditions.checkArgument(bytes.length == data.length, "length of provided byte array is %s but expected %s", bytes.length,
                data.length);
        System.arraycopy(bytes, 0, data, 0, data.length);
    }

    public void copyFrom(NibbleArray array) {
        Preconditions.checkNotNull(array, "array");
        copyFrom(array.data);
    }

    public byte[] getData() {
        return data;
    }

    public NibbleArray copy() {
        return new NibbleArray(getData().clone());
    }

    @Override
    public NibbleArray clone() {
        try {
            NibbleArray clone = (NibbleArray) super.clone();
            clone.data = this.data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
