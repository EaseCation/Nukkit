package cn.nukkit.math;

import java.util.Random;

/**
 * @deprecated use {@link Mth} instead
 */
@Deprecated
public class MathHelper {

    private MathHelper() {
    }

    public static float sqrt(float paramFloat) {
        return (float) Math.sqrt(paramFloat);
    }

    public static float sin(float paramFloat) {
        return Mth.sin(paramFloat);
    }

    public static float cos(float paramFloat) {
        return Mth.cos(paramFloat);
    }

    public static float sin(double paramFloat) {
        return Mth.sin((float) paramFloat);
    }

    public static float cos(double paramFloat) {
        return Mth.cos((float) paramFloat);
    }

    public static int floor(double d0) {
        return Mth.floor(d0);
    }

    public static long floor_double_long(double d) {
        return Mth.lfloor(d);
    }

    public static int floor_float_int(float f) {
        return Mth.floor(f);
    }

    public static int log2(int bits) {
        return Mth.log2PowerOfTwo(bits);
    }

    /**
     * Returns a random number between min and max, inclusive.
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return A random number between min and max, inclusive.
     */
    public static int getRandomNumberInRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static int ceil(float floatNumber) {
        return Mth.ceil(floatNumber);
    }

    public static int clamp(int check, int min, int max) {
        return Mth.clamp(check, min, max);
    }

    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
        return Mth.clampedLerp(lowerBnd, upperBnd, slide);
    }

    public static float denormalizeClamp(float lowerBnd, float upperBnd, float slide) {
        return Mth.clampedLerp(lowerBnd, upperBnd, slide);
    }
}
