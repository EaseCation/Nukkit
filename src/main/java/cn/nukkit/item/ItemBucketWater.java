package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

@Log4j2
public class ItemBucketWater extends Item {
    public ItemBucketWater() {
        this(0, 1);
    }

    public ItemBucketWater(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketWater(Integer meta, int count) {
        super(WATER_BUCKET, meta, count, "Water Bucket");
    }

    protected ItemBucketWater(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketWater.name";
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

        if (target.canContainWater()) {
            block = target;
        }

        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, block, face, this, Item.get(BUCKET));

        boolean canContainWater = block.canContainWater();
        boolean canBeFlowedInto = block.canBeFlowedInto();
        if (!canContainWater && !canBeFlowedInto) {
            event.setCancelled(true);
        }

        boolean nether = false;
        if (player.getLevel().getDimension() == Dimension.NETHER) {
            event.setCancelled(true);
            nether = true;
        }

        player.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Block placeBlock = Block.get(Block.FLOWING_WATER);
            if (canContainWater && !block.isAir() && !block.isWater()) {
                player.getLevel().setExtraBlock(block, placeBlock, true);
            } else if (canBeFlowedInto) {
                player.getLevel().setExtraBlock(block, Block.get(Block.AIR), true, false);
                player.getLevel().setBlock(block, placeBlock, true);
            } else {
                log.trace("Water bucket empty failed: {} ({})", block.toString(), block.superToString());
            }

            if (player.isSurvivalLike()) {
                player.getInventory().setItemInHand(event.getItem());
            }

            level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_WATER);

            EntityFactory entityFactory = getEntityFactory();
            if (entityFactory != null) {
                entityFactory.create(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5))).spawnToAll();
            }

            return true;
        }

        if (nether) {
            if (player.isSurvivalLike()) {
                player.getInventory().setItemInHand(get(BUCKET));
            }

            player.getLevel().addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_FIZZ);
            player.getLevel().addParticle(new ExplodeParticle(target.add(0.5, 1, 0.5)));
        } else {
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            player.getLevel().sendBlocks(new Player[]{player}, new Block[]{level.getExtraBlock(target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            player.getInventory().sendContents(player);
        }
        return false;
    }

    @Override
    public boolean isBucket() {
        return true;
    }

    @Nullable
    protected EntityFactory getEntityFactory() {
        return null;
    }
}
