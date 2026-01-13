package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemBucketPowderSnow extends Item {
    public ItemBucketPowderSnow() {
        this(0, 1);
    }

    public ItemBucketPowderSnow(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketPowderSnow(Integer meta, int count) {
        super(POWDER_SNOW_BUCKET, meta, count, "Powder Snow Bucket");
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketPowderSnow.name";
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

        if (!block.canBeReplaced()) {
            return false;
        }

        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, block, face, this, Item.get(BUCKET));
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        level.setExtraBlock(block, Blocks.air(), true, false);
        level.setBlock(block, Block.get(Block.POWDER_SNOW), true);

        if (player.isSurvivalLike()) {
            player.getInventory().setItemInHand(event.getItem());
        }

        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_POWDER_SNOW);
        return true;
    }

    @Override
    public boolean isBucket() {
        return true;
    }
}
