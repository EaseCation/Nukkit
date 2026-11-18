package cn.nukkit.molang.runtime;

import cn.nukkit.math.Mth;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class MolangMath {
    /**
     * 对齐缓动使用的 float 角度和 sinf4 表；普通 Mth 的 double 角度表舍入不同。
     */
    private static final float[] ELASTIC_SINE = Utils.make(() -> {
        float[] table = new float[65536];
        for (int i = 0; i < table.length; i++) {
            float angle = i * 0x1.921fb6p-14f;
            float rounded = angle * 0.31830987334251404f + 12582912f;
            int sign = Float.floatToRawIntBits(rounded) << 31;
            float multiple = rounded - 12582912f;
            float reduced = angle - multiple * 3.140625f;
            reduced -= multiple * 0.0009675025939941406f;
            reduced -= multiple * 1.5099067240953445e-7f;
            reduced -= multiple * 5.126688136514179e-12f;
            float square = reduced * reduced;
            float signed = Float.intBitsToFloat(Float.floatToRawIntBits(reduced) ^ sign);
            float polynomial = 2.605780537123792e-6f * square - 0.00019809602235909551f;
            polynomial = polynomial * square + 0.008333062753081322f;
            polynomial = polynomial * square - 0.16666659712791443f;
            table[i] = signed + (square * polynomial) * signed;
        }
        return table;
    });

    /**
     * 使用 cvttss2si；NaN 和越界值产生整数不定值，非 Java 的饱和转换。
     */
    private static int toNativeInt(float value) {
        return value >= -0x1.0p31f && value < 0x1.0p31f ? (int) value : Integer.MIN_VALUE;
    }

    public static float abs(float x) {
        return Math.abs(x);
    }

    public static float acos(float x) {
        if (!(Math.abs(x) > 1.0005f)) {
            x = min(max(x, -1), 1);
        }
        return (float) Math.acos(x) * Mth.RAD_TO_DEG;
    }

    public static float asin(float x) {
        if (!(Math.abs(x) > 1.0005f)) {
            x = min(max(x, -1), 1);
        }
        return (float) Math.asin(x) * Mth.RAD_TO_DEG;
    }

    public static float atan(float x) {
        return (float) Math.atan(x) * Mth.RAD_TO_DEG;
    }

    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x) * Mth.RAD_TO_DEG;
    }

    public static float ceil(float x) {
        // 整数快路径将负小数截到正零，但保留负零输入。
        return x < 0 && x > -1 ? 0 : (float) Math.ceil(x);
    }

    public static float clamp(float value, float min, float max) {
        // 先检查上界，maxss 在相等或 NaN 时选择第二个操作数。
        return value > max ? max : max(value, min);
    }

    public static float cos(float degrees) {
        return (float) Math.cos(degrees * Mth.DEG_TO_RAD);
    }

    public static float copy_sign(float magnitude, float sign) {
        return Math.copySign(magnitude, sign);
    }

    public static float die_roll(float num, float low, float high) {
        return dieRollFromRandom(num, low, high, ThreadLocalRandom.current());
    }

    static float dieRollFromRandom(float num, float low, float high, RandomGenerator random) {
        int n = toNativeInt(num);
        if (n <= 0) {
            return 0;
        }
        low = (float) Math.floor(low);
        high = (float) Math.floor(high);
        if (low > high) {
            float temporary = low;
            low = high;
            high = temporary;
        }
        float total = 0;
        for (int i = 0; i < n; i++) {
            float sample = clampRandomSample(random.nextFloat());
            total += (1 - sample) * low + sample * high;
        }
        return total;
    }

    public static float die_roll_integer(float num, float low, float high) {
        return dieRollIntegerFromRandom(num, low, high, ThreadLocalRandom.current());
    }

    static float dieRollIntegerFromRandom(float num, float low, float high, RandomGenerator random) {
        int n = toNativeInt(num);
        if (n <= 0) {
            return 0;
        }
        low = (float) Math.floor(low);
        high = (float) Math.floor(high);
        if (low > high) {
            float temporary = low;
            low = high;
            high = temporary;
        }
        float top = (1 - high * 0x1.0p-23f) + high;
        float total = 0;
        for (int i = 0; i < n; i++) {
            float sample = clampRandomSample(random.nextFloat());
            float value = (float) Math.floor((1 - sample) * low + sample * top);
            total += value > high ? high : value > low ? value : low;
        }
        return total;
    }

    public static float exp(float x) {
        return (float) Math.exp(x);
    }

    public static float floor(float x) {
        return (float) Math.floor(x);
    }

    public static float hermite_blend(float t) {
        return 3 * t * t - 2 * t * t * t;
    }

    public static float inverse_lerp(float min, float max, float value) {
        return Mth.inverseLerp(value, min, max);
    }

    public static float lerp(float start, float end, float t) {
        return Mth.lerp(t, start, end);
    }

    public static float lerprotate(float start, float end, float t) {
        return start + min_angle(end - start) * t;
    }

    public static float ln(float x) {
        return (float) Math.log(x);
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float min_angle(float degrees) {
        float result = (degrees + 180) % 360;
        if (result < 0) result += 360;
        return result - 180;
    }

    public static float mod(float a, float b) {
        if (b == 0 || Float.isNaN(b)) return 0;
        // 最终加零会将负零余数转为正零。
        return a % b + 0f;
    }

    public static float pi() {
        return Mth.PI;
    }

    public static float pow(float base, float exponent) {
        if (base == 1 || base == -1 && Float.isInfinite(exponent)) return 1;
        return (float) Math.pow(base, exponent);
    }

    public static float random(float low, float high) {
        return randomFromSample(low, high, ThreadLocalRandom.current().nextFloat());
    }

    public static float random_integer(float low, float high) {
        return randomIntegerFromSample(low, high, ThreadLocalRandom.current().nextFloat());
    }

    static float randomFromSample(float low, float high, float sample) {
        float min = low < high ? low : high;
        float max = low > high ? low : high;
        sample = clampRandomSample(sample);
        // 使用动态参数插值，避免端点相减溢出；常量范围特化具有不同的舍入顺序。
        return (1 - sample) * min + sample * max;
    }

    static float randomIntegerFromSample(float low, float high, float sample) {
        float min = low < high ? low : high;
        float max = low > high ? low : high;
        sample = clampRandomSample(sample);
        // 保持 float 运算顺序和原始小数端点，不能替换成整数跨度抽样。
        float top = (1 - max * 0x1.0p-23f) + max;
        float value = (float) Math.floor(top * sample + (1 - sample) * min);
        return value > max ? max : min > value ? min : value;
    }

    private static float clampRandomSample(float sample) {
        // 对齐 SSE max/min 的 NaN 和负零处理。
        return sample > 0 ? sample < 1 ? sample : 1 : 0;
    }

    public static float round(float x) {
        // roundf 的半整数远离零；使用 double 加法避免半整数前一个 float 提前进位。
        return Math.copySign((float) Math.floor(Math.abs((double) x) + 0.5), x);
    }

    public static float sign(float x) {
        return x >= 0 ? 1 : -1;
    }

    public static float sin(float degrees) {
        return (float) Math.sin(degrees * Mth.DEG_TO_RAD);
    }

    public static float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public static float trunc(float x) {
        return x < 0 ? (float) Math.ceil(x) : (float) Math.floor(x);
    }

    public static float ease_in_sine(float start, float end, float t) {
        int phase = toNativeInt((t + 1) * 16384) & 0xffff;
        return (1 - ELASTIC_SINE[phase]) * (end - start) + start;
    }

    public static float ease_out_sine(float start, float end, float t) {
        int phase = toNativeInt(t * 16384) & 0xffff;
        return (end - start) * ELASTIC_SINE[phase] + start;
    }

    public static float ease_in_out_sine(float start, float end, float t) {
        int phase = toNativeInt(t * 32768 + 16384) & 0xffff;
        return start - ((ELASTIC_SINE[phase] - 1) * (end - start)) * 0.5f;
    }

    public static float ease_in_quad(float start, float end, float t) {
        return ((end - start) * t) * t + start;
    }

    public static float ease_out_quad(float start, float end, float t) {
        return start - ((end - start) * t) * (t - 2);
    }

    public static float ease_in_out_quad(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return (doubled * halfDistance) * doubled + start;
        }
        return start - ((doubled - 3) * (doubled - 1) - 1) * halfDistance;
    }

    public static float ease_in_cubic(float start, float end, float t) {
        return ((end - start) * t) * (t * t) + start;
    }

    public static float ease_out_cubic(float start, float end, float t) {
        float shifted = t - 1;
        return ((shifted * shifted) * shifted + 1) * (end - start) + start;
    }

    public static float ease_in_out_cubic(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return (doubled * doubled) * (doubled * halfDistance) + start;
        }
        float shifted = doubled - 2;
        return ((shifted * shifted) * shifted + 2) * halfDistance + start;
    }

    public static float ease_in_quart(float start, float end, float t) {
        return ((end - start) * t) * ((t * t) * t) + start;
    }

    public static float ease_out_quart(float start, float end, float t) {
        float shifted = t - 1;
        float square = shifted * shifted;
        return start - (square * square - 1) * (end - start);
    }

    public static float ease_in_out_quart(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return ((doubled * doubled) * doubled) * (doubled * halfDistance) + start;
        }
        float shifted = doubled - 2;
        float square = shifted * shifted;
        return start - (square * square - 2) * halfDistance;
    }

    public static float ease_in_quint(float start, float end, float t) {
        float square = t * t;
        return ((end - start) * square) * (square * t) + start;
    }

    public static float ease_out_quint(float start, float end, float t) {
        float shifted = t - 1;
        float square = shifted * shifted;
        return ((square * shifted) * square + 1) * (end - start) + start;
    }

    public static float ease_in_out_quint(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return ((halfDistance * doubled) * doubled) * ((doubled * doubled) * doubled) + start;
        }
        float shifted = doubled - 2;
        float square = shifted * shifted;
        return ((square * shifted) * square + 2) * halfDistance + start;
    }

    public static float ease_in_expo(float start, float end, float t) {
        // 指数曲线在端点也求值，不额外强制返回 start 或 end。
        return (float) Math.pow(2, (t - 1) * 10) * (end - start) + start;
    }

    public static float ease_out_expo(float start, float end, float t) {
        return (1 - (float) Math.pow(2, t * -10)) * (end - start) + start;
    }

    public static float ease_in_out_expo(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        float shifted = doubled - 1;
        if (doubled < 1) {
            return (float) Math.pow(2, shifted * 10) * halfDistance + start;
        }
        return (2 - (float) Math.pow(2, shifted * -10)) * halfDistance + start;
    }

    public static float ease_in_circ(float start, float end, float t) {
        return start - ((float) Math.sqrt(1 - t * t) - 1) * (end - start);
    }

    public static float ease_out_circ(float start, float end, float t) {
        float shifted = t - 1;
        return (float) Math.sqrt(1 - shifted * shifted) * (end - start) + start;
    }

    public static float ease_in_out_circ(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return start - ((float) Math.sqrt(1 - doubled * doubled) - 1) * halfDistance;
        }
        float shifted = doubled - 2;
        return ((float) Math.sqrt(1 - shifted * shifted) + 1) * halfDistance + start;
    }

    public static float ease_in_back(float start, float end, float t) {
        return ((end - start) * t) * ((t * 2.70158f - 1.70158f) * t) + start;
    }

    public static float ease_out_back(float start, float end, float t) {
        float shifted = t - 1;
        return ((shifted * shifted) * (shifted * 2.70158f + 1.70158f) + 1) * (end - start) + start;
    }

    public static float ease_in_out_back(float start, float end, float t) {
        float doubled = t + t;
        float halfDistance = (end - start) * 0.5f;
        if (doubled < 1) {
            return ((doubled * 3.5949094f - 2.5949094f) * doubled) * (doubled * halfDistance) + start;
        }
        float shifted = doubled - 2;
        return ((shifted * 3.5949094f + 2.5949094f) * (shifted * shifted) + 2) * halfDistance + start;
    }

    public static float ease_in_bounce(float start, float end, float t) {
        float distance = end - start;
        return distance - ease_out_bounce(0, distance, 1 - t) + start;
    }

    public static float ease_out_bounce(float start, float end, float t) {
        float distance = end - start;
        if (t < 0.3636363744735718f) {
            // 第一段先乘区间幅度，再乘二次项；保持浮点求值顺序。
            return (t * distance) * (t * 7.5625f) + start;
        }
        float offset;
        if (t < 0.7272727489471436f) {
            t -= 0.5454545617103577f;
            offset = 0.75f;
        } else if (t < 0.9090909361839294f) {
            t -= 0.8181818127632141f;
            offset = 0.9375f;
        } else {
            t -= 0.9545454382896423f;
            offset = 0.984375f;
        }
        return ((t * 7.5625f) * t + offset) * distance + start;
    }

    public static float ease_in_out_bounce(float start, float end, float t) {
        float distance = end - start;
        float doubled = t + t;
        if (t < 0.5f) {
            return (distance - ease_out_bounce(0, distance, 1 - doubled)) * 0.5f + start;
        }
        return (ease_out_bounce(0, distance, doubled - 1) + distance) * 0.5f + start;
    }

    public static float ease_in_elastic(float start, float end, float t) {
        if (t == 0 || Float.isNaN(t)) {
            return start;
        }
        if (t == 1) {
            return end;
        }
        float shifted = t - 1;
        float amplitude = (float) Math.pow(2, shifted * 10) * (end - start);
        int phase = toNativeInt((shifted - 0.075f) * 218453.328125f) & 0xffff;
        return start - amplitude * ELASTIC_SINE[phase];
    }

    public static float ease_out_elastic(float start, float end, float t) {
        if (t == 0 || Float.isNaN(t)) {
            return start;
        }
        if (t == 1) {
            return end;
        }
        float amplitude = (float) Math.pow(2, t * -10) * (end - start);
        int phase = toNativeInt((t - 0.075f) * 218453.328125f) & 0xffff;
        return amplitude * ELASTIC_SINE[phase] + end;
    }

    public static float ease_in_out_elastic(float start, float end, float t) {
        if (t == 0 || Float.isNaN(t)) {
            return start;
        }
        float doubled = t + t;
        if (doubled == 2) {
            return end;
        }
        float shifted = doubled - 1;
        float angle = (shifted - 0.075f) * 20.943950653076172f;
        int phase = toNativeInt(angle * 10430.3779296875f) & 0xffff;
        float sine = ELASTIC_SINE[phase] * 0.5f;
        float amplitude = (float) Math.pow(2, shifted * (doubled < 1 ? 10 : -10)) * (end - start);
        return doubled < 1 ? start - amplitude * sine : amplitude * sine + end;
    }

    /**
     * netease only
     */
    public static float and(float left, float right) {
        return toNativeInt(left) & toNativeInt(right);
    }

    /**
     * netease only
     */
    public static float or(float left, float right) {
        return toNativeInt(left) | toNativeInt(right);
    }

    public static void initialize() {
        // NOOP
    }

    private MolangMath() {
    }
}
