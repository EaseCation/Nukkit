package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;

public class ObjectSwampTree extends TreeGenerator {

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = Block.get(BlockID.LOG, BlockWood.OAK);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = Block.get(BlockID.LEAVES, BlockLeaves.OAK);

    @Override
    public boolean generate(ChunkManager worldIn, NukkitRandom rand, BlockVector3 vectorPosition) {
        BlockVector3 position = new BlockVector3(vectorPosition.getX(), vectorPosition.getY(), vectorPosition.getZ());

        int i = rand.nextBoundedInt(4) + 5;
        boolean flag = true;

        HeightRange heightRange = worldIn.getHeightRange();
        if (position.getY() > heightRange.getMinY() && position.getY() + i + 1 <= heightRange.getMaxY()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 3;
                }

                BlockVector3 pos2 = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= heightRange.getMinY() && j < heightRange.getMaxY()) {
                            pos2.setComponents(l, j, i1);
                            if (!this.canGrowInto(worldIn.getBlockIdAt(0, pos2.x, pos2.y, pos2.z))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockVector3 down = position.down();
                int block = worldIn.getBlockIdAt(0, down.x, down.y, down.z);

                if ((block == Block.GRASS_BLOCK || block == Block.DIRT) && position.getY() < heightRange.getMaxY() - i - 1) {
                    this.setDirtAt(worldIn, down);

                    for (int k1 = position.getY() - 3 + i; k1 <= position.getY() + i; ++k1) {
                        int j2 = k1 - (position.getY() + i);
                        int l2 = 2 - j2 / 2;

                        for (int j3 = position.getX() - l2; j3 <= position.getX() + l2; ++j3) {
                            int k3 = j3 - position.getX();

                            for (int i4 = position.getZ() - l2; i4 <= position.getZ() + l2; ++i4) {
                                int j1 = i4 - position.getZ();

                                if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || rand.nextBoundedInt(2) != 0 && j2 != 0) {
                                    BlockVector3 blockpos = new BlockVector3(j3, k1, i4);
                                    int id = worldIn.getBlockIdAt(0, blockpos.x, blockpos.y, blockpos.z);

                                    if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int l1 = 0; l1 < i; ++l1) {
                        BlockVector3 up = position.up(l1);
                        int id = worldIn.getBlockIdAt(0, up.x, up.y, up.z);

                        if (id == Block.AIR || id == Block.LEAVES || id == Block.FLOWING_WATER || id == Block.WATER) {
                            this.setBlockAndNotifyAdequately(worldIn, up, this.metaWood);
                        }
                    }

                    for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                        int k2 = i2 - (position.getY() + i);
                        int i3 = 2 - k2 / 2;
                        BlockVector3 pos2 = new BlockVector3();

                        for (int l3 = position.getX() - i3; l3 <= position.getX() + i3; ++l3) {
                            for (int j4 = position.getZ() - i3; j4 <= position.getZ() + i3; ++j4) {
                                pos2.setComponents(l3, i2, j4);

                                if (worldIn.getBlockIdAt(0, pos2.x, pos2.y, pos2.z) == Block.LEAVES) {
                                    BlockVector3 blockpos2 = pos2.west();
                                    BlockVector3 blockpos3 = pos2.east();
                                    BlockVector3 blockpos4 = pos2.north();
                                    BlockVector3 blockpos1 = pos2.south();

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(0, blockpos2.x, blockpos2.y, blockpos2.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos2, 8);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(0, blockpos3.x, blockpos3.y, blockpos3.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos3, 2);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(0, blockpos4.x, blockpos4.y, blockpos4.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos4, 1);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(0, blockpos1.x, blockpos1.y, blockpos1.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos1, 4);
                                    }
                                }
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void addVine(ChunkManager worldIn, BlockVector3 pos, int meta) {
        this.setBlockAndNotifyAdequately(worldIn, pos, Block.get(BlockID.VINE, meta));
    }

    private void addHangingVine(ChunkManager worldIn, BlockVector3 pos, int meta) {
        this.addVine(worldIn, pos, meta);
        int i = 4;

        for (pos = pos.down(); i > 0 && worldIn.getBlockIdAt(0, pos.x, pos.y, pos.z) == Block.AIR; --i) {
            this.addVine(worldIn, pos, meta);
            pos = pos.down();
        }
    }
}
