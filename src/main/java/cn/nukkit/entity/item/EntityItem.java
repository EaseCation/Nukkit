package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityItem extends Entity {
    public static final int NETWORK_ID = EntityID.ITEM;

    public EntityItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    protected String owner;
    protected String thrower;

    protected Item item;

    protected int pickupDelay;

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.125f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(5);
        this.setHealth(this.namedTag.getShort("Health"));

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.contains("PickupDelay")) {
            this.pickupDelay = this.namedTag.getShort("PickupDelay");
        }

        if (this.namedTag.contains("Owner")) {
            this.owner = this.namedTag.getString("Owner");
        }

        if (this.namedTag.contains("Thrower")) {
            this.thrower = this.namedTag.getString("Thrower");
        }

        if (!this.namedTag.contains("Item")) {
            this.close();
            return;
        }

        this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
        this.setDataFlag(DATA_FLAG_IMMOBILE, true, false);

        if (item != null && item.isFireResistant()) {
            fireProof = true;
            setDataFlag(DATA_FLAG_FIRE_IMMUNE, true, false);
        }

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        DamageCause cause = source.getCause();
        switch (cause) {
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                if (item != null && item.isFireResistant()) {
                    return false;
                }
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                if (item != null && !item.isExplodable() || isInsideOfWater()) {
                    return false;
                }
                break;
            case VOID:
            case CONTACT:
                break;
            default:
                return false;
        }

        if (super.attack(source)) {
            if (this.item == null || this.isAlive()) {
                return true;
            }
            if (cause == DamageCause.LAVA || cause == DamageCause.FIRE_TICK) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
            }
            if (!this.item.isShulkerBox()) {
                return true;
            }
            CompoundTag nbt = this.item.getNamedTag();
            if (nbt == null) {
                return true;
            }
            ListTag<CompoundTag> items = nbt.getList("Items", CompoundTag.class);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.get(i);
                Item item = NBTIO.getItemHelper(itemTag);
                if (item.isNull()) {
                    continue;
                }
                this.level.dropItem(this, item);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        if (this.age % 60 == 0 && this.onGround && this.pickupDelay <= 0 && this.getItem() != null && this.isAlive()) {
            if (this.getItem().getCount() < this.getItem().getMaxStackSize()) {
                for (Entity entity : this.getLevel().getNearbyEntities(getBoundingBox().grow(1, 1, 1), this)) {
                    if (entity instanceof EntityItem) {
                        if (!entity.isAlive()) {
                            continue;
                        }
                        if (!entity.isOnGround()) {
                            continue;
                        }
                        if (((EntityItem) entity).pickupDelay > 0) {
                            continue;
                        }
                        Item closeItem = ((EntityItem) entity).getItem();
                        if (closeItem.getCount() >= closeItem.getMaxStackSize()) {
                            continue;
                        }
                        if (!closeItem.equals(getItem(), true, true)) {
                            continue;
                        }
                        int newAmount = this.getItem().getCount() + closeItem.getCount();
                        if (newAmount > this.getItem().getMaxStackSize()) {
                            continue;
                        }
                        entity.close();
                        this.getItem().setCount(newAmount);

                        EntityEventPacket packet = new EntityEventPacket();
                        packet.eid = getId();
                        packet.data = newAmount;
                        packet.event = EntityEventPacket.MERGE_ITEMS;
                        Server.broadcastPacket(this.getViewers().values(), packet);
                    }
                }
            }
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (isInsideOfFire() && (item == null || !item.isFireResistant())) {
            this.close();
            return false;
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }/* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, true)) {
                            return true;
                        }
                    }
                }
            }*/

            updateLiquidMovement();

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(Mth.floor(this.x), Mth.floor(this.y - 1), Mth.floor(this.z) - 1).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY *= -0.5;
            }

            this.updateMovement();

            if (this.age > 6000 && (item == null || item.shouldDespawn())) {
                ItemDespawnEvent ev = new ItemDespawnEvent(this);
                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.age = 0;
                } else {
                    this.close();
                    return false;
                }
            }
        }

        return hasUpdate || this.age <= 60 || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    private void updateLiquidMovement() {
        double y = this.y + getEyeHeight();

        Block block = level.getBlock((int) x, (int) boundingBox.getMaxY(), (int) z);
        if (block.isLiquidSource()) {
            //item is fully in liquid
            motionY -= getGravity() * -0.015;
            return;
        }

        Block floor = getLevelBlock();
        if (floor.isLiquidSource() || (floor = level.getExtraBlock(floor)).isWaterSource()) {
            double height = floor.y + 1 - ((BlockLiquid) floor).getFluidHeightPercent() - 0.1111111;
            if (y < height) {
                //item is going up in liquid, don't let it go back down too fast
                motionY = getGravity() - 0.06;
                return;
            }
        }

        //item is not in liquid
        motionY -= getGravity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.item != null) { // Yes, a item can be null... I don't know what causes this, but it can happen.
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(this.item, -1));
            this.namedTag.putShort("Health", (int) this.getHealth());
            this.namedTag.putShort("Age", this.age);
            this.namedTag.putShort("PickupDelay", this.pickupDelay);
            if (this.owner != null) {
                this.namedTag.putString("Owner", this.owner);
            }

            if (this.thrower != null) {
                this.namedTag.putString("Thrower", this.thrower);
            }
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : (this.item == null ? "" : this.item.hasCustomName() ? this.item.getCustomName() : this.item.getName());
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        this.respawnToAll();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getThrower() {
        return thrower;
    }

    public void setThrower(String thrower) {
        this.thrower = thrower;
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
    protected DataPacket createAddEntityPacket() {
        AddItemEntityPacket addEntity = new AddItemEntityPacket();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;
        addEntity.item = this.getItem();
        return addEntity;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return true;
    }
}
