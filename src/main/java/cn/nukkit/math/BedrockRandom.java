package cn.nukkit.math;

/**
 * Mersenne Twister 19937 (mt19937_32)
 */
public class BedrockRandom implements RandomSource {
    private static final ThreadLocal<BedrockRandom> LOCAL = ThreadLocal.withInitial(BedrockRandom::new);

    /**
     * The number of elements in the state sequence (degree of recurrence).
     */
    private static final int STATE_SIZE = 624;
    /**
     * The shift size used on twists to transform the values.
     */
    private static final int SHIFT_SIZE = 397;
    /**
     * The XOR mask applied as the linear function on each twist.
     */
    private static final int XOR_MASK = 0x9908b0df;
    private static final int[] MAGIC01 = {0, XOR_MASK};
    /**
     * The XOR mask used as parameter b in the tempering process of the generation algorithm.
     */
    private static final int TEMPERING_B = 0x9d2c5680;
    /**
     * The XOR mask used as parameter b in the tempering process of the generation algorithm.
     */
    private static final int TEMPERING_C = 0xefc60000;
    /**
     * The initialization multiplier used to seed the state sequence when a single value is used as seed.
     */
    private static final int INITIALIZATION_MULTIPLIER = 0x6c078965;
    /**
     * The default seed used on construction or seeding.
     */
    private static final int DEFAULT_SEED = 0x1571;

    /**
     * 0x1.0p-32
     */
    private static final double REAL2_UNIT = 2.328306436538696e-10;

    private final int[] mt;
    private int initedIdx;
    private int mti;

    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public BedrockRandom() {
        this(RandomSupport.seedUniquifier() ^ System.nanoTime());
    }

    public BedrockRandom(long seed) {
        this((int) seed);
    }

    public BedrockRandom(int seed) {
        mt = new int[STATE_SIZE];
        setSeed(seed);
    }

    @Override
    public BedrockRandom fork() {
        return new BedrockRandom(genRandInt32());
    }

    public void setSeed(int seed) {
        initedIdx = STATE_SIZE + 1;
        gaussianSource.reset();
        initGenRandFast(seed);
    }

    @Override
    public void setSeed(long seed) {
        setSeed((int) seed);
    }

    private void initGenRandFast(int s) {
        int mtLast = mt[0] = s;
        for (int idx = 1; idx <= SHIFT_SIZE; idx++) {
            mt[idx] = mtLast = idx + INITIALIZATION_MULTIPLIER * ((mtLast >>> 30) ^ mtLast);
        }
        mti = STATE_SIZE;
        initedIdx = SHIFT_SIZE + 1;
    }

    private void initGenRand(int s) {
        int mtLast = mt[0] = s;
        for (mti = 1; mti < STATE_SIZE; ++mti) {
            mt[mti] = mtLast = mti + INITIALIZATION_MULTIPLIER * ((mtLast >>> 30) ^ mtLast);
        }
        initedIdx = STATE_SIZE;
    }

    /**
     * @return uint32
     */
    private int genRandInt32() {
        if (mti == STATE_SIZE) {
            mti = 0;
        } else if (mti > STATE_SIZE) {
            initGenRand(DEFAULT_SEED);
        }

        if (mti >= STATE_SIZE - SHIFT_SIZE) {
            if (mti >= STATE_SIZE - 1) {
                mt[STATE_SIZE - 1] = MAGIC01[mt[0] & 1] ^ ((mt[0] & 0x7fffffff | mt[STATE_SIZE - 1] & 0x80000000) >>> 1) ^ mt[SHIFT_SIZE - 1];
            } else {
                mt[mti] = MAGIC01[mt[mti + 1] & 1] ^ ((mt[mti + 1] & 0x7fffffff | mt[mti] & 0x80000000) >>> 1) ^ mt[mti - (STATE_SIZE - SHIFT_SIZE)];
            }
        } else {
            mt[mti] = MAGIC01[mt[mti + 1] & 1] ^ ((mt[mti + 1] & 0x7fffffff | mt[mti] & 0x80000000) >> 1) ^ mt[mti + SHIFT_SIZE];

            if (initedIdx < STATE_SIZE) {
                mt[initedIdx] = initedIdx + INITIALIZATION_MULTIPLIER * ((mt[initedIdx - 1] >>> 30) ^ mt[initedIdx - 1]);
            }
        }

        int y = mt[mti++];
        y = ((y ^ (y >> 11)) << 7) & TEMPERING_B ^ y ^ (y >> 11);
        return (y << 15) & TEMPERING_C ^ y ^ (((y << 15) & TEMPERING_C ^ y) >> 18);
    }

    private double genRandReal2() {
//        return (genRandInt32() & 0xffffffffL) * REAL2_UNIT;
        return genRandInt32() * 0x1.0p-31;
    }

    @Override
    public int nextInt() {
        return genRandInt32();
    }

    @Override
    public int nextUnsignedInt() {
        return genRandInt32();
    }

    @Override
    public int nextInt(int bound) {
        if (bound == 0) {
            return 0;
        }
        return genRandInt32() % bound;
    }

    @Override
    public long nextLong() {
        return ((long) genRandInt32() << 32) + genRandInt32();
    }

    @Override
    public boolean nextBoolean() {
//        return genRandInt32() < 0;
        return (genRandInt32() << 1) < 0;
    }

    @Override
    public float nextFloat() {
        return (float) genRandReal2();
    }

    @Override
    public double nextDouble() {
        return genRandReal2();
    }

    @Override
    public double nextGaussian() {
        return gaussianSource.nextGaussian();
    }

    public static BedrockRandom current() {
        return LOCAL.get();
    }
}
