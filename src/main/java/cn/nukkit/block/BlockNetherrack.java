package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/26 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockNetherrack extends BlockSolid {

    public BlockNetherrack() {
    }

    @Override
    public int getId() {
        return NETHERRACK;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!onFertilized()) {
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

    @Override
    public boolean isFertilizable() {
        return true;
    }

    protected boolean onFertilized() {
        boolean hasCrimson = false;
        boolean hasWarped = false;

        int thisX = getFloorX();
        int thisY = getFloorY();
        int thisZ = getFloorZ();
        POSITIONS:
        for (int x = thisX - 1; x <= thisX + 1; x++) {
            for (int z = thisZ - 1; z <= thisZ + 1; z++) {
                for (int y = thisY - 1; y <= thisY + 1; y++) {
                    if (x == thisX && z == thisZ && y == thisY - 1) {
                        continue;
                    }

                    Block block = level.getBlock(x, y, z);
                    int id = block.getId();
                    if (id == CRIMSON_NYLIUM) {
                        hasCrimson = true;
                    } else if (id == WARPED_NYLIUM) {
                        hasWarped = true;
                    }

                    if (hasCrimson && hasWarped) {
                        break POSITIONS;
                    }
                }
            }
        }

        if (hasCrimson && hasWarped) {
            level.setBlock(this, get(ThreadLocalRandom.current().nextBoolean() ? CRIMSON_NYLIUM : WARPED_NYLIUM), true);
            return true;
        } else if (hasCrimson) {
            level.setBlock(this, get(CRIMSON_NYLIUM), true);
            return true;
        } else if (hasWarped) {
            level.setBlock(this, get(WARPED_NYLIUM), true);
            return true;
        }
        return false;
    }
}
