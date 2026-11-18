package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * 显式选择的可增长数组。容量不参与 Molang 的 count 和索引回绕。
 */
public final class MolangArray {
    private static final int MIN_CAPACITY = 16;

    @Nullable
    private float[] numbers;
    @Nullable
    private Object[] references;
    private int length;

    private MolangArray(int length, boolean numeric) {
        this.length = length;
        int capacity = Math.max(MIN_CAPACITY, length);
        if (numeric) {
            numbers = new float[capacity];
        } else {
            references = new Object[capacity];
        }
    }

    public static MolangArray numbers(float... values) {
        MolangArray array = new MolangArray(values.length, true);
        System.arraycopy(values, 0, array.numbers, 0, values.length);
        return array;
    }

    public static MolangArray references(Object... values) {
        MolangArray array = new MolangArray(values.length, false);
        for (int index = 0; index < values.length; index++) {
            array.references[index] = MolangValues.copy(values[index]);
        }
        return array;
    }

    public int length() {
        return length;
    }

    public boolean isNumeric() {
        return numbers != null;
    }

    @Nullable
    public Object get(int index) {
        if (length == 0) {
            return null;
        }
        int resolved = Math.max(0, index) % length;
        return numbers != null ? numbers[resolved] : references[resolved];
    }

    public float getNumber(int index) {
        if (length == 0) {
            return 0;
        }
        int resolved = Math.max(0, index) % length;
        return numbers != null ? numbers[resolved] : MolangValues.toFloat(references[resolved]);
    }

    public long getOperand(int index) {
        if (length == 0) {
            return 0;
        }
        int resolved = Math.max(0, index) % length;
        return numbers != null ? Integer.toUnsignedLong(Float.floatToRawIntBits(numbers[resolved]))
                : MolangValues.numericOperand(references[resolved]);
    }

    public void setNumber(int index, float value) {
        if (!grow(index)) {
            return;
        }
        if (numbers != null) {
            numbers[index] = value;
        } else {
            references[index] = value;
        }
    }

    public void set(int index, @Nullable Object value) {
        if (value instanceof Number number) {
            setNumber(index, number.floatValue());
            return;
        }
        if (value instanceof Boolean bool) {
            setNumber(index, bool ? 1 : 0);
            return;
        }
        int previousLength = length;
        if (!grow(index)) {
            return;
        }
        if (numbers != null) {
            Object[] promoted = new Object[numbers.length];
            for (int oldIndex = 0; oldIndex < previousLength; oldIndex++) {
                promoted[oldIndex] = numbers[oldIndex];
            }
            references = promoted;
            numbers = null;
        }
        references[index] = MolangValues.copy(value);
    }

    public MolangArray copy() {
        MolangArray result = new MolangArray(length, numbers != null);
        if (numbers != null) {
            System.arraycopy(numbers, 0, result.numbers, 0, length);
        } else {
            for (int index = 0; index < length; index++) {
                result.references[index] = MolangValues.copy(references[index]);
            }
        }
        return result;
    }

    private boolean grow(int index) {
        if (index < 0 || index == Integer.MAX_VALUE) {
            return false;
        }
        int required = index + 1;
        int capacity = numbers != null ? numbers.length : references.length;
        if (required > capacity) {
            long expanded = Math.max(required, capacity + (long) (capacity >>> 1));
            int nextCapacity = (int) Math.min(expanded, Integer.MAX_VALUE - 8L);
            if (nextCapacity < required) {
                return false;
            }
            if (numbers != null) {
                numbers = Arrays.copyOf(numbers, nextCapacity);
            } else {
                references = Arrays.copyOf(references, nextCapacity);
            }
        }
        length = Math.max(length, required);
        return true;
    }
}
