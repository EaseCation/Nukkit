package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    protected boolean overridable(int id) {
        switch (id) {
            case Block.AIR:
            case Block.SAPLING:
            case Block.OAK_LOG:
            case Block.SPRUCE_LOG:
            case Block.BIRCH_LOG:
            case Block.JUNGLE_LOG:
            case Block.LEAVES:
            case Block.SNOW_LAYER:
            case Block.ACACIA_LOG:
            case Block.DARK_OAK_LOG:
            case Block.LEAVES2:
                return true;
            default:
                return false;
        }
    }

    public int getType() {
        return 0;
    }

    public int getTrunkBlock() {
        return Block.OAK_LOG;
    }

    public int getLeafBlock() {
        return Block.LEAVES;
    }

    public int getTreeHeight() {
        return 7;
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        growTree(level, x, y, z, random, 0);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, int type) {
        ObjectTree tree;
        switch (type) {
            case BlockSapling.SPRUCE:
                tree = new ObjectSpruceTree();
                break;
            case BlockSapling.BIRCH:
                tree = new ObjectBirchTree();
                break;
            case BlockSapling.JUNGLE:
                tree = new ObjectJungleTree();
                break;
            case BlockSapling.BIRCH_TALL:
                tree = new ObjectTallBirchTree();
                break;
            case BlockSapling.OAK:
            default:
                tree = new ObjectOakTree();
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }


    public boolean canPlaceObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
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

    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

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
                    }
                }
            }
        }
    }

    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockAt(0, x, y - 1, z, Block.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int blockId = level.getBlockIdAt(0, x, y + yy, z);
            if (this.overridable(blockId)) {
                level.setBlockAt(0, x, y + yy, z, this.getTrunkBlock());
            }
        }
    }
}
