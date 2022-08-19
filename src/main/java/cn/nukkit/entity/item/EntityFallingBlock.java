package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.SupportType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends Entity {

    public static final int NETWORK_ID = 66;

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
        return blockId == BlockID.ANVIL;
    }

    protected int blockId;
    protected int damage;

    public boolean sync;

    public EntityFallingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag != null) {
            if (namedTag.contains("TileID")) {
                blockId = namedTag.getInt("TileID");
            } else if (namedTag.contains("Tile")) {
                blockId = namedTag.getInt("Tile");
                namedTag.putInt("TileID", blockId);
            }

            if (namedTag.contains("Data")) {
                damage = namedTag.getByte("Data");
            }
        }

        if (blockId == 0) {
            close();
            return;
        }

        this.dataProperties.putLong(DATA_NUKKIT_FLAGS, NUKKIT_FLAG_VARIANT_BLOCK); //HACK: multi-version handle
        setDataProperty(new IntEntityData(DATA_VARIANT, (this.getBlock() << Block.BLOCK_META_BITS) | this.getDamage()));
    }

    public boolean canCollideWith(Entity entity) {
        return blockId == BlockID.ANVIL;
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
        this.timing.startTiming();

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            Vector3 pos = (new Vector3(x - 0.5, y, z - 0.5)).round();

            if (onGround) {
                close();
                Block block = level.getBlock(pos);

                Vector3 floorPos = new Vector3(x - 0.5, y, z - 0.5).floor();
                Block floorBlock = this.level.getBlock(floorPos);
                if (this.getBlock() == Block.SNOW_LAYER && floorBlock.getId() == Block.SNOW_LAYER && (floorBlock.getDamage() & 0x7) != 0x7) {
                    int mergedHeight = (floorBlock.getDamage() & 0x7) + 1 + (this.getDamage() & 0x7) + 1;
                    if (mergedHeight > 8) {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, 0x7));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            setBlock(floorPos, event.getTo());

                            Vector3 abovePos = floorPos.up();
                            Block aboveBlock = this.level.getBlock(abovePos);
                            if (aboveBlock.getId() == Block.AIR) {
                                EntityBlockChangeEvent event2 = new EntityBlockChangeEvent(this, aboveBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 8 - 1));
                                this.server.getPluginManager().callEvent(event2);
                                if (!event2.isCancelled()) {
                                    setBlock(abovePos, event2.getTo());
                                }
                            }
                        }
                    } else {
                        EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 1));
                        this.server.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            setBlock(floorPos, event.getTo());
                        }
                    }
                } else if (getBlock() == BlockID.SCAFFOLDING && floorBlock.getId() == BlockID.SCAFFOLDING) {
                    Block top = floorBlock;
                    Block up;
                    while ((up = top.up()).getId() == BlockID.SCAFFOLDING) {
                        top = up;
                    }
                    if (top.getFloorY() >= 255) {
                        if (level.gameRules.getBoolean(GameRule.DO_ENTITY_DROPS)) {
                            level.dropItem(this, Item.get(getBlock(), getDamage()));
                        }
                    } else {
                        setBlock(up, top.clone());
                    }
                } else if (!block.isAir() && (!block.canBeReplaced() && (!block.canContainSnow() || getBlock() != BlockID.SNOW_LAYER) || !SupportType.hasFullSupport(block.down(), BlockFace.UP)) || this.getBlock() == Block.SNOW_LAYER && block instanceof BlockLiquid) {
                    if (this.getBlock() != Block.SNOW_LAYER ? this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) : this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        getLevel().dropItem(this, Block.get(this.getBlock(), this.getDamage()).toItem());
                    }
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(getBlock(), getDamage()));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (getBlock() == BlockID.SNOW_LAYER && block.canContainSnow()) {
                            level.setExtraBlock(pos, block, true, false);
                        }
                        setBlock(pos, event.getTo());

                        if (event.getTo().getId() == Item.ANVIL) {
                            getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_FALL);

                            Entity[] e = level.getCollidingEntities(this.getBoundingBox(), this);
                            for (Entity entity : e) {
                                if (entity instanceof EntityLiving && highestPosition > y) {
                                    entity.attack(new EntityDamageByBlockEvent(event.getTo(), entity, DamageCause.CONTACT, (float) Math.min(40, Math.max(0, (highestPosition - y) * 2))));
                                }
                            }
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        this.timing.stopTiming();

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public int getBlock() {
        return blockId;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        namedTag.putInt("TileID", blockId);
        namedTag.putByte("Data", damage);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityFallingBlock.NETWORK_ID;
        packet.entityUniqueId = this.getId();
        packet.entityRuntimeId = getId();
        packet.x = (float) x;
        packet.y = (float) y + this.getBaseOffset();
        packet.z = (float) z;
        packet.speedX = (float) motionX;
        packet.speedY = (float) motionY;
        packet.speedZ = (float) motionZ;
        packet.metadata = dataProperties;
        player.dataPacket(packet);
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

    protected void setBlock(Vector3 pos, Block block) {
        //TODO: sync
        level.setBlock(0, pos, block, true);
    }
}
