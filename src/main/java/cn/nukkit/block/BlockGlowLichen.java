package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockGlowLichen extends BlockMultiface {
    BlockGlowLichen() {

    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SHEARS | BlockToolType.SWORD;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return super.getDrops(item, player);
        }
        return new Item[0];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            boolean success = false;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            List<BlockFace> randomFaces = new ObjectArrayList<>(BlockFace.getValues());
            Collections.shuffle(randomFaces, random);
            TEST:
            for (BlockFace blockFace : randomFaces) {
                if (!hasFace(blockFace)) {
                    continue;
                }
                List<BlockFace> randomSides = new ObjectArrayList<>(BlockFace.getValues());
                Collections.shuffle(randomSides, random);
                for (BlockFace side : randomSides) {
                    if (side.getAxis() == blockFace.getAxis() || hasFace(side)) {
                        continue;
                    }
                    Block neighbor = getSide(side);
                    BlockFace oppositeSide = side.getOpposite();
                    if (canBeSupportedBy(neighbor, oppositeSide)) {
                        addFace(side);
                        level.setBlock(this, this, true);
                    } else if (!trySpreadTo(neighbor, blockFace)
                            && !trySpreadTo(neighbor.getSide(blockFace), oppositeSide)) {
                        continue;
                    }
                    success = true;
                    break TEST;
                }
            }
            if (!success) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    private boolean trySpreadTo(Block block, BlockFace face) {
        if (!canBeSupportedBy(block.getSide(face), face.getOpposite())) {
            return false;
        }
        Block newBlock;
        if (block.isAir()) {
            newBlock = get(GLOW_LICHEN);
        } else if (block.isWaterSource()) {
            newBlock = get(GLOW_LICHEN);
            level.setExtraBlock(block, block, true, false);
        } else if (block.is(GLOW_LICHEN) && !hasFace(block, face)) {
            newBlock = block;
        } else {
            return false;
        }
        addFace(newBlock, face);
        level.setBlock(block, newBlock, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
