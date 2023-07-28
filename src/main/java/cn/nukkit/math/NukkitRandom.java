package cn.nukkit.math;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Xoroshiro128++
 *
 * @author Angelic47
 * Nukkit Project
 */
public class NukkitRandom implements RandomSource {
    private static final ThreadLocal<NukkitRandom> LOCAL = ThreadLocal.withInitial(NukkitRandom::new);
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(RandomSupport.mixStafford13(System.currentTimeMillis()) ^ RandomSupport.mixStafford13(System.nanoTime()));

    private long l, h;

    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public NukkitRandom() {
        this(seedUniquifier());
    }

    public NukkitRandom(long seed) {
        setSeed(seed);
    }

    private NukkitRandom(long l, long h) {
        this.l = l;
        this.h = h;
        checkZeroSeed();
    }

    private void checkZeroSeed() {
        if ((l | h) == 0) {
            l = RandomSupport.GOLDEN_RATIO_64;
            h = RandomSupport.SILVER_RATIO_64;
        }
    }

    @Override
    public NukkitRandom fork() {
        return new NukkitRandom(nextLong(), nextLong());
    }

    @Override
    public void setSeed(long seed) {
        long s = seed ^ RandomSupport.SILVER_RATIO_64;
        l = RandomSupport.mixStafford13(s);
        h = RandomSupport.mixStafford13(s + RandomSupport.GOLDEN_RATIO_64);
        checkZeroSeed();
        gaussianSource.reset();
    }

    public static long seedUniquifier() {
        return SEED_UNIQUIFIER.getAndAdd(RandomSupport.GOLDEN_RATIO_64);
    }

    private long next() {
        final long s0 = l;
        long s1 = h;

        long result = Long.rotateLeft(s0 + s1, 17) + s0;

        s1 ^= s0;
        l = Long.rotateLeft(s0, 49) ^ s1 ^ s1 << 21;
        h = Long.rotateLeft(s1, 28);

        return result;
    }

    private long nextBits(int bits) {
        return next() >>> 64 - bits;
    }

    @Override
    public int nextInt() {
        return (int) next();
    }

    @Override
    public int nextInt(int bound) {
        long r = Integer.toUnsignedLong(nextInt()) * bound;
        long t = r & 0xffffffffL;
        if (t < bound) {
            int remainder = Integer.remainderUnsigned(~bound + 1, bound);
            while (t < remainder) {
                r = Integer.toUnsignedLong(nextInt()) * bound;
                t = r & 0xffffffffL;
            }
        }
        return (int) (r >> 32);
    }

    @Override
    public long nextLong() {
        return next();
    }

    @Override
    public boolean nextBoolean() {
        return (next() & 1) != 0;
    }

    @Override
    public float nextFloat() {
        return nextBits(24) * RandomSupport.FLOAT_UNIT;
    }

    @Override
    public double nextDouble() {
        return nextBits(53) * RandomSupport.DOUBLE_UNIT;
    }

    /**
     * Returns the next pseudorandom, uniformly distributed
     * {@code double} value between {@code 0.0} and
     * {@code 1.0} from this random number generator's sequence,
     * using a fast multiplication-free method which, however,
     * can provide only 52 significant bits.
     *
     * <p>This method is faster than {@link #nextDouble()}, but it
     * can return only dyadic rationals of the form <var>k</var> / 2<sup>&minus;52</sup>,
     * instead of the standard <var>k</var> / 2<sup>&minus;53</sup>.
     *
     * <p>The only difference between the output of this method and that of
     * {@link #nextDouble()} is an additional least significant bit set in half of the
     * returned values. For most applications, this difference is negligible.
     *
     * @return the next pseudorandom, uniformly distributed {@code double}
     * value between {@code 0.0} and {@code 1.0} from this
     * random number generator's sequence, using 52 significant bits only.
     */
    public double nextDoubleFast() {
        return Double.longBitsToDouble(0x3ffL << 52 | next() >>> 12) - 1;
    }

    @Override
    public double nextGaussian() {
        return gaussianSource.nextGaussian();
    }

    @Override
    public void consumeCount(int count) {
        for (int i = 0; i < count; i++) {
            nextLong();
        }
    }

    public static NukkitRandom current() {
        return LOCAL.get();
    }
}
