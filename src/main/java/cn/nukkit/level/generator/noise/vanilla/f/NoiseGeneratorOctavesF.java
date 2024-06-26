package cn.nukkit.level.generator.noise.vanilla.f;

import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;

import java.util.Arrays;

public class NoiseGeneratorOctavesF {
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private final NoiseGeneratorImprovedF[] generatorCollection;
    private final int octaves;

    public NoiseGeneratorOctavesF(NukkitRandom seed, int octavesIn) {
        this.octaves = octavesIn;
        this.generatorCollection = new NoiseGeneratorImprovedF[octavesIn];

        for (int i = 0; i < octavesIn; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorImprovedF(seed);
        }
    }

    /*
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public float[] generateNoiseOctaves(float[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, float xScale, float yScale, float zScale) {
        if (noiseArray == null) {
            noiseArray = new float[xSize * ySize * zSize];
        } else {
            Arrays.fill(noiseArray, 0.0f);
        }

        float d3 = 1.0f;

        for (int j = 0; j < this.octaves; ++j) {
            float d0 = (float) xOffset * d3 * xScale;
            float d1 = (float) yOffset * d3 * yScale;
            float d2 = (float) zOffset * d3 * zScale;
            int k = Mth.floor(d0);
            int l = Mth.floor(d2);
            d0 = d0 - (float) k;
            d2 = d2 - (float) l;
            k = k % 16777216;
            l = l % 16777216;
            d0 = d0 + (float) k;
            d2 = d2 + (float) l;
            this.generatorCollection[j].populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3);
            d3 /= 2.0D;
        }

        return noiseArray;
    }

    /*
     * Bouncer function to the main one with some default arguments.
     */
    public float[] generateNoiseOctaves(float[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, float xScale, float zScale, float p_76305_10_) {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0f, zScale);
    }
}
