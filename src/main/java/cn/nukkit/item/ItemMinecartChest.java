package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Rail;

public class ItemMinecartChest extends Item {

    public ItemMinecartChest() {
        this(0, 1);
    }

    public ItemMinecartChest(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecartChest(Integer meta, int count) {
        super(CHEST_MINECART, meta, 1, "Minecart with Chest");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (Rail.isRailBlock(target)) {
            Rail.Orientation type = ((BlockRail) target).getOrientation();
            double adjacent = 0.0D;
            if (type.isAscending()) {
                adjacent = 0.5D;
            }
            EntityMinecartChest minecart = new EntityMinecartChest(
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
