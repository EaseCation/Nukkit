package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSponge extends BlockSolid {
    public static final int[] SPONGES = {
            SPONGE,
            WET_SPONGE,
    };

    public static final int DRY = 0;
    public static final int WET = 1;

    BlockSponge() {

    }

    @Override
    public int getId() {
        return SPONGE;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public String getName() {
        return "Sponge";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!level.setBlock(this, this, true)) {
            return false;
        }

        level.scheduleUpdate(this, 1);
        return true;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.15f;
    }

    private boolean performWaterAbsorb(Block block) {
        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.getValues()) {
                Block faceBlock = entry.block.getSide(face);
                Block extraBlock;
                if (faceBlock.isWater()) {
                    this.level.setBlock(faceBlock, Block.get(BlockID.AIR), true);
                    ++waterRemoved;
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                } else if (faceBlock.canContainWater() && !faceBlock.is(BARRIER) && (extraBlock = level.getExtraBlock(faceBlock)).isWater()) {
                    level.setExtraBlock(extraBlock, get(AIR), true);
                    ++waterRemoved;
                    if (entry.distance < 6) {
                        entries.add(new Entry(extraBlock, entry.distance + 1));
                    }
                } else if (faceBlock.getId() == BlockID.AIR) {
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                }
            }
        }
        return waterRemoved > 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            boolean hasWater = false;
            for (BlockFace face : BlockFace.getValues()) {
                Block block = getSide(face);
                if (block.isWater()) {
                    hasWater = true;
                    break;
                }
            }
            if (!hasWater) {
                return 0;
            }

            if (!performWaterAbsorb(this)) {
                return 0;
            }

            if (isWarmBiome()) {
                evaporateWater(true);
            } else {
                Block block = get(WET_SPONGE);
                level.setBlock(this, block, true, true);

                if (isWarmBiome()) {
                    setShouldDry(block);
                }
            }

            level.addParticle(new DestroyBlockParticle(add(0.5, 1, 0.5), get(WATER)));
            return type;
        }

        return 0;
    }

    protected void evaporateWater(boolean effectOnly) {
        if (!effectOnly) {
            level.setBlock(this, get(SPONGE), true);
        }
        level.addLevelEvent(add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);
    }

    protected static void setShouldDry(Block block) {
        if (block.level.isRandomBlockTickPending(block, block)) {
            return;
        }
        block.level.scheduleRandomUpdate(block, 20 * ThreadLocalRandom.current().nextInt(90, 180));
    }

    protected boolean isWarmBiome() {
        Biome biome = level.getBiome(getFloorX(), getFloorY(), getFloorZ());
//        return biome.getTemperature() >= 1; //TODO
        return !biome.canRain() && !biome.canSnow() && !biome.isFreezing() && level.getDimension() == Dimension.OVERWORLD;
    }

    private static class Entry {
        private final Block block;
        private final int distance;

        public Entry(Block block, int distance) {
            this.block = block;
            this.distance = distance;
        }
    }

    @Override
    public String getDescriptionId() {
        return "tile.sponge.dry.name";
    }
}
