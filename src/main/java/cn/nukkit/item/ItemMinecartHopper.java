package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartHopper;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Rail;

public class ItemMinecartHopper extends Item {

    public ItemMinecartHopper() {
        this(0, 1);
    }

    public ItemMinecartHopper(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecartHopper(Integer meta, int count) {
        super(HOPPER_MINECART, meta, count, "Minecart with Hopper");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            EntityMinecartHopper minecart = new EntityMinecartHopper(
                    level.getChunk(target.getFloorX() >> 4, target.getFloorZ() >> 4), Entity.getDefaultNBT(target.getX() + 0.5, target.getY() + 0.0625D + adjacent, target.getZ() + 0.5)
            );

            if (player.isSurvivalLike()) {
                Item item = player.getInventory().getItemInHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }

            minecart.spawnToAll();
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
