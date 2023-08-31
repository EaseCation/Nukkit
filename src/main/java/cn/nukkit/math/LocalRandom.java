package cn.nukkit.math;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Java ThreadLocalRandom
 */
public class LocalRandom implements RandomSource {
    private final ThreadLocalRandom random;

    public LocalRandom() {
        this(ThreadLocalRandom.current());
    }

    private LocalRandom(ThreadLocalRandom random) {
        this.random = random;
    }

    @Override
    public RandomSource fork() {
        return new LocalRandom(ThreadLocalRandom.current());
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public float nextFloat(float bound) {
        return random.nextFloat(bound);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        return random.nextFloat(origin, bound);
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public double nextDouble(double bound) {
        return random.nextDouble(bound);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        return random.nextDouble(origin, bound);
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }

    @Override
    public void consumeCount(int count) {
    }
}
