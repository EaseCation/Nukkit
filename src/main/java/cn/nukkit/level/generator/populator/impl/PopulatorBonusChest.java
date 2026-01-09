package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.level.generator.task.PlaceBlockEntityTask;
import cn.nukkit.loot.LootTableNames;
import cn.nukkit.math.RandomSource;

public class PopulatorBonusChest extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        HeightRange heightRange = level.getHeightRange();
        for (int xx = 3; xx <= 10; xx++) {
            for (int zz = 3; zz <= 10; zz++) {
                for (int y = heightRange.getMaxY() - 1 - 1; y >= heightRange.getMinY(); y--) {
                    if (PopulatorHelpers.isNonSolid(chunk.getBlockId(0, xx, y, zz))) {
                        continue;
                    }

                    chunk.setBlock(0, xx, y + 1, zz, Block.CHEST, 2);
                    Server.getInstance().getScheduler().scheduleTask(new PlaceBlockEntityTask(chunk,
                            BlockEntity.getDefaultCompound(chunkX << 4 | xx, y + 1, chunkZ << 4 | zz, BlockEntity.CHEST)
                                    .putString("LootTable", LootTableNames.CHESTS_SPAWN_BONUS_CHEST)
                                    .putInt("LootTableSeed", random.nextInt())));

                    if (chunk.getBlockId(0, xx - 1, y + 1, zz) == Block.AIR && Block.solid[chunk.getBlockId(0, xx - 1, y, zz)]) {
                        chunk.setBlock(0, xx - 1, y + 1, zz, Block.TORCH, 5);
                    }
                    if (chunk.getBlockId(0, xx + 1, y + 1, zz) == Block.AIR && Block.solid[chunk.getBlockId(0, xx + 1, y, zz)]) {
                        chunk.setBlock(0, xx + 1, y + 1, zz, Block.TORCH, 5);
                    }
                    if (chunk.getBlockId(0, xx, y + 1, zz - 1) == Block.AIR && Block.solid[chunk.getBlockId(0, xx, y, zz - 1)]) {
                        chunk.setBlock(0, xx, y + 1, zz - 1, Block.TORCH, 5);
                    }
                    if (chunk.getBlockId(0, xx, y + 1, zz + 1) == Block.AIR && Block.solid[chunk.getBlockId(0, xx, y, zz + 1)]) {
                        chunk.setBlock(0, xx, y + 1, zz + 1, Block.TORCH, 5);
                    }
                    return;
                }
            }
        }
    }
}
