package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author FlamingKnight
 */
public abstract class ObjectNetherTree extends ObjectTree {

    private final int treeHeight;

    public ObjectNetherTree() {
        this(ThreadLocalRandom.current().nextInt(9) + 4);
    }

    public ObjectNetherTree(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int maxBlockY = level.getHeightRange().getMaxY();

        if (y + treeHeight >= maxBlockY) {
            return;
        }

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight());

        int mid = 2;
        for (int yy = y - 3 + treeHeight; yy <= y + this.treeHeight - 1; ++yy) {
            if (yy >= maxBlockY) {
                continue;
            }

            for (int xx = x - mid; xx <= x + mid; xx++) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; zz += mid * 2) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && random.nextBoundedInt(2) == 0) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        level.setBlockAt(0, xx, yy, zz, this.getLeafBlock());
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                int zOff = Math.abs(zz - z);
                for (int xx = x - mid; xx <= x + mid; xx += mid * 2) {
                    int xOff = Math.abs(xx - x);
                    if (xOff == mid && zOff == mid && (random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        level.setBlockAt(0, xx, yy, zz, this.getLeafBlock());
                    }
                }
            }

            for (int xx = x - 1; xx <= x + 1; xx++) {
                for (int zz = z - 1; zz <= z + 1; zz++) {
                    if (xx == x && zz == z) {
                        continue;
                    }
                    if (Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        continue;
                    }
                    if (random.nextBoundedInt(20) != 0) {
                        continue;
                    }
                    level.setBlockAt(0, xx, yy, zz, Block.SHROOMLIGHT);
                }
            }
        }

        for (int yy = y - 4 + treeHeight; yy <= y + this.treeHeight - 3; ++yy) {
            if (yy >= maxBlockY) {
                continue;
            }

            for (int xx = x - mid; xx <= x + mid; xx++) {
                for (int zz = z - mid; zz <= z + mid; zz += mid * 2) {
                    if (!Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        if (random.nextBoundedInt(3) == 0) {
                            for (int i = 0; i < random.nextBoundedInt(5); i++) {
                                if (!Block.solid[level.getBlockIdAt(0, xx, yy - i, zz)])
                                    level.setBlockAt(0, xx, yy - i, zz, getLeafBlock());
                            }
                        }
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                for (int xx = x - mid; xx <= x + mid; xx += mid * 2) {
                    if (!Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        if (random.nextBoundedInt(3) == 0) {
                            for (int i = 0; i < random.nextBoundedInt(4); i++) {
                                if (!Block.solid[level.getBlockIdAt(0, xx, yy - i, zz)])
                                    level.setBlockAt(0, xx, yy - i, zz, getLeafBlock());
                            }
                        }
                    }
                }
            }
        }

        for (int xCanopy = x - mid + 1; xCanopy <= x + mid - 1; xCanopy++) {
            for (int zCanopy = z - mid + 1; zCanopy <= z + mid - 1; zCanopy++) {
                if (!Block.solid[level.getBlockIdAt(0, xCanopy, y + treeHeight, zCanopy)])
                    level.setBlockAt(0, xCanopy, y + treeHeight, zCanopy, getLeafBlock());
            }
        }
    }

    @Override
    protected int placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        int lowest = Integer.MIN_VALUE;
        int maxBlockY = level.getHeightRange().getMaxY() - 1;

        level.setBlockAt(0, x, y, z, getTrunkBlock());
        level.setBlockAt(0, x, y - 1, z, Block.NETHERRACK);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int posY = y + yy;
            if (posY >= maxBlockY) {
                continue;
            }
            int blockId = level.getBlockIdAt(0, x, posY, z);
            if (this.overridable(blockId)) {
                level.setBlockAt(0, x, posY, z, this.getTrunkBlock());
                if (lowest == Integer.MIN_VALUE) {
                    lowest = posY;
                }
            }
        }

        return lowest;
    }
}
