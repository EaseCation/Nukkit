package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayDeque;
import java.util.Queue;

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
        if (block.isWater() && performWaterAbsorb(block)) { //TODO: addToRandomTickingQueue(1)
            level.setBlock(block, Block.get(WET_SPONGE), true, true);

            level.addParticle(new DestroyBlockParticle(this.add(0.5, 1, 0.5), Block.get(BlockID.WATER)));
            return true;
        }

        return level.setBlock(this, this, true, true);
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
