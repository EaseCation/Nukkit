package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

/**
 * 运行时独占数组的写入。新缓冲区由调用者直接接管，避免再经过通用值复制。
 */
final class MolangArrays {
    @Nullable
    static Object writeNumber(@Nullable Object array, int index, float value) {
        if (index < 0 || index == Integer.MAX_VALUE) {
            return array;
        }
        if (array instanceof MolangArray dynamic) {
            dynamic.setNumber(index, value);
            return dynamic;
        }
        if (array instanceof float[] values) {
            if (index < values.length) {
                values[index] = value;
                return values;
            }
            float[] expanded = new float[index + 1];
            System.arraycopy(values, 0, expanded, 0, values.length);
            expanded[index] = value;
            return expanded;
        }
        if (array == null) {
            float[] values = new float[Math.max(16, index + 1)];
            values[index] = value;
            return values;
        }
        return write(array, index, value);
    }

    @Nullable
    static Object write(@Nullable Object array, int index, @Nullable Object value) {
        if (index < 0 || index == Integer.MAX_VALUE) {
            return array;
        }
        if (array instanceof MolangArray dynamic) {
            dynamic.set(index, value);
            return dynamic;
        }
        if (array instanceof Object[] values && index < values.length) {
            values[index] = MolangValues.copy(value);
            return values;
        }
        boolean numeric = value instanceof Number || value instanceof Boolean;
        if (numeric && (array == null || array instanceof float[])) {
            return writeNumber(array, index, MolangValues.toFloat(value));
        }
        int oldLength = MolangValues.length(array);
        int newLength = Math.max(oldLength, index + 1);
        if (array == null) {
            newLength = Math.max(16, newLength);
        }
        Object[] expanded = new Object[newLength];
        for (int oldIndex = 0; oldIndex < oldLength; oldIndex++) {
            expanded[oldIndex] = MolangValues.copy(MolangValues.getElement(array, oldIndex));
        }
        expanded[index] = MolangValues.copy(value);
        return expanded;
    }

    private MolangArrays() {
    }
}
