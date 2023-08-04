package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDripstonePointed;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockSerializer;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.block.SupportType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends Entity {

    public static final int NETWORK_ID = EntityID.FALLING_BLOCK;

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return block.getId() == BlockID.ANVIL;
    }

    private Block block;

    public boolean sync;

    public EntityFallingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        Block block;
        CompoundTag fallingBlock = namedTag.getCompound("FallingBlock", null);
        if (fallingBlock != null) {
            block = NBTIO.getBlockHelper(fallingBlock);
        } else {
            int id;
            int meta = namedTag.getByte("Data");

            if (namedTag.contains("Tile")) {
                id = namedTag.getInt("Tile");
                namedTag.remove("Tile");
            } else {
                id = namedTag.getInt("TileID");
                namedTag.remove("TileID");
            }

            if (id != Block.AIR) {
                block = NBTIO.getBlockHelper(new CompoundTag()
                        .putString("name", GlobalBlockPalette.getNameByBlockId(id))
                        .putShort("val", meta));

                if (block.getId() == Block.INFO_UPDATE) {
                    block = null;
                } else {
                    namedTag.putCompound("FallingBlock", BlockSerializer.serialize(block));
                }
            } else {
                block = null;
            }
        }
        this.block = block;
        if (block == null || block.isAir()) {
            close();
            return;
        }

        this.fireProof = true;
        this.setDataFlag(DATA_FLAG_FIRE_IMMUNE, true, false);

        this.dataProperties.putLong(DATA_NUKKIT_FLAGS, NUKKIT_FLAG_VARIANT_BLOCK); //HACK: multi-version handle
        setDataProperty(new IntEntityData(DATA_VARIANT, (this.block.getId() << Block.BLOCK_META_BITS) | this.block.getDamage()), false);
    }

    public boolean canCollideWith(Entity entity) {
        return block.getId() == BlockID.ANVIL && super.canCollideWith(entity);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            Vector3 pos = new Vector3(x - 0.5, y, z - 0.5).round();

            if (onGround) {
                close();
                Block block = level.getBlock(pos);

                Vector3 floorPos = new Vector3(x - 0.5, y, z - 0.5).floor();
                Block floorBlock = this.level.getBlock(floorPos);
                if (this.block.getId() == Block.POINTED_DRIPSTONE) {
                    getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_POINTED_DRIPSTONE_LAND);

                    float damage = (float) Math.min(40, Math.max(0, (highestPosition - y) * 2));
                    if (damage > 0) {
                        Entity[] entities = level.getCollidingEntities(getBoundingBox(), this);
                        for (Entity entity : entities) {
                            if (!(entity instanceof EntityLiving) || highestPosition <= y) {
                                continue;
                            }
                            entity.attack(new EntityDamageByBlockEvent(Block.get(Block.POINTED_DRIPSTONE, this.block.getDamage()), entity, DamageCause.STALACTITE, damage));
                        }
                    }

                    if (level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        level.dropItem(this, Item.get(Block.getItemId(Block.POINTED_DRIPSTONE), BlockDripstonePointed.HANGING_BIT));
                    }
                } else if (this.block.getId() == Block.SNOW_LAYER && floorBlock.getId() == Block.SNOW_LAYER && (floorBlock.getDamage() & 0x7) != 0x7) {
                    int mergedHeight = (floorBlock.getDamage() & 0x7) + 1 + (this.block.getDamage() & 0x7) + 1;
                    if (mergedHeight > 8) {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, 0x7));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            placeBlock(floorPos, event.getTo());

                            Vector3 abovePos = floorPos.up();
                            Block aboveBlock = this.level.getBlock(abovePos);
                            if (aboveBlock.getId() == Block.AIR) {
                                EntityBlockChangeEvent event2 = new EntityBlockChangeEvent(this, aboveBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 8 - 1));
                                this.server.getPluginManager().callEvent(event2);
                                if (!event2.isCancelled()) {
                                    placeBlock(abovePos, event2.getTo());
                                }
                            }
                        }
                    } else {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 1));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            placeBlock(floorPos, event.getTo());
                        }
                    }
                } else if (this.block.getId() == BlockID.SCAFFOLDING && floorBlock.getId() == BlockID.SCAFFOLDING) {
                    Block top = floorBlock;
                    Block up;
                    while ((up = top.up()).getId() == BlockID.SCAFFOLDING) {
                        top = up;
                    }
                    if (top.getFloorY() >= level.getMaxHeight()) {
                        if (level.gameRules.getBoolean(GameRule.DO_ENTITY_DROPS)) {
                            level.dropItem(this, Item.get(Block.getItemId(this.block.getId()), this.block.getDamage()));
                        }
                    } else {
                        placeBlock(up, top.clone());
                    }
                } else if (!block.isAir() && (!block.canBeReplaced() && (!block.canContainSnow() || this.block.getId() != BlockID.SNOW_LAYER) || !SupportType.hasFullSupport(block.down(), BlockFace.UP)) || this.block.getId() == Block.SNOW_LAYER && block instanceof BlockLiquid) {
                    if (this.block.getId() != Block.SNOW_LAYER ? this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) : this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        getLevel().dropItem(this, Block.get(this.block.getId(), this.block.getDamage()).toItem());
                    }
                } else {
                    boolean coverSnow = this.block.getId() == BlockID.SNOW_LAYER && block.canContainSnow();
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(this.block.getId(), coverSnow ? BlockSnowLayer.COVERED_BIT | this.block.getDamage() : this.block.getDamage()));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (coverSnow) {
                            level.setExtraBlock(pos, block, true, false);
                        }
                        placeBlock(pos, event.getTo());

                        if (event.getTo().getId() == Item.ANVIL) {
                            getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_FALL);

                            float damage = (float) Math.min(40, Math.max(0, (highestPosition - y) * 2));
                            if (damage > 0) {
                                Entity[] entities = level.getCollidingEntities(getBoundingBox(), this);
                                for (Entity entity : entities) {
                                    if (!(entity instanceof EntityLiving) || highestPosition <= y) {
                                        continue;
                                    }
                                    entity.attack(new EntityDamageByBlockEvent(event.getTo(), entity, DamageCause.ANVIL, damage));
                                }
                            }
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public Block getBlock() {
        return block.clone();
    }

    public Block getBlockUnsafe() {
        return block;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        namedTag.putCompound("FallingBlock", BlockSerializer.serialize(block));
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public void resetFallDistance() {
        if (!this.closed) { // For falling anvil: do not reset fall distance before dealing damage to entities
            this.highestPosition = this.y;
        }
    }

    private void placeBlock(Vector3 pos, Block block) {
        //TODO: sync
        level.setBlock(pos, block, true);
    }
}
