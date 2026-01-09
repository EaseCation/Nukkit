package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockFrogSpawn extends BlockFlowable {
    BlockFrogSpawn() {

    }

    @Override
    public int getId() {
        return FROG_SPAWN;
    }

    @Override
    public String getName() {
        return "Frog Spawn";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!target.isWaterSource() && (target.isAir() || !target.canContainWater() || !level.getExtraBlock(target).isWaterSource())) {
            return false;
        }

        Block above = target.up();
        if (!above.isAir()) {
            return false;
        }

        if (!level.setBlock(above, this, true)) {
            return false;
        }

        level.scheduleRandomUpdate(above, ThreadLocalRandom.current().nextInt(3600, 12000));
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = down();
            if (below.isWaterSource() || !below.isAir() && below.canContainWater() && level.getExtraBlock(below).isWaterSource()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            //TODO: hatch
            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(3600, 12000));
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
