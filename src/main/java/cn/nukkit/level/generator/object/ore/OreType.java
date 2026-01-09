package cn.nukkit.level.generator.object.ore;

import cn.nukkit.block.Block;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
//porktodo: rewrite this, the whole class is terrible and generated ores look stupid
public class OreType {
    public final int fullId;
    public final int clusterCount;
    public final int clusterSize;
    public final int maxHeight;
    public final int minHeight;
    public final int replaceBlockId;

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight) {
        this(material, clusterCount, clusterSize, minHeight, maxHeight, Block.STONE);
    }

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight, int replaceBlockId) {
        this.fullId = material.getFullId();
        this.clusterCount = clusterCount;
        this.clusterSize = clusterSize;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.replaceBlockId = replaceBlockId;
    }

    public boolean spawn(FullChunk chunk, RandomSource rand, int x, int y, int z) {
        float piScaled = rand.nextFloat() * (float) Math.PI;
        double scaleMaxX = (float) (x + 8) + Mth.sin(piScaled) * (float) clusterSize / 8.0F;
        double scaleMinX = (float) (x + 8) - Mth.sin(piScaled) * (float) clusterSize / 8.0F;
        double scaleMaxZ = (float) (z + 8) + Mth.cos(piScaled) * (float) clusterSize / 8.0F;
        double scaleMinZ = (float) (z + 8) - Mth.cos(piScaled) * (float) clusterSize / 8.0F;
        double scaleMaxY = y + rand.nextBoundedInt(3) - 2;
        double scaleMinY = y + rand.nextBoundedInt(3) - 2;

        for (int i = 0; i < clusterSize; ++i) {
            float sizeIncr = (float) i / (float) clusterSize;
            double scaleX = scaleMaxX + (scaleMinX - scaleMaxX) * (double) sizeIncr;
            double scaleY = scaleMaxY + (scaleMinY - scaleMaxY) * (double) sizeIncr;
            double scaleZ = scaleMaxZ + (scaleMinZ - scaleMaxZ) * (double) sizeIncr;
            double randSizeOffset = rand.nextFloat() * (double) clusterSize / 16.0D;
            double randVec1 = (double) (Mth.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            double randVec2 = (double) (Mth.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            int minX = Mth.floor(scaleX - randVec1 / 2.0D);
            int minY = Mth.floor(scaleY - randVec2 / 2.0D);
            int minZ = Mth.floor(scaleZ - randVec1 / 2.0D);
            int maxX = Mth.floor(scaleX + randVec1 / 2.0D);
            int maxY = Mth.floor(scaleY + randVec2 / 2.0D);
            int maxZ = Mth.floor(scaleZ + randVec1 / 2.0D);

            HeightRange heightRange = chunk.getHeightRange();
            if (minY < heightRange.getMinY() || maxY >= heightRange.getMaxY()) {
                continue;
            }

            for (int xSeg = minX; xSeg <= maxX; ++xSeg) {
                double xVal = ((double) xSeg + 0.5D - scaleX) / (randVec1 / 2.0D);

                if (xVal * xVal < 1.0D) {
                    for (int ySeg = minY; ySeg <= maxY; ++ySeg) {
                        double yVal = ((double) ySeg + 0.5D - scaleY) / (randVec2 / 2.0D);

                        if (xVal * xVal + yVal * yVal < 1.0D) {
                            for (int zSeg = minZ; zSeg <= maxZ; ++zSeg) {
                                double zVal = ((double) zSeg + 0.5D - scaleZ) / (randVec1 / 2.0D);

                                if (xVal * xVal + yVal * yVal + zVal * zVal < 1.0D) {
                                    int localX = xSeg & 0xf;
                                    int localZ = zSeg & 0xf;
                                    if (chunk.getBlockId(0, localX, ySeg, localZ) == replaceBlockId) {
                                        chunk.setFullBlockId(0, localX, ySeg, localZ, fullId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
