package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

/**
 * Created by yescallop on 2016/2/13.
 */
public class ItemBoat extends Item {
    public static final int OAK_BOAT = 0;
    public static final int SPRUCE_BOAT = 1;
    public static final int BIRCH_BOAT = 2;
    public static final int JUNGLE_BOAT = 3;
    public static final int ACACIA_BOAT = 4;
    public static final int DARK_OAK_BOAT = 5;
    public static final int MANGROVE_BOAT = 6;
    public static final int BAMBOO_RAFT = 7;
    public static final int CHERRY_BOAT = 8;
    public static final int UNDEFINED_BOAT = 9;

    public ItemBoat() {
        this(0, 1);
    }

    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBoat(Integer meta, int count) {
        super(BOAT, meta, 1, "Boat");
    }

    protected ItemBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP || block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(block).isWater()) {
            return false;
        }

        double y;
        if (target.isWater()) {
            y = block.getY() - 0.375;
        } else {
            AxisAlignedBB bb = target.getBoundingBox();
            if (bb != null) {
                y = bb.getMaxY();
            } else {
                y = target.getY() + 0.3;
            }
        }

        Entity boat = getEntityFactory().create(
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4),
                Entity.getDefaultNBT(block.getX() + 0.5, y, block.getZ() + 0.5, null, ((float) player.yaw + 90f) % 360, 0)
                        .putByte("woodID", this.getDamage())
        );

        if (player.isSurvivalLike()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    protected EntityFactory getEntityFactory() {
        return EntityBoat::new;
    }
}
