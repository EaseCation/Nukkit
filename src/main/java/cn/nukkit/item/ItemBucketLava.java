package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ItemBucketLava extends Item {
    public ItemBucketLava() {
        this(0, 1);
    }

    public ItemBucketLava(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketLava(Integer meta, int count) {
        super(LAVA_BUCKET, meta, count, "Lava Bucket");
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketLava.name";
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

        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, block, face, this, Item.get(BUCKET));

        boolean canBeFlowedInto = block.canBeFlowedInto();
        if (!canBeFlowedInto) {
            event.setCancelled(true);
        }

        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getExtraBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            player.getInventory().sendContents(player);
            return false;
        }

        if (canBeFlowedInto) {
            player.getLevel().setExtraBlock(block, Block.get(Block.AIR), true, false);
            player.getLevel().setBlock(block, Block.get(Block.FLOWING_LAVA), true);
        } else {
            log.trace("Lava bucket empty failed: {} ({})", block.toString(), block.superToString());
        }

        if (player.isSurvivalLike()) {
            player.getInventory().setItemInHand(event.getItem());
        }

        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 20000;
    }

    @Override
    public boolean isBucket() {
        return true;
    }
}
