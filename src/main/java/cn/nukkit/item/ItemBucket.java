package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.*;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.GameVersion.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class ItemBucket extends Item {

    public static final int EMPTY_BUCKET = 0;
    public static final int MILK_BUCKET = 1;
    public static final int COD_BUCKET = 2;
    public static final int SALMON_BUCKET = 3;
    public static final int TROPICAL_FISH_BUCKET = 4;
    public static final int PUFFERFISH_BUCKET = 5;
    public static final int WATER_BUCKET = 8;
    public static final int LAVA_BUCKET = 10;
    public static final int POWDER_SNOW_BUCKET = 11;
    public static final int AXOLOTL_BUCKET = 12;
    public static final int TADPOLE_BUCKET = 13;
    public static final int UNDEFINED_BUCKET = 14;

    public ItemBucket() {
        this(0, 1);
    }

    public ItemBucket(Integer meta) {
        this(meta, 1);
    }

    public ItemBucket(Integer meta, int count) {
        super(BUCKET, meta, count, getName(meta != null ? meta : 0));
    }

    protected static String getName(int meta) {
        switch (meta) {
            case MILK_BUCKET:
                return "Milk Bucket";
            case COD_BUCKET:
                return "Bucket of Cod";
            case SALMON_BUCKET:
                return "Bucket of Salmon";
            case TROPICAL_FISH_BUCKET:
                return "Bucket of Tropical Fish";
            case PUFFERFISH_BUCKET:
                return "Bucket of Pufferfish";
            case WATER_BUCKET:
                return "Water Bucket";
            case LAVA_BUCKET:
                return "Lava Bucket";
            case POWDER_SNOW_BUCKET:
                return "Powder Snow Bucket";
            case AXOLOTL_BUCKET:
                return "Bucket of Axolotl";
            case TADPOLE_BUCKET:
                return "Bucket of Tadpole";
            case EMPTY_BUCKET:
            default:
                return "Bucket";
        }
    }

    /**
     * @param meta bucket meta
     * @return block ID
     */
    public static int getPlaceBlockFromMeta(int meta) {
        switch (meta) {
            case COD_BUCKET:
            case SALMON_BUCKET:
            case TROPICAL_FISH_BUCKET:
            case PUFFERFISH_BUCKET:
            case WATER_BUCKET:
            case AXOLOTL_BUCKET:
            case TADPOLE_BUCKET:
                return FLOWING_WATER;
            case LAVA_BUCKET:
                return FLOWING_LAVA;
            case POWDER_SNOW_BUCKET:
                return POWDER_SNOW;
            default:
                return AIR;
        }
    }

    /**
     * @param blockId target block ID
     * @return bucket meta
     */
    public static int getMetaFromBlock(int blockId) {
        switch (blockId) {
            case FLOWING_WATER:
            case WATER:
                return WATER_BUCKET;
            case FLOWING_LAVA:
            case LAVA:
                return LAVA_BUCKET;
            case POWDER_SNOW:
                return POWDER_SNOW_BUCKET;
            default:
                return EMPTY_BUCKET;
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.getDamage() == EMPTY_BUCKET ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        if (player.isAdventure()) {
            return false;
        }

        int meta = this.getDamage();
        Block placeBlock = Block.get(getPlaceBlockFromMeta(meta));
        Block extra = level.getExtraBlock(target);

        if (placeBlock.isAir()) {
            if (target.isLiquidSource()) {
                Item result = Item.get(BUCKET, getMetaFromBlock(target.getId()));
                PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, block, face, this, result);
                player.getServer().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, Block.get(AIR), true);

                    /*// When water is removed ensure any adjacent still water is replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Vector3 pos = target.getSideVec(side);
                        Block b = level.getBlock(pos);
                        if (b.getId() == WATER) {
                            level.setBlock(b, Block.get(FLOWING_WATER));
                        } else {
                            b = level.getExtraBlock(pos);
                            if (b.getId() == WATER) {
                                level.setExtraBlock(b, Block.get(FLOWING_WATER));
                            }
                        }
                    }*/

                    if (player.isSurvival()) {
                        if (this.getCount() - 1 <= 0) {
                            player.getInventory().setItemInHand(ev.getItem());
                        } else {
                            Item clone = this.clone();
                            clone.setCount(this.getCount() - 1);
                            player.getInventory().setItemInHand(clone);
                            if (player.getInventory().canAddItem(ev.getItem())) {
                                player.getInventory().addItem(ev.getItem());
                            } else {
                                player.dropItem(ev.getItem());
                            }
                        }
                    }

                    if (target.isLava()) {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_LAVA);
                    } else {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
                    }

                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            } else if (extra.isWaterSource()) {
                Item result = Item.get(BUCKET, getMetaFromBlock(extra.getId()));
                PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, block, face, this, result);
                player.getServer().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    player.getLevel().setExtraBlock(extra, Block.get(AIR), true);

                    /*// When water is removed ensure any adjacent still water is replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Vector3 pos = extra.getSideVec(side);
                        Block b = level.getBlock(pos);
                        if (b.getId() == WATER) {
                            level.setBlock(b, Block.get(FLOWING_WATER));
                        } else {
                            b = level.getExtraBlock(pos);
                            if (b.getId() == WATER) {
                                level.setExtraBlock(b, Block.get(FLOWING_WATER));
                            }
                        }
                    }*/

                    if (player.isSurvival()) {
                        if (this.getCount() - 1 <= 0) {
                            player.getInventory().setItemInHand(ev.getItem());
                        } else {
                            Item clone = this.clone();
                            clone.setCount(this.getCount() - 1);
                            player.getInventory().setItemInHand(clone);
                            if (player.getInventory().canAddItem(ev.getItem())) {
                                player.getInventory().addItem(ev.getItem());
                            } else {
                                player.dropItem(ev.getItem());
                            }
                        }
                    }

                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            } else if (target.getId() == BlockID.POWDER_SNOW) {
                Item result = Item.get(BUCKET, getMetaFromBlock(target.getId()));
                PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, block, face, this, result);
                player.getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    player.getInventory().sendContents(player);
                    return false;
                }

                level.setBlock(target, Blocks.air(), true);

                if (player.isSurvival()) {
                    if (getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem());
                    } else {
                        Item clone = clone();
                        clone.setCount(getCount() - 1);
                        player.getInventory().setItemInHand(clone);
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }
                }

                level.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_BUCKET_FILL_POWDER_SNOW);
            }
        } else if (placeBlock.isLiquid()) {
            if (target.canContainWater() && placeBlock.isWater()) {
                block = target;
            }

            Item result = Item.get(BUCKET, EMPTY_BUCKET);
            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, block, face, this, result);

            boolean canContainWater = block.canContainWater() && placeBlock.isWater();
            boolean canBeFlowedInto = block.canBeFlowedInto();
            if (!canContainWater && !canBeFlowedInto) {
                ev.setCancelled(true);
            }

            boolean nether = false;
            if (player.getLevel().getDimension() == Dimension.NETHER && meta != LAVA_BUCKET) {
                ev.setCancelled(true);
                nether = true;
            }

            player.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                 if (canContainWater && !block.isAir() && !block.isWater()) {
                     player.getLevel().setExtraBlock(block, placeBlock, true);
                 } else if (canBeFlowedInto) {
                     player.getLevel().setBlock(block, placeBlock, true);
                     player.getLevel().setExtraBlock(block, Block.get(AIR), true, false);
                 } else {
                    log.trace("Bucket '{}' empty failed: {} ({})", getName(), block.toString(), block.superToString());
                 }

                if (player.isSurvival()) {
                    if (this.getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem());
                    } else {
                        Item clone = this.clone();
                        clone.setCount(this.getCount() - 1);
                        player.getInventory().setItemInHand(clone);
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }
                }

                if (meta == LAVA_BUCKET) {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
                } else {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_WATER);
                }

                switch (meta) {
                    case COD_BUCKET:
                        Entity cod = new EntityCod(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        cod.spawnToAll();
                        break;
                    case SALMON_BUCKET:
                        Entity salmon = new EntitySalmon(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        salmon.spawnToAll();
                        break;
                    case TROPICAL_FISH_BUCKET:
                        Entity tropicalFish = new EntityTropicalFish(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        tropicalFish.spawnToAll();
                        break;
                    case PUFFERFISH_BUCKET:
                        Entity pufferfish = new EntityPufferfish(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        pufferfish.spawnToAll();
                        break;
                    case AXOLOTL_BUCKET:
                        Entity axolotl = new EntityAxolotl(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        axolotl.spawnToAll();
                        break;
                    case TADPOLE_BUCKET:
                        if (!V1_19_0.isAvailable()) {
                            break;
                        }
                        Entity tadpole = new EntityTadpole(block.getChunk(), Entity.getDefaultNBT(block.add(0.5, 0, 0.5)));
                        tadpole.spawnToAll();
                        break;
                }

                return true;
            } else if (nether) {
                if (!player.isCreative()) {
                    this.setDamage(EMPTY_BUCKET);
                    player.getInventory().setItemInHand(this);
                }

                player.getLevel().addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_FIZZ);
                player.getLevel().addParticle(new ExplodeParticle(target.add(0.5, 1, 0.5)));
            } else {
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{extra}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                player.getInventory().sendContents(player);
            }
        } else if (!placeBlock.isAir()) {
            if (!block.canBeReplaced()) {
                return false;
            }

            Item result = Item.get(BUCKET, EMPTY_BUCKET);
            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, block, face, this, result);
            player.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            level.setExtraBlock(block, Blocks.air(), true, false);
            level.setBlock(block, placeBlock, true);

            if (player.isSurvival()) {
                if (getCount() - 1 <= 0) {
                    player.getInventory().setItemInHand(ev.getItem());
                } else {
                    Item clone = clone();
                    clone.setCount(getCount() - 1);
                    player.getInventory().setItemInHand(clone);
                    if (player.getInventory().canAddItem(ev.getItem())) {
                        player.getInventory().addItem(ev.getItem());
                    } else {
                        player.dropItem(ev.getItem());
                    }
                }
            }

            if (meta == POWDER_SNOW_BUCKET) {
                level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_POWDER_SNOW);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return this.getDamage() == MILK_BUCKET;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (player.isSpectator() || this.getDamage() != MILK_BUCKET) {
            return false;
        }

        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (!player.isCreative()) {
            pop();
            player.getInventory().setItemInHand(get(BUCKET));
        }

        player.removeAllEffects();
        return true;
    }

    @Override
    public boolean canRelease() {
        return getDamage() == MILK_BUCKET;
    }

    @Override
    public int getFuelTime() {
        if (getDamage() != LAVA_BUCKET) {
            return 0;
        }
        return 20000;
    }
}
