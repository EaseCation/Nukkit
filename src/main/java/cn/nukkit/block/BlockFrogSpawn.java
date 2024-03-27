package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockFrogSpawn extends BlockFlowable {
    public BlockFrogSpawn() {
        this(0);
    }

    public BlockFrogSpawn(int meta) {
        super(0);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block below = down();
        if (!below.isWaterSource() && (below.isAir() || !below.canContainWater() || !level.getExtraBlock(below).isWaterSource())) {
            return false;
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(3600, 12000));
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = down();
            if (below.isWaterSource() || !below.isAir() && below.canContainWater() && level.getExtraBlock(below).isWaterSource()) {
                return 0;
            }

            level.useBreakOn(this);
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

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
