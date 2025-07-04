package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSponge extends BlockSolid {

    public static final int DRY = 0;
    public static final int WET = 1;
    private static final String[] NAMES = new String[]{
            "Sponge",
            "Wet sponge"
    };

    public BlockSponge() {
        this(0);
    }

    public BlockSponge(int meta) {
        super(meta);
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
        return NAMES[this.getDamage() & 0b1];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (this.getDamage() == WET && level.getDimension() == Dimension.NETHER) {
            level.setBlock(block, Block.get(BlockID.SPONGE, DRY), true, true);

            this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);
            return true;
        }

        if (this.getDamage() == DRY && block instanceof BlockWater && performWaterAbsorb(block)) { //TODO: addToRandomTickingQueue(1)
            level.setBlock(block, Block.get(BlockID.SPONGE, WET), true, true);

            level.addParticle(new DestroyBlockParticle(this.add(0.5, 1, 0.5), Block.get(BlockID.WATER)));
            return true;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getDamage() != DRY) {
            return 0;
        }
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
}
