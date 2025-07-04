package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ObjectBigSpruceTree extends ObjectSpruceTree {
    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;

    public ObjectBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(15) + 20;

        if (!level.getHeightRange().isValidBlockY(y + treeHeight)) {
            return;
        }

        int topSize = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int lRadius = baseLeafRadius + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    protected int placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        int lowest = Integer.MIN_VALUE;

        // The base dirt block
        level.setBlockAt(0, x, y - 1, z, Block.DIRT);
        int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int posY = y + yy;
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    int blockId = level.getBlockIdAt(0, x, posY, z);
                    if (this.overridable(blockId)) {
                        level.setBlockAt(0, x + xx, posY, z + zz, this.getTrunkBlock());
                        if (lowest == Integer.MIN_VALUE) {
                            lowest = posY;
                        }
                    }
                }
            }
        }

        return lowest;
    }

    @Override
    public void placeLeaves(ChunkManager level, int topSize, int lRadius, int x, int y, int z, NukkitRandom random) {
        int radius = random.nextBoundedInt(2);
        int maxR = 1;
        int minR = 0;

        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius + 1; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - radius; zz <= z + radius + 1; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (radius > 0 && (xx < x && xOff == radius || xx > x && xOff == radius + 1) && (zz < z && zOff == radius || zz > z && zOff == radius + 1)) {
                        continue;
                    }

                    if (!Block.solid[level.getBlockIdAt(0, xx, yyy, zz)]) {
                        level.setBlockAt(0, xx, yyy, zz, this.getLeafBlock(), this.getType());
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
