package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public abstract class ItemBucketEntity extends Item {
    protected ItemBucketEntity(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (!player.canPlaceOn(target, this)) {
            return false;
        }

        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, block, face, this, get(BUCKET));
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (player.isSurvivalLike()) {
            player.getInventory().setItemInHand(event.getItem());
        }

        getEntityFactory().create(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5))).spawnToAll();
        return true;
    }

    @Override
    public boolean isBucket() {
        return true;
    }

    protected abstract EntityFactory getEntityFactory();
}
