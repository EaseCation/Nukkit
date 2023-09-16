package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

import javax.annotation.Nullable;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Contributed by: larryTheCoder on 2017/7/8.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.block in project Nukkit.
 */
public class BlockRailDetector extends BlockRail {

    public BlockRailDetector() {
        this(0);
        canBePowered = true;
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return isActive() ? 0 : (side == BlockFace.UP ? 15 : 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            updateState(null);
            return type;
        }
        return super.onUpdate(type);
    }

    public void activate(EntityMinecartAbstract minecart) {
        if (!this.isActive()) {
            updateState(minecart);
        }
    }

    protected void updateState(@Nullable EntityMinecartAbstract minecart) {
        boolean wasPowered = isActive();
        boolean isPowered = minecart != null;

        if (!isPowered) {
            for (Entity entity : level.getNearbyEntities(new SimpleAxisAlignedBB(
                    getFloorX() + 0.2,
                    getFloorY(),
                    getFloorZ() + 0.2,
                    getFloorX() + 1 - 0.2,
                    getFloorY() + 1 - 0.2,
                    getFloorZ() + 1 - 0.2))) {
                if (entity instanceof EntityMinecartAbstract) {
                    isPowered = true;
                    break;
                }
            }
        }

        if (isPowered && !wasPowered) {
            setActive(true);
            level.updateAround(this);
            level.updateAround(this.downVec());
        } else if (!isPowered && wasPowered) {
            setActive(false);
            level.updateAround(this);
            level.updateAround(this.downVec());
        }

        if (isPowered) {
            level.scheduleUpdate(this, this, 20);
        }

        level.updateComparatorOutputLevel(this);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.DETECTOR_RAIL, 0, 1)
        };
    }
}
