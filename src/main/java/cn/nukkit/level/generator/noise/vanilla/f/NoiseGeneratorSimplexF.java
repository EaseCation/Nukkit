package cn.nukkit.level.generator.noise.vanilla.f;

import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

/**
 * SimplexNoise
 */
public class NoiseGeneratorSimplexF {
    private static final float SQRT_3 = (float) Math.sqrt(3.0f);
    private static final int[][] grad3 = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    private static final float F2 = 0.5f * (SQRT_3 - 1.0f);
    private static final float G2 = (3.0f - SQRT_3) / 6.0f;
    private static final float F3 = 0.33333334f;
    private static final float G3 = 0.16666667f;

    private final int[] p;
    public final float xo;
    public final float yo;
    public final float zo;

    public NoiseGeneratorSimplexF() {
        this(new NukkitRandom(System.currentTimeMillis()));
    }

    public NoiseGeneratorSimplexF(RandomSource p_i45471_1_) {
        this.p = new int[512];
        this.xo = p_i45471_1_.nextFloat() * 256.0f;
        this.yo = p_i45471_1_.nextFloat() * 256.0f;
        this.zo = p_i45471_1_.nextFloat() * 256.0f;

        int i = 0;
        while (i < 256) {
            this.p[i] = i++;
        }

        for (int l = 0; l < 256; ++l) {
            int j = p_i45471_1_.nextBoundedInt(256 - l) + l;
            int k = this.p[l];
            this.p[l] = this.p[j];
            this.p[j] = k;
            this.p[l + 256] = this.p[l];
        }
    }

    private static float dot(int[] p_151604_0_, float p_151604_1_, float p_151604_3_) {
        return (float) p_151604_0_[0] * p_151604_1_ + (float) p_151604_0_[1] * p_151604_3_;
    }

    private static float dot(int[] g, float x, float y, float z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    private static float getCornerNoise(int i, float x, float y) {
        float t = 0.5f - Mth.square(x) - Mth.square(y);
        if (t < 0) {
            return 0;
        }
        return Mth.square(Mth.square(t)) * dot(grad3[i], x, y);
    }

    private static float getCornerNoise(int i, float x, float y, float z) {
        float t = 0.6f - Mth.square(x) - Mth.square(y) - Mth.square(z);
        if (t < 0) {
            return 0;
        }
        return Mth.square(Mth.square(t)) * dot(grad3[i], x, y, z);
    }

    public float getValue(float p_151605_1_, float p_151605_3_) {
        float d3 = F2;
        float d4 = (p_151605_1_ + p_151605_3_) * d3;
        int i = Mth.floor(p_151605_1_ + d4);
        int j = Mth.floor(p_151605_3_ + d4);
        float d5 = G2;
        float d6 = (float) (i + j) * d5;
        float d7 = (float) i - d6;
        float d8 = (float) j - d6;
        float d9 = p_151605_1_ - d7;
        float d10 = p_151605_3_ - d8;
        int k;
        int l;

        if (d9 > d10) {
            k = 1;
            l = 0;
        } else {
            k = 0;
            l = 1;
        }

        float d11 = d9 - (float) k + d5;
        float d12 = d10 - (float) l + d5;
        float d13 = d9 - 1.0f + 2.0f * d5;
        float d14 = d10 - 1.0f + 2.0f * d5;
        int i1 = i & 255;
        int j1 = j & 255;
        int k1 = this.p[i1 + this.p[j1]] % 12;
        int l1 = this.p[i1 + k + this.p[j1 + l]] % 12;
        int i2 = this.p[i1 + 1 + this.p[j1 + 1]] % 12;
        float d15 = 0.5f - d9 * d9 - d10 * d10;
        float d0;

        if (d15 < 0.0f) {
            d0 = 0.0f;
        } else {
            d15 = d15 * d15;
            d0 = d15 * d15 * dot(grad3[k1], d9, d10);
        }

        float d16 = 0.5f - d11 * d11 - d12 * d12;
        float d1;

        if (d16 < 0.0f) {
            d1 = 0.0f;
        } else {
            d16 = d16 * d16;
            d1 = d16 * d16 * dot(grad3[l1], d11, d12);
        }

        float d17 = 0.5f - d13 * d13 - d14 * d14;
        float d2;

        if (d17 < 0.0f) {
            d2 = 0.0f;
        } else {
            d17 = d17 * d17;
            d2 = d17 * d17 * dot(grad3[i2], d13, d14);
        }

        return 70.0f * (d0 + d1 + d2);
    }

    public float getValue(float x, float y, float z) {
        float s = (x + y + z) * F3;
        int i = Mth.floor(x + s);
        int j = Mth.floor(y + s);
        int k = Mth.floor(z + s);
        float t = (i + j + k) * G3;
        float x0 = x - (i - t);
        float y0 = y - (j - t);
        float z0 = z - (k - t);
        int i0;
        int j0;
        int k0;
        int i1;
        int j1;
        int k1;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i0 = 1;
                j0 = 0;
                k0 = 0;
                i1 = 1;
                j1 = 1;
                k1 = 0;
            } else if (x0 >= z0) {
                i0 = 1;
                j0 = 0;
                k0 = 0;
                i1 = 1;
                j1 = 0;
                k1 = 1;
            } else {
                i0 = 0;
                j0 = 0;
                k0 = 1;
                i1 = 1;
                j1 = 0;
                k1 = 1;
            }
        } else if (y0 < z0) {
            i0 = 0;
            j0 = 0;
            k0 = 1;
            i1 = 0;
            j1 = 1;
            k1 = 1;
        } else if (x0 < z0) {
            i0 = 0;
            j0 = 1;
            k0 = 0;
            i1 = 0;
            j1 = 1;
            k1 = 1;
        } else {
            i0 = 0;
            j0 = 1;
            k0 = 0;
            i1 = 1;
            j1 = 1;
            k1 = 0;
        }
        int ii = i & 0xff;
        int jj = j & 0xff;
        int kk = k & 0xff;
        return (getCornerNoise(this.p[ii + this.p[jj + this.p[kk]]] % 12, x0, y0, z0)
                + getCornerNoise(this.p[ii + i0 + this.p[jj + j0 + this.p[kk + k0]]] % 12, x0 - i0 + G3, y0 - j0 + G3, z0 - k0 + G3)
                + getCornerNoise(this.p[ii + i1 + this.p[jj + j1 + this.p[kk + k1]]] % 12, x0 - i1 + F3, y0 - j1 + F3, z0 - k1 + F3)
                + getCornerNoise(this.p[ii + 1 + this.p[jj + 1 + this.p[kk + 1]]] % 12, x0 - 1 + 0.5f, y0 - 1 + 0.5f, z0 - 1 + 0.5f)) * 32;
    }

    public void add(float[] p_151606_1_, float p_151606_2_, float p_151606_4_, int p_151606_6_, int p_151606_7_, float p_151606_8_, float p_151606_10_, float p_151606_12_) {
        int i = 0;

        for (int j = 0; j < p_151606_7_; ++j) {
            float d0 = (p_151606_4_ + (float) j) * p_151606_10_ + this.yo;

            for (int k = 0; k < p_151606_6_; ++k) {
                float d1 = (p_151606_2_ + (float) k) * p_151606_8_ + this.xo;
                float d5 = (d1 + d0) * F2;
                int l = Mth.floor(d1 + d5);
                int i1 = Mth.floor(d0 + d5);
                float d6 = (float) (l + i1) * G2;
                float d7 = (float) l - d6;
                float d8 = (float) i1 - d6;
                float d9 = d1 - d7;
                float d10 = d0 - d8;
                int j1;
                int k1;

                if (d9 > d10) {
                    j1 = 1;
                    k1 = 0;
                } else {
                    j1 = 0;
                    k1 = 1;
                }

                float d11 = d9 - (float) j1 + G2;
                float d12 = d10 - (float) k1 + G2;
                float d13 = d9 - 1.0f + 2.0f * G2;
                float d14 = d10 - 1.0f + 2.0f * G2;
                int l1 = l & 255;
                int i2 = i1 & 255;
                int j2 = this.p[l1 + this.p[i2]] % 12;
                int k2 = this.p[l1 + j1 + this.p[i2 + k1]] % 12;
                int l2 = this.p[l1 + 1 + this.p[i2 + 1]] % 12;
                float d15 = 0.5f - d9 * d9 - d10 * d10;
                float d2;

                if (d15 < 0.0f) {
                    d2 = 0.0f;
                } else {
                    d15 = d15 * d15;
                    d2 = d15 * d15 * dot(grad3[j2], d9, d10);
                }

                float d16 = 0.5f - d11 * d11 - d12 * d12;
                float d3;

                if (d16 < 0.0f) {
                    d3 = 0.0f;
                } else {
                    d16 = d16 * d16;
                    d3 = d16 * d16 * dot(grad3[k2], d11, d12);
                }

                float d17 = 0.5f - d13 * d13 - d14 * d14;
                float d4;

                if (d17 < 0.0f) {
                    d4 = 0.0f;
                } else {
                    d17 = d17 * d17;
                    d4 = d17 * d17 * dot(grad3[l2], d13, d14);
                }

                int i3 = i++;
                p_151606_1_[i3] += 70.0f * (d2 + d3 + d4) * p_151606_12_;
            }
        }
    }
}
