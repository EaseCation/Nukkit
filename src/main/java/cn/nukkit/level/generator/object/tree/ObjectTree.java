package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.RandomSource;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    protected boolean overridable(int id) {
        switch (id) {
            case Block.AIR:
            case Block.SNOW_LAYER:
                return true;
        }
        Block ub = Block.getUnsafe(id);
        if (ub.isLog() || ub.isLeaves() || ub.isSapling()) {
            return true;
        }
        return false;
    }

    public int getType() {
        return 0;
    }

    public int getTrunkBlock() {
        return Block.OAK_LOG;
    }

    public int getLeafBlock() {
        return Block.OAK_LEAVES;
    }

    public int getTreeHeight() {
        return 7;
    }

    public static void growTree(ChunkManager level, int x, int y, int z, RandomSource random) {
        growTree(level, x, y, z, random, 0);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, RandomSource random, int type) {
        growTree(level, x, y, z, random, type, false);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, RandomSource random, int type, float beehiveProbability) {
        growTree(level, x, y, z, random, type, beehiveProbability, false);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, RandomSource random, int type, boolean snowy) {
        growTree(level, x, y, z, random, type, 0, snowy);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, RandomSource random, int type, float beehiveProbability, boolean snowy) {
        ObjectTree tree;
        switch (type) {
            case BlockSapling.SPRUCE:
                tree = new ObjectSpruceTree(snowy);
                break;
            case BlockSapling.BIRCH:
                tree = new ObjectBirchTree(beehiveProbability);
                break;
            case BlockSapling.JUNGLE:
                tree = new ObjectJungleTree();
                break;
            case BlockSapling.BIRCH_TALL:
                tree = new ObjectTallBirchTree(beehiveProbability);
                break;
            case BlockSapling.OAK:
            default:
                tree = new ObjectOakTree(beehiveProbability);
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }

    public boolean canPlaceObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        int height = this.getTreeHeight() + 2;
        if (!level.getHeightRange().isValidBlockY(y + height)) {
            return false;
        }

        int radiusToCheck = 0;
        for (int yy = 0; yy <= height; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable(level.getBlockIdAt(0, x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        int lowestLeaves = Integer.MIN_VALUE;
        int lowestLog = this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            int yOff = yy - (y + this.getTreeHeight());
            int mid = 1 - yOff / 2;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(0, xx, yy, zz)]) {
                        level.setBlockAt(0, xx, yy, zz, this.getLeafBlock(), this.getType());
                        if (lowestLeaves == Integer.MIN_VALUE) {
                            lowestLeaves = yy;
                        }
                    }
                }
            }
        }

        if (canPlaceBeehive(random) && lowestLeaves != Integer.MIN_VALUE && lowestLog != Integer.MIN_VALUE) {
            placeBeehive(level, x, z, random, lowestLeaves, lowestLog);
        }
    }

    protected int placeTrunk(ChunkManager level, int x, int y, int z, RandomSource random, int trunkHeight) {
        int lowest = Integer.MIN_VALUE;

        // The base dirt block
        level.setBlockAt(0, x, y - 1, z, Block.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int posY = y + yy;
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

    protected void placeBeehive(ChunkManager level, int x, int z, RandomSource random, int lowestLeaves, int lowestLog) {
        int y = Math.max(lowestLeaves - 1, lowestLog + 1);
        IntList sides = new IntArrayList(3);
        if (level.getBlockIdAt(0, x + 1, y, z) == Block.AIR) {
            sides.add(3);
        }
        if (level.getBlockIdAt(0, x, y, z + 1) == Block.AIR) {
            sides.add(0);
        }
        if (level.getBlockIdAt(0, x - 1, y, z) == Block.AIR) {
            sides.add(1);
        }
        if (sides.isEmpty()) {
            return;
        }
        switch (sides.getInt(random.nextInt(sides.size()))) {
            case 0 -> level.setBlockAt(0, x, y, z + 1, Block.BEE_NEST);
            case 1 -> level.setBlockAt(0, x - 1, y, z, Block.BEE_NEST);
            case 3 -> level.setBlockAt(0, x + 1, y, z, Block.BEE_NEST);
        }
    }

    protected boolean canPlaceBeehive(RandomSource random) {
        return false;
    }
}
