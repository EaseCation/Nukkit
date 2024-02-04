package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;

public class ObjectJungleBigTree extends HugeTreesGenerator {
    public ObjectJungleBigTree(int baseHeightIn, int extraRandomHeight, Block woodMetadata, Block leavesMetadata) {
        super(baseHeightIn, extraRandomHeight, woodMetadata, leavesMetadata);
    }

    public boolean generate(ChunkManager level, NukkitRandom rand, BlockVector3 position) {
        int height = this.getHeight(rand);

        if (!this.ensureGrowable(level, rand, position, height)) {
            return false;
        } else {
            this.createCrown(level, position.up(height), 2);

            for (int j = position.getY() + height - 2 - rand.nextBoundedInt(4); j > position.getY() + height / 2; j -= 2 + rand.nextBoundedInt(4)) {
                float f = rand.nextFloat() * ((float) Math.PI * 2F);
                int k = (int) (position.getX() + (0.5F + Mth.cos(f) * 4.0F));
                int l = (int) (position.getZ() + (0.5F + Mth.sin(f) * 4.0F));

                for (int i1 = 0; i1 < 5; ++i1) {
                    k = (int) (position.getX() + (1.5F + Mth.cos(f) * (float) i1));
                    l = (int) (position.getZ() + (1.5F + Mth.sin(f) * (float) i1));
                    this.setBlockAndNotifyAdequately(level, new BlockVector3(k, j - 3 + i1 / 2, l), this.woodMetadata);
                }

                int j2 = 1 + rand.nextBoundedInt(2);

                for (int k1 = j - j2; k1 <= j; ++k1) {
                    int l1 = k1 - j;
                    this.growLeavesLayer(level, new BlockVector3(k, k1, l), 1 - l1);
                }
            }

            for (int i2 = 0; i2 < height; ++i2) {
                BlockVector3 blockpos = position.up(i2);

                if (this.canGrowInto(level.getBlockIdAt(0, blockpos.x, blockpos.y, blockpos.z))) {
                    this.setBlockAndNotifyAdequately(level, blockpos, this.woodMetadata);

                    if (i2 > 0) {
                        this.placeVine(level, rand, blockpos.west(), 8);
                        this.placeVine(level, rand, blockpos.north(), 1);
                    }
                }

                if (i2 < height - 1) {
                    BlockVector3 blockpos1 = blockpos.east();

                    if (this.canGrowInto(level.getBlockIdAt(0, blockpos1.x, blockpos1.y, blockpos1.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos1, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos1.east(), 2);
                            this.placeVine(level, rand, blockpos1.north(), 1);
                        }
                    }

                    BlockVector3 blockpos2 = blockpos.south().east();

                    if (this.canGrowInto(level.getBlockIdAt(0, blockpos2.x, blockpos2.y, blockpos2.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos2, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos2.east(), 2);
                            this.placeVine(level, rand, blockpos2.south(), 4);
                        }
                    }

                    BlockVector3 blockpos3 = blockpos.south();

                    if (this.canGrowInto(level.getBlockIdAt(0, blockpos3.x, blockpos3.y, blockpos3.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos3, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos3.west(), 8);
                            this.placeVine(level, rand, blockpos3.south(), 4);
                        }
                    }
                }
            }

            return true;
        }
    }

    private void placeVine(ChunkManager level, NukkitRandom random, BlockVector3 pos, int meta) {
        if (random.nextBoundedInt(3) > 0 && level.getBlockIdAt(0, pos.x, pos.y, pos.z) == 0) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.VINE, meta));
        }
    }

    private void createCrown(ChunkManager level, BlockVector3 pos, int i1) {
        for (int j = -2; j <= 0; ++j) {
            this.growLeavesLayerStrict(level, pos.up(j), i1 + 1 - j);
        }
    }
}
