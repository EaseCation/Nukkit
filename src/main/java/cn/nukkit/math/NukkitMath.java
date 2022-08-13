package cn.nukkit.math;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitMath {

    /**
     * @deprecated use Mth
     */
    @Deprecated
    public static int floorDouble(double n) {
        return Mth.floor(n);
    }

    /**
     * @deprecated 返回结果与标准 Math.ceil 不一致, 例如 0.5
     */
    @Deprecated
    public static int ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    /**
     * @deprecated use Mth
     */
    @Deprecated
    public static int floorFloat(float n) {
        return Mth.floor(n);
    }

    /**
     * @deprecated 返回结果与标准 Math.ceil 不一致, 例如 0.5f
     */
    @Deprecated
    public static int ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int randomRange(NukkitRandom random) {
        return randomRange(random, 0);
    }

    public static int randomRange(NukkitRandom random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(NukkitRandom random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

    public static double round(double d) {
        return round(d, 0);
    }

    public static double round(double d, int precision) {
        return ((double) Math.round(d * Math.pow(10, precision))) / Math.pow(10, precision);
    }

    /**
     * @deprecated use Mth
     */
    @Deprecated
    public static double clamp(double value, double min, double max) {
        return Mth.clamp(value, min, max);
    }

    /**
     * @deprecated use Mth
     */
    @Deprecated
    public static int clamp(int value, int min, int max) {
        return Mth.clamp(value, min, max);
    }

    /**
     * @deprecated use Mth
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
