package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * Created by yescallop on 2016/2/13.
 */
public class ItemBoat extends Item {
    public static final int OAK_BOAT = 1;
    public static final int SPRUCE_BOAT = 1;
    public static final int BIRCH_BOAT = 2;
    public static final int JUNGLE_BOAT = 3;
    public static final int ACACIA_BOAT = 4;
    public static final int DARK_OAK_BOAT = 5;
    public static final int MANGROVE_BOAT = 6;
    public static final int BAMBOO_RAFT = 7;
    public static final int UNDEFINED_BOAT = 8;

    public ItemBoat() {
        this(0, 1);
    }

    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBoat(Integer meta, int count) {
        super(BOAT, meta, count, "Boat");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP || block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(block).isWater()) return false;
        EntityBoat boat = new EntityBoat(
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY() - (target.isWater() ? 0.0625 : 0)))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                        .add(new FloatTag("", 0)))
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
}
