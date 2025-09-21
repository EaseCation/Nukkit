package cn.nukkit.math;

import java.util.concurrent.atomic.AtomicLong;

public interface RandomSource {
    RandomSource fork();

    void setSeed(long seed);

    int nextInt();

    default int nextUnsignedInt() {
        return nextInt() & 0x7fffffff;
    }

    /**
     * @param bound the upper bound (exclusive)
     */
    int nextInt(int bound);

    /**
     * @param origin the least value that can be returned
     * @param bound the upper bound (exclusive)
     */
    default int nextInt(int origin, int bound) {
        if (origin >= bound) {
            return origin;
        }
        return origin + nextInt(bound - origin);
    }

    default int nextIntInclusive(int origin, int bound) {
        return nextInt(origin, bound + 1);
    }

    default int nextGaussianInt(int n) {
        return nextInt(n) - nextInt(n);
    }

    long nextLong();

    default long nextUnsignedLong() {
        return nextLong() & 0x7fffffffffffffffL;
    }

    boolean nextBoolean();

    float nextFloat();

    default float nextFloat(float bound) {
        return nextFloat() * bound;
    }

    default float nextFloat(float origin, float bound) {
        if (origin >= bound) {
            return origin;
        }
        return origin + nextFloat(bound - origin);
    }

    double nextDouble();

    default double nextDouble(double bound) {
        return nextDouble() * bound;
    }

    default double nextDouble(double origin, double bound) {
        if (origin >= bound) {
            return origin;
        }
        return origin + nextDouble(bound - origin);
    }

    double nextGaussian();

    /**
     * Returns a random float between {@code -1.0} and {@code 1.0}
     */
    default float nextGaussianFloat() {
        return nextFloat() - nextFloat();
    }

    default double triangle(double mode, double deviation) {
        return mode + deviation * (nextDouble() - nextDouble());
    }

    default Vector3 nextVector3() {
        return new Vector3(nextFloat(), nextFloat(), nextFloat());
    }

    default Vector3 nextGaussianVector3() {
        return new Vector3(nextGaussian(), nextGaussian(), nextGaussian());
    }

    default void consumeCount(int count) {
        for (int i = 0; i < count; i++) {
            nextInt();
        }
    }

    /**
     * Returns a random unsigned integer
     */
    default int nextRange() {
        return nextUnsignedInt();
    }

    /**
     * @param start the least value that can be returned
     */
    default int nextRange(int start) {
        return nextRange(start, 0x7fffffff);
    }

    /**
     * Returns a random integer between start (inclusive) and end (inclusive)
     */
    default int nextRange(int start, int end) {
        return nextIntInclusive(start, end);
    }

    /**
     * @param bound the upper bound (exclusive)
     */
    default int nextBoundedInt(int bound) {
        if (bound == 0) {
            return 0;
        }
        return nextUnsignedInt() % bound;
    }

    final class RandomSupport {
        public static final float FLOAT_UNIT = 0x1.0p-24f;
        public static final double DOUBLE_UNIT = 0x1.0p-53;

        public static final long GOLDEN_RATIO_64 = 0x9e3779b97f4a7c15L;
        public static final long SILVER_RATIO_64 = 0x6A09E667F3BCC909L;

        private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

        public static long seedUniquifier() {
            return SEED_UNIQUIFIER.updateAndGet(seed -> seed * 1181783497276652981L);
        }

        public static long mixStafford13(long z) {
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            return z ^ (z >>> 31);
        }
    }
}
