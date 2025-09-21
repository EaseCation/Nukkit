package cn.nukkit.math;

import java.util.function.IntPredicate;

public final class Mth {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = PI * 2;
    public static final float HALF_PI = PI / 2;
    public static final float DEG_TO_RAD = PI / 180;
    public static final float RAD_TO_DEG = 180 / PI;
    public static final float E = (float) Math.E;
    public static final float EPSILON = 1.0E-5f;
    public static final float EPSILON_NORMAL_SQRT = 1.0E-15f;
    public static final float SQRT_OF_TWO = (float) Math.sqrt(2);
    public static final float SQRT_OF_THREE = (float) Math.sqrt(3);
    private static final double LOG_OF_TWO = Math.log(2);

    private static final float[] SIN = new float[65536];
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
            0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
            31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9,
    };
    private static final double FRAC_BIAS = Double.longBitsToDouble(0x42b0000000000000L);
    private static final int LUT_SIZE = 257;
    private static final double[] ASIN_TAB = new double[LUT_SIZE];
    private static final double[] COS_TAB = new double[LUT_SIZE];

    static {
        for (int i = 0; i < SIN.length; i++) {
            SIN[i] = (float) Math.sin(i * Math.PI * 2 / 65536);
        }
        for (int i = 0; i < LUT_SIZE; i++) {
            double asin = Math.asin(i / 256.0);
            COS_TAB[i] = Math.cos(asin);
            ASIN_TAB[i] = asin;
        }
    }

    public static float sin(float v) {
        return SIN[(int) (v * 10430.378f) & Character.MAX_VALUE];
    }

    public static double sin(double v) {
        return SIN[(int) (v * 10430.378) & Character.MAX_VALUE];
    }

    public static float cos(float v) {
        return SIN[(int) (v * 10430.378f + 16384) & Character.MAX_VALUE];
    }

    public static double cos(double v) {
        return SIN[(int) (v * 10430.378 + 16384) & Character.MAX_VALUE];
    }

    public static int floor(float v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }

    public static int floor(double v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }

    public static long lfloor(double v) {
        long l = (long) v;
        return v < l ? l - 1 : l;
    }

    public static int ceil(float v) {
        int i = (int) v;
        return v > i ? i + 1 : i;
    }

    public static int ceil(double v) {
        int i = (int) v;
        return v > i ? i + 1 : i;
    }

    public static byte clamp(byte v, byte min, byte max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static short clamp(short v, short min, short max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static char clamp(char v, char min, char max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static int clamp(int v, int min, int max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static long clamp(long v, long min, long max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static float clamp(float v, float min, float max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static double clamp(double v, double min, double max) {
        return v < min ? min :
                v > max ? max : v;
    }

    public static double clampedLerp(double min, double max, double v) {
        return v < 0 ? min :
                v > 1 ? max : lerp(v, min, max);
    }

    public static float clampedLerp(float min, float max, float v) {
        return v < 0 ? min :
                v > 1 ? max : lerp(v, min, max);
    }

    public static double absMax(double a, double b) {
        if (a < 0) {
            a = -a;
        }
        if (b < 0) {
            b = -b;
        }
        return a > b ? a : b;
    }

    public static boolean equal(float a, float b) {
        return Math.abs(b - a) < EPSILON;
    }

    public static boolean equal(double a, double b) {
        return Math.abs(b - a) < 9.999999747378752E-6;
    }

    public static float positiveModulo(float x, float y) {
        return (x % y + y) % y;
    }

    public static double positiveModulo(double x, double y) {
        return (x % y + y) % y;
    }

    public static int wrapDegrees(int v) {
        v %= 360;
        if (v >= 180) {
            v -= 360;
        }
        if (v < -180) {
            v += 360;
        }
        return v;
    }

    public static float wrapDegrees(float v) {
        v %= 360;
        if (v >= 180) {
            v -= 360;
        }
        if (v < -180) {
            v += 360;
        }
        return v;
    }

    public static double wrapDegrees(double v) {
        v %= 360;
        if (v >= 180) {
            v -= 360;
        }
        if (v < -180) {
            v += 360;
        }
        return v;
    }

    public static float degreesDifference(float a, float b) {
        return wrapDegrees(b - a);
    }

    public static float degreesDifferenceAbs(float a, float b) {
        return Math.abs(degreesDifference(a, b));
    }

    public static float rotateIfNecessary(float a, float b, float n) {
        return b - clamp(degreesDifference(a, b), -n, n);
    }

    public static float approach(float a, float b, float v) {
        v = Math.abs(v);
        return a < b ? clamp(a + v, a, b) : clamp(a - v, b, a);
    }

    public static float approachDegrees(float a, float b, float v) {
        return approach(a, a + degreesDifference(a, b), v);
    }

    public static int smallestEncompassingPowerOfTwo(int v) {
        if (v == 0) {
            return 1;
        }
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        return (v | v >> 16) + 1;
    }

    public static long smallestEncompassingPowerOfTwo(long v) {
        if (v == 0) {
            return 1;
        }
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return (v | v >> 32) + 1;
    }

    public static boolean isPowerOfTwo(int v) {
        return v != 0 && (v & v - 1) == 0;
    }

    public static int ceillog2(int v) {
        v = isPowerOfTwo(v) ? v : smallestEncompassingPowerOfTwo(v);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) (v * 125613361L >> 27) & 0x1f];
    }

    public static int log2(int v) {
//        int i = ceillog2(v);
//        return isPowerOfTwo(v) ? i : i - 1;
        return (int) (Math.log(v) / LOG_OF_TWO);
    }

    public static int log2PowerOfTwo(int powerOfTwo) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(powerOfTwo);
    }

    public static float frac(float v) {
        return v - (float) floor(v);
    }

    public static double frac(double v) {
        return v - (double) lfloor(v);
    }

    public static int getSeed(BlockVector3 vec) {
        return getSeed(vec.getX(), vec.getY(), vec.getZ());
    }

    public static int getSeed(int x, int y, int z) {
        long xord = (x * 0x2fc20fL) ^ z * 0x6ebfff5L ^ (long) y;
        return (int) ((xord * 0x285b825 + 0xb) * xord);
    }

    public static double inverseLerp(double v, double a, double b) {
        return (v - a) / (b - a);
    }

    public static float inverseLerp(float v, float a, float b) {
        return (v - a) / (b - a);
    }

    public static double atan2(double dy, double dx) {
        double square = dx * dx + dy * dy;

        if (Double.isNaN(square)) {
            return Double.NaN;
        }

        boolean ny = dy < 0;
        if (ny) {
            dy = -dy;
        }

        boolean nx = dx < 0;
        if (nx) {
            dx = -dx;
        }

        boolean yg = dy > dx;
        if (yg) {
            double t = dx;
            dx = dy;
            dy = t;
        }

        double s = fastInvSqrt(square);
        dx *= s;
        dy *= s;
        double b = FRAC_BIAS + dy;
        int i = (int) Double.doubleToRawLongBits(b);
        double asin = ASIN_TAB[i];
        double e = dy * COS_TAB[i] - dx * (b - FRAC_BIAS);
        double r = asin + (6 + e * e) * e * (1 / 6d);

        if (yg) {
            r = Math.PI / 2 - r;
        }
        if (nx) {
            r = Math.PI - r;
        }
        if (ny) {
            r = -r;
        }
        return r;
    }

    static float fastInvSqrt(float v) {
        float h = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 0x5f375a86 - (i >> 1);
        v = Float.intBitsToFloat(i);
        v *= 1.5f - h * v * v;
        return v;
    }

    static double fastInvSqrt(double v) {
        double h = 0.5 * v;
        long l = Double.doubleToRawLongBits(v);
        l = 0x5fe6eb50c7b537a9L - (l >> 1);
        v = Double.longBitsToDouble(l);
        v *= 1.5 - h * v * v;
        return v;
    }

    public static int binarySearch(int f, int t, IntPredicate p) {
        int i = t - f;
        while (i > 0) {
            int m = i / 2;
            int v = f + m;
            if (p.test(v)) {
                i = m;
            } else {
                f = v + 1;
                i -= m + 1;
            }
        }
        return f;
    }

    public static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    public static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    public static float lerp2(float t0, float t, float a0, float b0, float a1, float b1) {
        return lerp(t, lerp(t0, a0, b0), lerp(t0, a1, b1));
    }

    public static double lerp2(double t0, double t, double a0, double b0, double a1, double b1) {
        return lerp(t, lerp(t0, a0, b0), lerp(t0, a1, b1));
    }

    public static float lerp3(float t0, float t1, float t, float a0, float b0, float a1, float b1, float a2, float b2, float a3, float b3) {
        return lerp(t, lerp2(t0, t1, a0, b0, a1, b1), lerp2(t0, t1, a2, b2, a3, b3));
    }

    public static double lerp3(double t0, double t1, double t, double a0, double b0, double a1, double b1, double a2, double b2, double a3, double b3) {
        return lerp(t, lerp2(t0, t1, a0, b0, a1, b1), lerp2(t0, t1, a2, b2, a3, b3));
    }

    public static float smoothstep(float v) {
        return v * v * v * (v * (v * 6 - 15) + 10);
    }

    public static double smoothstep(double v) {
        return v * v * v * (v * (v * 6 - 15) + 10);
    }

    public static float smoothstepDerivative(float v) {
        return 30 * v * v * (v - 1) * (v - 1);
    }

    public static double smoothstepDerivative(double v) {
        return 30 * v * v * (v - 1) * (v - 1);
    }

    public static int sign(float v) {
        return v == 0 ? 0 :
                v > 0 ? 1 : -1;
    }

    public static int sign(double v) {
        return v == 0 ? 0 :
                v > 0 ? 1 : -1;
    }

    public static float rotLerp(float t, float a, float b) {
        return a + t * wrapDegrees(b - a);
    }

    /*@Deprecated
    public static float rotlerp(float a, float b, float t) {
//        b -= a;
//        while (b < 180) {
//            b += 360;
//        }
//        while (b >= 180) {
//            b -= 360;
//        }
//        return a + t * b;
        return rotLerp(t, a, b);
    }*/

    public static float square(float v) {
        return v * v;
    }

    public static double square(double v) {
        return v * v;
    }

    public static int square(int v) {
        return v * v;
    }

    public static long square(long v) {
        return v * v;
    }

    public static float cube(float v) {
        return v * v * v;
    }

    public static double cube(double v) {
        return v * v * v;
    }

    public static int cube(int v) {
        return v * v * v;
    }

    public static long cube(long v) {
        return v * v * v;
    }

    public static float clampedMap(float t, float n, float m, float a, float b) {
        return clampedLerp(a, b, inverseLerp(t, n, m));
    }

    public static double clampedMap(double t, double n, double m, double a, double b) {
        return clampedLerp(a, b, inverseLerp(t, n, m));
    }

    public static float map(float t, float n, float m, float a, float b) {
        return lerp(inverseLerp(t, n, m), a, b);
    }

    public static double map(double t, double n, double m, double a, double b) {
        return lerp(inverseLerp(t, n, m), a, b);
    }

    public static int positiveCeilDiv(int x, int y) {
        return -Math.floorDiv(-x, y);
    }

    public static float length(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static double length(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static int quantize(float a, int b) {
        return floor(a / (float) b) * b;
    }

    public static int quantize(double a, int b) {
        return floor(a / (double) b) * b;
    }

    public static double round(double a, int precision) {
        return Math.round(a * Math.pow(10, precision)) / Math.pow(10, precision);
    }

    private Mth() {
        throw new IllegalStateException();
    }
}
