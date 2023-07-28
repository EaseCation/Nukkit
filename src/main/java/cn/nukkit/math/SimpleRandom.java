package cn.nukkit.math;

/**
 * Linear Congruential Generator (Java Legacy Random)
 */
public class SimpleRandom implements RandomSource {
    private static final ThreadLocal<SimpleRandom> LOCAL = ThreadLocal.withInitial(SimpleRandom::new);

    private static final long MULTIPLIER = 0x5deece66dL;
    private static final long ADDEND = 0xb;
    private static final long MASK = (1L << 48) - 1;

    private long seed;

    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public SimpleRandom() {
        this(RandomSupport.seedUniquifier() ^ System.nanoTime());
    }

    public SimpleRandom(long seed) {
        setSeed(seed);
    }

    @Override
    public SimpleRandom fork() {
        return new SimpleRandom(nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.seed = (seed ^ MULTIPLIER) & MASK;
        gaussianSource.reset();
    }

    private int next(int bits) {
        long nextSeed = (seed * MULTIPLIER + ADDEND) & MASK;
        seed = nextSeed;
        return (int) (nextSeed >> (48 - bits));
    }

    @Override
    public int nextInt() {
        return next(32);
    }

    @Override
    public int nextInt(int bound) {
        int r = next(31);
        int m = bound - 1;
        if ((bound & m) == 0) {
            r = (int) ((bound * (long) r) >> 31);
        } else {
            for (int u = r;
                 u - (r = u % bound) + m < 0;
                 u = next(31))
                ;
        }
        return r;
    }

    @Override
    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    @Override
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    @Override
    public float nextFloat() {
        return next(24) * RandomSupport.FLOAT_UNIT;
    }

    @Override
    public double nextDouble() {
        return (((long) next(26) << 27) + next(27)) * RandomSupport.DOUBLE_UNIT;
    }

    @Override
    public double nextGaussian() {
        return gaussianSource.nextGaussian();
    }

    public static SimpleRandom current() {
        return LOCAL.get();
    }
}
