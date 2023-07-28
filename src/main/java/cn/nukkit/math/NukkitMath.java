package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitMath {

    /**
     * @deprecated use {@link Mth#floor(double)} instead
     */
    @Deprecated
    public static int floorDouble(double n) {
        return Mth.floor(n);
    }

    /**
     * @deprecated use {@link Mth#ceil(double)} instead
     */
    @Deprecated
    public static int ceilDouble(double n) {
        return Mth.ceil(n);
    }

    /**
     * @deprecated use {@link Mth#floor(float)} instead
     */
    @Deprecated
    public static int floorFloat(float n) {
        return Mth.floor(n);
    }

    /**
     * @deprecated use {@link Mth#ceil(float)} instead
     */
    @Deprecated
    public static int ceilFloat(float n) {
        return Mth.ceil(n);
    }

    /**
     * @deprecated use {@link NukkitRandom#nextRange()} instead
     */
    @Deprecated
    public static int randomRange(NukkitRandom random) {
        return random.nextRange();
    }

    /**
     * @deprecated use {@link NukkitRandom#nextRange(int)} instead
     */
    @Deprecated
    public static int randomRange(NukkitRandom random, int start) {
        return random.nextRange(start);
    }

    /**
     * @deprecated use {@link NukkitRandom#nextRange(int, int)} instead
     */
    @Deprecated
    public static int randomRange(NukkitRandom random, int start, int end) {
        return random.nextRange(start, end);
    }

    /**
     * @deprecated use {@link Math#round(double)} instead
     */
    @Deprecated
    public static double round(double d) {
        return Math.round(d);
    }

    public static double round(double d, int precision) {
        return ((double) Math.round(d * Math.pow(10, precision))) / Math.pow(10, precision);
    }

    /**
     * @deprecated use {@link Mth#clamp(double, double, double)} instead
     */
    @Deprecated
    public static double clamp(double value, double min, double max) {
        return Mth.clamp(value, min, max);
    }

    /**
     * @deprecated use {@link Mth#clamp(int, int, int)} instead
     */
    @Deprecated
    public static int clamp(int value, int min, int max) {
        return Mth.clamp(value, min, max);
    }

    /**
     * @deprecated use {@link Mth#clamp(float, float, float)} instead
     */
    @Deprecated
    public static float clamp(float value, float min, float max) {
        return Mth.clamp(value, min, max);
    }

    public static double getDirection(double diffX, double diffZ) {
        diffX = Math.abs(diffX);
        diffZ = Math.abs(diffZ);

        return Math.max(diffX, diffZ);
    }

}
