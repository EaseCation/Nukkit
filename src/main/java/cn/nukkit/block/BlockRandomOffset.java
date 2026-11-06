package cn.nukkit.block;

import cn.nukkit.math.*;

import java.util.Objects;

/**
 * Computes random offsets from horizontal block coordinates.
 */
public record BlockRandomOffset(Axis x, Axis y, Axis z) {
    private static final int HORIZONTAL_STEPS = 16;

    public BlockRandomOffset {
        Objects.requireNonNull(x, "x");
        Objects.requireNonNull(y, "y");
        Objects.requireNonNull(z, "z");
    }

    public boolean isZero() {
        return x.isZero() && y.isZero() && z.isZero();
    }

    public Vector3f offset(int blockX, int blockZ) {
        RandomSource random = random(blockX, blockZ);
        float offsetX = x.sample(random.nextFloat());
        float offsetY = y.sample(random.nextFloat());
        float offsetZ = z.sample(random.nextFloat());
        return new Vector3f(offsetX, offsetY, offsetZ);
    }

    public AxisAlignedBB apply(int blockX, int blockZ, AxisAlignedBB box) {
        RandomSource random = random(blockX, blockZ);
        float offsetX = x.sample(random.nextFloat());
        float offsetY = y.sample(random.nextFloat());
        float offsetZ = z.sample(random.nextFloat());
        return box.offset(offsetX, offsetY, offsetZ);
    }

    public static AxisAlignedBB applyHorizontal(int blockX, int blockZ, float maxOffset, AxisAlignedBB box) {
        RandomSource random = random(blockX, blockZ);
        float offsetX = Axis.sample(-maxOffset, maxOffset, HORIZONTAL_STEPS, random.nextFloat());
        // Consume the Y draw even when its offset is zero so Z uses the third draw.
        random.nextFloat();
        float offsetZ = Axis.sample(-maxOffset, maxOffset, HORIZONTAL_STEPS, random.nextFloat());
        return box.offset(offsetX, 0, offsetZ);
    }

    /**
     * @return xoroshiro128++
     */
    private static RandomSource random(int x, int z) {
        // Multiply X with 32-bit overflow, then mix and shift in 64 bits before truncating the result to a signed 32-bit seed.
        long hash = (long) (x * 0x2fc20f) ^ z * 0x6ebfff5L;
        int seed = (int) (((hash * 0x285b825L + 0xbL) * hash) >> 16);
        return NukkitRandom.main(seed);
    }

    /**
     * A step count of zero samples continuously, one selects the midpoint,
     * and larger counts select from evenly spaced values including both endpoints.
     */
    public record Axis(float min, float max, long steps) {
        public Axis {
            if (!Float.isFinite(min) || !Float.isFinite(max)) {
                throw new IllegalArgumentException("Random offset bounds must be finite");
            }
            if (steps < 0 || steps > 0xffff_ffffL) {
                throw new IllegalArgumentException("Random offset steps must be an unsigned 32-bit integer");
            }
        }

        private float sample(float value) {
            return sample(min, max, steps, value);
        }

        private boolean isZero() {
            if (min >= max) {
                return min == 0;
            }
            if (steps == 1) {
                return (min + max) * 0.5f == 0;
            }
            return steps > 1 && min == 0 && (max - min) / (steps - 1) == 0;
        }

        private static float sample(float min, float max, long steps, float value) {
            if (min >= max) {
                return min;
            }
            if (steps == 0) {
                return (max - min) * value + min;
            }
            if (steps == 1) {
                return (min + max) * 0.5f;
            }
            float bucket = Mth.lfloor(value * steps);
            float step = (max - min) / (steps - 1);
            return bucket * step + min;
        }
    }
}
