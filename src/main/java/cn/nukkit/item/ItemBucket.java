package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBucket extends Item {

    static final int TYPE_MILK_BUCKET = 1;
    static final int TYPE_COD_BUCKET = 2;
    static final int TYPE_SALMON_BUCKET = 3;
    static final int TYPE_TROPICAL_FISH_BUCKET = 4;
    static final int TYPE_PUFFERFISH_BUCKET = 5;
    static final int TYPE_WATER_BUCKET = 8;
    static final int TYPE_LAVA_BUCKET = 10;
    static final int TYPE_POWDER_SNOW_BUCKET = 11;
    static final int TYPE_AXOLOTL_BUCKET = 12;
    static final int TYPE_TADPOLE_BUCKET = 13;

    public static final int[] BUCKETS = {
            BUCKET,
            MILK_BUCKET,
            COD_BUCKET,
            SALMON_BUCKET,
            TROPICAL_FISH_BUCKET,
            PUFFERFISH_BUCKET,
            BUCKET,
            BUCKET,
            WATER_BUCKET,
            BUCKET,
            LAVA_BUCKET,
            POWDER_SNOW_BUCKET,
            AXOLOTL_BUCKET,
            TADPOLE_BUCKET,
    };

    public ItemBucket() {
        this(0, 1);
    }

    public ItemBucket(Integer meta) {
        this(meta, 1);
    }

    public ItemBucket(Integer meta, int count) {
        super(BUCKET, meta, count, "Bucket");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
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

        boolean waterlogged = false;
        int resultItemId = -1;
        if (target.is(Block.POWDER_SNOW)) {
            resultItemId = Item.POWDER_SNOW_BUCKET;
        } else if (target.isWaterSource()) {
            resultItemId = Item.WATER_BUCKET;
        } else if (target.isLava() && target.isLiquidSource()) {
            resultItemId = Item.LAVA_BUCKET;
        } else if (!target.isAir() && target.canContainWater()) {
            Block extra = target.level.getExtraBlock(target);
            if (extra.isWaterSource()) {
                waterlogged = true;
                resultItemId = Item.WATER_BUCKET;
            }
        }
        if (resultItemId == -1) {
            return false;
        }
        Item result = Item.get(resultItemId);

        PlayerBucketFillEvent event = new PlayerBucketFillEvent(player, block, face, this, result);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getExtraBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            player.getInventory().sendContents(player);
            return false;
        }

        level.setExtraBlock(target, Blocks.air(), true, waterlogged);
        if (!waterlogged) {
            level.setBlock(target, Blocks.air(), true);
        }

        if (player.isSurvivalLike()) {
            if (getCount() - 1 <= 0) {
                player.getInventory().setItemInHand(event.getItem());
            } else {
                Item clone = clone();
                clone.pop();
                player.getInventory().setItemInHand(clone);

                for (Item item : player.getInventory().addItem(event.getItem())) {
                    player.dropItem(item);
                }
            }
        }

        switch (resultItemId) {
            case Item.WATER_BUCKET -> level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
            case Item.LAVA_BUCKET -> level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_LAVA);
            case Item.POWDER_SNOW_BUCKET -> level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_POWDER_SNOW);
        }

        return true;
    }

    @Override
    public boolean isBucket() {
        return true;
    }
}
