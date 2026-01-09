package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.RandomSource;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectSpruceTree extends ObjectTree {
    protected int treeHeight;
    private final boolean canCreateSnow;

    public ObjectSpruceTree() {
        this(false);
    }

    public ObjectSpruceTree(boolean canCreateSnow) {
        this.canCreateSnow = canCreateSnow;
    }

    @Override
    public int getTrunkBlock() {
        return Block.SPRUCE_LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.SPRUCE_LEAVES;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        this.treeHeight = random.nextBoundedInt(4) + 6;

        int topSize = this.treeHeight - (1 + random.nextBoundedInt(2));
        int lRadius = 2 + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.treeHeight - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    public void placeLeaves(ChunkManager level, int topSize, int lRadius, int x, int y, int z, RandomSource random)   {
        int radius = random.nextBoundedInt(2);
        int maxR = 1;
        int minR = 0;

        boolean createSnow = false;
        if (this.canCreateSnow) {
            FullChunk chunk = level.getChunk(x >> 4, z >> 4);
            createSnow = chunk == null || Biomes.get(chunk.getBiomeId(x & 0x0f, z & 0x0f)).isFreezing();
        }

        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - radius; zz <= z + radius; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }

                    if (!Block.solid[level.getBlockIdAt(0, xx, yyy, zz)]) {
                        level.setBlockAt(0, xx, yyy, zz, this.getLeafBlock(), this.getType());

                        if (createSnow) {
                            if (level.getBlockIdAt(0, xx, yyy + 1, zz) == Block.AIR && level.getBlockIdAt(0, xx, yyy + 2, zz) == Block.AIR) {
                                level.setBlockAt(0, xx, yyy + 1, zz, Block.SNOW_LAYER, 0);
                            }
                        }
                    }
                }
            }

            if (radius >= maxR) {
                radius = minR;
                minR = 1;
                if (++maxR > lRadius) {
                    maxR = lRadius;
                }
            } else {
                ++radius;
            }
        }
    }
}
