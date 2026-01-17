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
public abstract class ItemBoat extends Item {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;
    public static final int MANGROVE = 6;
    public static final int RAFT = 7;
    public static final int CHERRY = 8;
    public static final int PALE_OAK = 9;

    public static final int[] BOATS = {
            OAK_BOAT,
            SPRUCE_BOAT,
            BIRCH_BOAT,
            JUNGLE_BOAT,
            ACACIA_BOAT,
            DARK_OAK_BOAT,
            MANGROVE_BOAT,
            BAMBOO_RAFT,
            CHERRY_BOAT,
            PALE_OAK_BOAT,
    };

    private static final String[] WOOD_TYPE_NAMES = {
            "oak",
            "spruce",
            "birch",
            "jungle",
            "acacia",
            "big_oak",
            "mangrove",
            "bamboo",
            "cherry",
            "pale_oak",
    };

    protected ItemBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getDescriptionId() {
        return "item.boat." + getWoodTypeName() + ".name";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (face != BlockFace.UP || block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(block).isWater()) {
            return false;
        }

        double y = Double.NEGATIVE_INFINITY;
        if (target.isWater() || target.is(Block.BUBBLE_COLUMN)) {
            y = block.getY() - 0.375;
        } else {
            AxisAlignedBB[] bbs = target.getCollisionShape();
            if (bbs != null && bbs.length > 0) {
                for (AxisAlignedBB bb : bbs) {
                    y = Math.max(y, bb.getMaxY());
                }
            } else {
                y = target.getY() + 0.3;
            }
        }

        Entity boat = getEntityFactory().create(
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4),
                Entity.getDefaultNBT(block.getX() + 0.5, y, block.getZ() + 0.5, null, ((float) player.yaw + 90f) % 360, 0)
                        .putByte("woodID", this.getBoatType())
        );
        //TODO: check nearby boats

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

    @Override
    public int getFuelTime() {
        return 1200;
    }

    @Override
    public boolean isBoat() {
        return true;
    }

    protected String getWoodTypeName() {
        int type = getBoatType();
        if (type >= 0 && type < WOOD_TYPE_NAMES.length) {
            return WOOD_TYPE_NAMES[type];
        }
        return WOOD_TYPE_NAMES[OAK];
    }

    public abstract int getBoatType();

    protected EntityFactory getEntityFactory() {
        return EntityBoat::new;
    }
}
