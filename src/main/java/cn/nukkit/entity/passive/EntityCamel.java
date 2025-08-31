package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Iterator;

public class EntityCamel extends EntityAnimal implements EntityInteractable, EntityRideable, InventoryHolder {
    public static final int NETWORK_ID = EntityID.CAMEL;

    protected HorseInventory inventory;

    private int jumpTicks;
    private int dashCooldown;

    public EntityCamel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Camel";
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        if (isSitting()) {
            return 0.945f;
        }
        return 2.375f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

//        dataProperties.putByte(DATA_CONTROLLING_SEAT_INDEX, 0);

        inventory = new HorseInventory(this);
        inventory.setSize(1);
        ListTag<CompoundTag> items = namedTag.getList("Items", (ListTag<CompoundTag>) null);
        if (items == null) {
            namedTag.putList(new ListTag<>("Items"));
        } else {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            Iterator<CompoundTag> iter = items.iterator();
            while (iter.hasNext()) {
                CompoundTag tag = iter.next();

                int slot = tag.getByte("Slot");
                if (slot < 0 || slot >= inventory.getSize()) {
                    iter.remove();
                    continue;
                }

                Item item = NBTIO.getItemHelper(tag);
                if (item.isNull()) {
                    iter.remove();
                    continue;
                }

                slots.put(slot, item);
            }
        }

        updateSaddled(inventory.getItem(0).is(Item.SADDLE), false);

        dataProperties.putByte(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType());
        dataProperties.putInt(DATA_CONTAINER_BASE_SIZE, 5);

        setDataFlag(DATA_FLAG_TAMED, true, false);
        setDataFlag(DATA_FLAG_REARING, true, false);

        movementSpeed = 0.09f;

        this.setMaxHealth(32);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        ListTag<CompoundTag> items = new ListTag<>("Items");
        if (inventory != null) {
            Int2ObjectMap<Item> slots = inventory.getContentsUnsafe();
            for (Int2ObjectMap.Entry<Item> entry : slots.int2ObjectEntrySet()) {
                int slot = entry.getIntKey();
                if (slot < 0 || slot >= inventory.getSize()) {
                    continue;
                }

                Item item = entry.getValue();
                if (item == null || item.isNull()) {
                    continue;
                }

                items.add(NBTIO.putItemHelper(item, slot));
            }
        }
        namedTag.putList(items);
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
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;
        Pair<Int2IntMap, Int2FloatMap> propertyValues = getProperties().getValues();
        if (propertyValues != null) {
            addEntity.intProperties = propertyValues.left();
            addEntity.floatProperties = propertyValues.right();
        }

        int maxHealth = getMaxHealth();
        addEntity.attributes = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
                Attribute.getAttribute(Attribute.MOVEMENT).setDefaultValue(movementSpeed).setValue(movementSpeed),
        };

        return addEntity;
    }

    @Override
    public boolean setHealth(float health) {
        if (!super.setHealth(health)) {
            return false;
        }

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.entityId = getId();
        int maxHealth = getMaxHealth();
        packet.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
        };
        Server.broadcastPacket(getViewers().values(), packet);
        return true;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        if (getHealth() > maxHealth) {
            this.health = maxHealth;
        }

        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.entityId = getId();
        int maximumHealth = getMaxHealth();
        packet.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maximumHealth).setDefaultMaxValue(maximumHealth).setDefaultValue(maximumHealth).setValue(getHealth()),
        };
        Server.broadcastPacket(getViewers().values(), packet);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity.getNetworkId() == -1) {
            if (hasControllingPassenger()) {
                return new Vector3f(0, 3.12501f, -0.5f);
            }
            return new Vector3f(0, 3.12501f, 0.5f);
        }

        if (hasControllingPassenger()) {
            return new Vector3f(0, 1.905f + entity.getRidingOffset(), -0.5f);
        }
        return new Vector3f(0, 1.905f + entity.getRidingOffset(), 0.5f);
    }

    @Override
    protected void onMountEntity(Entity entity) {
        jumpTicks = 0;
        dashCooldown = 0;

        entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 181));
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, 0));
    }

    @Override
    protected void onDismountEntity(Entity entity) {
        jumpTicks = 0;
        if (entity instanceof Player) {
            pitch = 0;
        }
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (player.isSneaking()) {
            return true;
        }
        if (!getDataFlag(DATA_FLAG_SADDLED) && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return true;
        }
        return passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (player.isSneaking()) {
            return "action.interact.opencontainer";
        }
        if (!getDataFlag(DATA_FLAG_SADDLED) && player.getInventory().getItemInHand().is(Item.SADDLE)) {
            return "action.interact.saddle";
        }
        return canRide() ? "action.interact.ride.horse" : "";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (player.isSneaking()) {
            openInventory(player);
            return false;
        }

        if (item.is(Item.SADDLE) && !getDataFlag(DATA_FLAG_SADDLED)) {
            Item saddle = item.clone();
            saddle.setCount(1);
            inventory.setItem(0, saddle);
            return true;
        }

        if (!canRide()) {
            return false;
        }

        mountEntity(player);
        return false;
    }

    @Override
    protected double getStepHeight() {
        return 1;
    }

    @Override
    public void kill() {
        super.kill();
        inventory.clearAll();
    }

    @Override
    public Item[] getDrops() {
        return inventory.getContents().values().toArray(new Item[0]);
    }

    @Override
    public HorseInventory getInventory() {
        return inventory;
    }

    @Override
    public void onPlayerInput(Player player, double motionX, double motionY) {
        if (!player.isServerAuthoritativeMovementEnabled()) {
            return;
        }

        motionX *= 0.3f;
        if (motionY > 0) {
            motionY *= 0.5f;
        } else if (motionY < 0) {
            motionY *= 0.15f;
        }

        double f = motionX * motionX + motionY * motionY;
        double friction = 0.6;

        this.pitch = Mth.clamp(player.pitch, -44.949997f, 44.949997f);
        this.yaw = player.yaw;

        if (f < 1.0E-4) {
            this.motionX = 0;
            this.motionZ = 0;
            return;
        }

        f = Math.sqrt(f);
        if (f < 1) {
            f = 1;
        }

        f = friction / f;
        motionX = motionX * f;
        motionY = motionY * f;

        Effect speed = getEffect(Effect.SPEED);
        if (speed != null) {
            float speedBoost = 0.2f * (speed.getAmplifier() + 1);
            motionX += motionX * speedBoost;
            motionY += motionY * speedBoost;
        }

        double d = this.yaw * Mth.DEG_TO_RAD;
        double f1 = Mth.sin(d);
        double f2 = Mth.cos(d);
        this.motionX = motionX * f2 - motionY * f1;
        this.motionZ = motionY * f2 + motionX * f1;
    }

    @Override
    public void onPlayerInput(Player player, double x, double y, double z, double yaw, double pitch) {
        if (player.isServerAuthoritativeMovementEnabled()) {
            return;
        }
        setPositionAndRotation(temporalVector.setComponents(x, y, z), yaw, pitch);
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

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            this.motionY -= this.getGravity();

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1d - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction = this.getLevel().getBlock(Mth.floor(this.x), Mth.floor(this.y - 1), Mth.floor(this.z) - 1).getFrictionFactor() * friction;
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            this.updateMovement();

            normalTick();
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    protected void normalTick() {
        if (dashCooldown > 0 && --dashCooldown == 0) {
            setDataFlag(DATA_FLAG_HAS_DASH_COOLDOWN, false);
        }

        if (canRide() && getPassenger() instanceof Player player) {
            this.pitch = Mth.clamp(player.pitch, -44.949997f, 44.949997f);
            this.yaw = player.yaw;
        }
    }

    public boolean canRide() {
        return passengers.size() < 2;
    }

    public void updateSaddled(boolean saddled) {
        this.updateSaddled(saddled, true);
    }

    public void updateSaddled(boolean saddled, boolean send) {
        this.setDataFlag(DATA_FLAG_SADDLED, saddled, send);
        this.setDataFlag(DATA_FLAG_WASD_CONTROLLED, saddled, send);
        this.setDataFlag(DATA_FLAG_CAN_DASH, saddled, send);
    }

    public void updatePlayerJump(Player player, boolean jumping) {
        if (!getDataFlag(DATA_FLAG_SADDLED)) {
            return;
        }
        if (getDataFlag(DATA_FLAG_HAS_DASH_COOLDOWN)) {
            return;
        }
        if (!isOnGround()) {
            return;
        }

        if (jumpTicks == 0 && !jumping) {
            return;
        }

        if (jumping) {
            jumpTicks++;
            return;
        }

        this.pitch = Mth.clamp(player.pitch, -44.949997f, 44.949997f);
        this.yaw = player.yaw;

        float jumpPower = 0.42f;
        Effect jumpBoost = getEffect(Effect.JUMP_BOOST);
        if (jumpBoost != null) {
            jumpPower += 0.1f * (jumpBoost.getAmplifier() + 1);
        }

        float jumpScale = jumpTicks < 10 ? jumpTicks * 0.1f : 0.8f + 2f / (jumpTicks - 9) * 0.1f;
        float dashScale = jumpScale >= 0.9f ? 1 : 0.4f + 0.4f * jumpScale / 0.9f;
        Vector3 motion = getDirectionVector().xz().normalize().multiply(22.2222f * dashScale * movementSpeed).add(0, 1.4285f * dashScale * jumpPower, 0);
        motionX += motion.x;
        motionY += motion.y;
        motionZ += motion.z;

        dashCooldown = 55;
        setDataFlag(DATA_FLAG_HAS_DASH_COOLDOWN, true);

        jumpTicks = 0;
    }

    public boolean isSitting() {
        return getDataFlag(DATA_FLAG_SITTING);
    }

    public boolean setSitting(boolean value) {
        return setDataFlag(DATA_FLAG_SITTING, value);
    }
}
