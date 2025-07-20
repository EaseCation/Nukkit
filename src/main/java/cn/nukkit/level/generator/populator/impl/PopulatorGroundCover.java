package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGroundCover extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int realX = chunkX << 4;
        int realZ = chunkZ << 4;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome realBiome = Biomes.get(chunk.getBiomeId(x, z));
                if (realBiome instanceof CoveredBiome) {
                    final CoveredBiome biome = (CoveredBiome) realBiome;
                    //just in case!
                    biome.synchronizeCover.lock();
                    try {
                        biome.preCover(realX | x, realZ | z);
                        int coverBlock = biome.getCoverBlock();

                        boolean hasCovered = false;
                        int realY;
                        //start one below build limit in case of cover blocks
                        for (int y = level.getHeightRange().getMaxY() - 1 - 1; y > 32; y--) {
                            if (chunk.getBlockId(0, x, y, z) == STONE) {
                                COVER:
                                if (!hasCovered) {
                                    if (y >= Normal.seaHeight) {
                                        chunk.setBlock(0, x, y + 1, z, coverBlock);
                                        int surfaceDepth = biome.getSurfaceDepth(y);
                                        for (int i = 0; i < surfaceDepth; i++) {
                                            realY = y - i;
                                            if (chunk.getBlockId(0, x, realY, z) == STONE) {
                                                chunk.setBlock(0, x, realY, z, biome.getSurfaceBlock(realY), biome.getSurfaceMeta(realY));
                                            } else break COVER;
                                        }
                                        y -= surfaceDepth;
                                    }
                                    int groundDepth = biome.getGroundDepth(y);
                                    for (int i = 0; i < groundDepth; i++) {
                                        realY = y - i;
                                        if (chunk.getBlockId(0, x, realY, z) == STONE) {
                                            chunk.setBlock(0, x, realY, z, biome.getGroundBlock(realY), biome.getGroundMeta(realY));
                                        } else break COVER;
                                    }
                                    //don't take all of groundDepth away because we do y-- in the loop
                                    y -= groundDepth - 1;
                                }
                                hasCovered = true;
                            } else {
                                if (hasCovered) {
                                    //reset it if this isn't a valid stone block (allows us to place ground cover on top and below overhangs)
                                    hasCovered = false;
                                }
                            }
                        }
                    } finally {
                        biome.synchronizeCover.unlock();
                    }
                }
            }
        }
    }
}
