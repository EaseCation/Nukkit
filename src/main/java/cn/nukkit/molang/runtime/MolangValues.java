package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 编译后的表达式共用的值转换与复合值操作，不依赖反射或表达式解析。
 */
public final class MolangValues {
    public static final long STRING_OPERAND = 1L << 32;
    static final long MISSING_OPERAND = 1L << 33;

    static final Object[] EMPTY_ARGUMENTS = {};

    public static float toFloat(@Nullable Object value) {
        if (value instanceof Number number) {
            return number.floatValue();
        }
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    /**
     * 低 32 位保存浮点原始位，bit 32 标记不能参与数值算术的字符串。
     */
    public static long numericOperand(@Nullable Object value) {
        if (value instanceof String) {
            return STRING_OPERAND;
        }
        return Integer.toUnsignedLong(Float.floatToRawIntBits(toFloat(value)));
    }

    static long equalityBits(@Nullable Object value) {
        // 无符号浮点位保存可立即读取的值；NaN 与任何数都不相等，负数标记延迟转换。
        if (value instanceof Float number) {
            return Integer.toUnsignedLong(Float.floatToRawIntBits(number));
        }
        if (value instanceof Number) {
            return -1;
        }
        if (value == null) {
            return 0;
        }
        if (value instanceof Boolean flag) {
            return flag ? Float.floatToRawIntBits(1) : 0;
        }
        return Float.floatToRawIntBits(Float.NaN);
    }

    @SuppressWarnings("unused")
    public static boolean equalSnapshot(long bits, @Nullable Object deferred, float number) {
        return bits >= 0 ? Float.intBitsToFloat((int) bits) == number : equalNumber(deferred, number);
    }

    public static boolean equalNumber(@Nullable Object value, float number) {
        if (value instanceof Number numeric) {
            return numeric.floatValue() == number;
        }
        if (value == null) {
            return number == 0;
        }
        return value instanceof Boolean bool && number == (bool ? 1 : 0);
    }

    public static boolean equal(@Nullable Object left, @Nullable Object right) {
        if (left instanceof String || right instanceof String) {
            return left instanceof String && left.equals(right);
        }
        if ((left == null || left instanceof Number || left instanceof Boolean)
                && (right == null || right instanceof Number || right instanceof Boolean)) {
            return toFloat(left) == toFloat(right);
        }
        return left == right;
    }

    @SuppressWarnings("unused")
    @Nullable
    public static Object getMember(@Nullable Object value, String name) {
        return value instanceof MolangStruct struct ? struct.get(name) : null;
    }

    @SuppressWarnings("unused")
    @Nullable
    public static Object getMember(@Nullable Object value, MolangPath path) {
        return value instanceof MolangStruct struct ? struct.get(path) : null;
    }

    @SuppressWarnings("unused")
    public static void setMember(@Nullable Object value, String name, @Nullable Object member) {
        if (value instanceof MolangStruct struct) {
            struct.set(name, member);
        }
    }

    @SuppressWarnings("unused")
    public static void setMember(@Nullable Object value, MolangPath path, @Nullable Object member) {
        if (value instanceof MolangStruct struct) {
            struct.set(path, member);
        }
    }

    @Nullable
    public static MolangRuntime asRuntime(@Nullable Object value) {
        return value instanceof MolangEntityReference reference ? reference.resolve() : null;
    }

    @SuppressWarnings("unused")
    public static boolean isResolved(@Nullable Object value) {
        return value != null && (!(value instanceof MolangEntityReference reference) || reference.resolve() != null);
    }

    /**
     * 仅预读内建运行时的普通变量。容器复制、外部 Number 转换和实体引用解析都可能调用宿主代码，
     * 遇到这些值时由原程序按源语句顺序执行。
     */
    @SuppressWarnings("unused")
    public static boolean hasScalarVariables(MolangRuntime runtime, MolangBindingPlan inputs) {
        if (!isBuiltinStorage(runtime)) {
            return false;
        }
        for (int index = 0; index < inputs.pathCount(); index++) {
            if (!isScalarInput(runtime.getVariableValue(inputs.path(index)))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static boolean isScalarInput(@Nullable Object value) {
        return value == null || value instanceof Float || value instanceof String;
    }

    @SuppressWarnings("unused")
    public static void copyVariableToElement(MolangRuntime runtime, MolangPath target, MolangPath index, MolangPath source) {
        if (runtime instanceof MolangBoundRuntime bound && bound.copyNumericVariableToElement(target, index, source)) {
            return;
        }
        Object offset = runtime.getVariableValue(index);
        Object value = runtime.getVariableValue(source);
        runtime.setVariableElementValue(target, (int) toFloat(offset), value);
    }

    @SuppressWarnings("unused")
    public static void finishOwnedIteration(MolangRuntime runtime, MolangPath path, float value) {
        MolangRuntime storage = runtime instanceof MolangBoundRuntime view ? view.delegate : runtime;
        if (storage instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner && owner.evaluationDepth() > 0) {
            owner.tempChanged();
        } else {
            runtime.setTempNumber(path, value);
        }
    }

    public static boolean isBuiltinStorage(MolangRuntime runtime) {
        return runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl || runtime instanceof MolangBoundRuntime;
    }

    public static int length(@Nullable Object value) {
        return switch (value) {
            case MolangArray values -> values.length();
            case Object[] values -> values.length;
            case float[] values -> values.length;
            case List<?> values -> values.size();
            case null, default -> 0;
        };
    }

    public static boolean isArray(@Nullable Object value) {
        return value instanceof MolangArray || value instanceof Object[] || value instanceof float[] || value instanceof List<?>;
    }

    @SuppressWarnings("unused")
    public static boolean isNumericArray(@Nullable Object value) {
        return value instanceof float[] || value instanceof MolangArray array && array.isNumeric();
    }

    @Nullable
    public static Object getElement(@Nullable Object value, int index) {
        if (value instanceof MolangArray array) {
            return array.get(index);
        }
        int size = length(value);
        if (size == 0) {
            return null;
        }
        int resolved = wrapIndex(index, size);
        return switch (value) {
            case Object[] values -> values[resolved];
            case float[] values -> values[resolved];
            case List<?> values -> values.get(resolved);
            case null, default -> null;
        };
    }

    public static float getElementFloat(@Nullable Object value, int index) {
        if (value instanceof MolangArray values) {
            return values.getNumber(index);
        }
        if (value instanceof float[] values) {
            return values.length == 0 ? 0 : values[wrapIndex(index, values.length)];
        }
        return toFloat(getElement(value, index));
    }

    public static long getElementOperand(@Nullable Object value, int index) {
        if (value instanceof MolangArray values) {
            return values.getOperand(index);
        }
        if (value instanceof float[] values) {
            return values.length == 0 ? 0 : Integer.toUnsignedLong(Float.floatToRawIntBits(values[wrapIndex(index, values.length)]));
        }
        return numericOperand(getElement(value, index));
    }

    public static void setElement(@Nullable Object value, int index, @Nullable Object element) {
        int size = length(value);
        if (size == 0) {
            return;
        }
        int resolved = wrapIndex(index, size);
        if (value instanceof MolangArray values) {
            values.set(resolved, element);
        } else if (value instanceof Object[] values) {
            values[resolved] = copy(element);
        } else if (value instanceof float[] values) {
            values[resolved] = toFloat(element);
        }
    }

    @Nullable
    public static Object copy(@Nullable Object value) {
        if (value instanceof Float) {
            return value;
        }
        if (value instanceof MolangStruct struct) {
            return struct.copy();
        }
        if (value instanceof MolangArray array) {
            return array.copy();
        }
        if (value instanceof float[] values) {
            return values.clone();
        }
        if (value instanceof Object[] values) {
            Object[] result = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = copy(values[i]);
            }
            return result;
        }
        if (value instanceof List<?> values) {
            Object[] result = values.toArray();
            for (int i = 0; i < result.length; i++) {
                result[i] = copy(result[i]);
            }
            return result;
        }
        if (value instanceof Boolean bool) {
            return bool ? 1f : 0f;
        }
        if (value instanceof Number number) {
            return number.floatValue();
        }
        return value;
    }

    static int wrapIndex(int index, int length) {
        if (index < length) {
            return Math.max(0, index);
        }
        int wrapped = index - length;
        return wrapped < length ? wrapped : index % length;
    }

    private MolangValues() {
    }
}
