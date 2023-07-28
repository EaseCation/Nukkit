package cn.nukkit.math;

public class MarsagliaPolarGaussian {
    private final RandomSource randomSource;

    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public MarsagliaPolarGaussian(RandomSource random) {
        randomSource = random;
    }

    public void reset() {
        haveNextNextGaussian = false;
    }

    public double nextGaussian() {
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        }

        double v1, v2, s;
        do {
            v1 = 2 * randomSource.nextDouble() - 1;
            v2 = 2 * randomSource.nextDouble() - 1;
            s = Mth.square(v1) + Mth.square(v2);
        } while (s >= 1 || s == 0);
        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        nextNextGaussian = v2 * multiplier;
        haveNextNextGaussian = true;
        return v1 * multiplier;
    }
}
