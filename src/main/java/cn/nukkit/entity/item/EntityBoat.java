package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;

import java.util.ArrayList;

/**
 * Created by yescallop on 2016/2/13.
 */
public class EntityBoat extends EntityVehicle {

    public static final int NETWORK_ID = EntityID.BOAT;

    public static final String BUOYANCY_DATA = "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"simulate_waves\":true}";

    public static final Vector3f PASSENGER_OFFSET = new Vector3f(-0.6f, 0, 0);
    public static final Vector3f RIDER_PASSENGER_OFFSET = new Vector3f(0.2f, 0, 0);

    public static final int RIDER_INDEX = 0;
    public static final int PASSENGER_INDEX = 1;

    public static final double SINKING_DEPTH = 0.07;
    public static final double SINKING_SPEED = 0.0005;
    public static final double SINKING_MAX_SPEED = 0.005;

    protected boolean sinking = true;
    public int woodID;
    private boolean autoMount = true;

    public EntityBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setMaxHealth(40);
        this.setHealth(40);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("AutoMount")) {
            this.autoMount = this.namedTag.getBoolean("AutoMount");
        }

        this.setDataFlag(DATA_FLAG_STACKABLE, true, false);
        this.setDataFlag(DATA_FLAG_GRAVITY, true, false);
        this.dataProperties.putInt(DATA_VARIANT, woodID = this.namedTag.getByte("woodID"));
        this.dataProperties.putByte(DATA_CONTROLLING_SEAT_INDEX, RIDER_INDEX);
        this.dataProperties.putBoolean(DATA_IS_BUOYANT, true);
        this.dataProperties.putString(DATA_BUOYANCY_DATA, BUOYANCY_DATA);
    }

    @Override
    public float getHeight() {
        return 0.455f;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    protected float getDrag() {
        return 0.1f;
    }

    @Override
    protected float getGravity() {
        return 0.03999999910593033F;
    }

    @Override
    public float getBaseOffset() {
        return 0.375F;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public void setAutoMount(boolean autoMount) {
        this.autoMount = autoMount;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            source.setDamage(source.getDamage() * 2);

            boolean attack = super.attack(source);

            if (isAlive()) {
                performHurtAnimation();
            }

            return attack;
        }
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
    public void close() {
        super.close();

        for (Entity linkedEntity : this.passengers) {
            linkedEntity.riding = null;
        }
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
            super.onUpdate(currentTick);

            double waterDiff = getWaterLevel();
            if (!hasControllingPassenger()) {
                if (waterDiff > SINKING_DEPTH && !sinking) {
                    sinking = true;
                } else if (waterDiff < 0.05 && sinking) {
                    sinking = false;
                }

                if (waterDiff < -SINKING_DEPTH) {
                    this.motionY = Math.min(0.05, this.motionY + 0.005);
                } else if (waterDiff < 0 || !sinking) {
                    this.motionY = this.motionY > SINKING_MAX_SPEED ? Math.max(this.motionY - 0.02, SINKING_MAX_SPEED) : this.motionY + SINKING_SPEED;
//                    this.motionY = this.motionY + SINKING_SPEED > SINKING_MAX_SPEED ? this.motionY - SINKING_SPEED : this.motionY + SINKING_SPEED;
                }
            }

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                double factor = 0;
                int count = 0;

                int y = getFloorY() - 1;
                if (level.getHeightRange().isValidBlockY(y)) {
                    for (int x = boundingBox.getFloorMinX(); x <= boundingBox.getFloorMaxX(); x++) {
                        for (int z = boundingBox.getFloorMinZ(); z <= boundingBox.getFloorMaxZ(); z++) {
                            Block block =  this.level.getBlock(x, y, z, false);
                            if (block.isAir() || !block.collidesWithBB(boundingBox)) {
                                continue;
                            }

                            factor += block.getFrictionFactor();
                            count++;
                        }
                    }
                }

                if (count != 0) {
                    factor /= count;
                } else {
                    factor = 0.6;
                }
                friction *= factor;
            }

            this.motionX *= friction;

            if (!hasControllingPassenger()) {
                if (waterDiff > SINKING_DEPTH || sinking) {
                    this.motionY = waterDiff > 0.5 ? this.motionY - this.getGravity() : (this.motionY - SINKING_SPEED < -SINKING_MAX_SPEED ? this.motionY : this.motionY - SINKING_SPEED);
                }
            }

            this.motionZ *= friction;

            Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
            Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

            this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

            if (!from.equals(to)) {
                this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
            }

            //TODO: lily pad collision
            this.updateMovement();

            if (this.autoMount && this.passengers.size() < 2) {
                for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.2, 0, 0.2), this)) {
                    if (entity.riding != null || isPassenger(entity)) {
                        continue;
                    }

//                    if (entity instanceof EntityBoat || entity instanceof EntityMinecartAbstract) {
//                        this.applyEntityCollision(entity);
//                        continue;
//                    }

                    if (!(entity instanceof EntityLiving) || entity instanceof EntityArmorStand || entity instanceof Player || entity instanceof EntityWaterAnimal) {
                        continue;
                    }

//                    if (this.passengers.size() < 2) {
//                        this.mountEntity(entity);
//                    }
                    this.mountEntity(entity);

                    if (this.passengers.size() >= 2) {
                        break;
                    }
                }
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    public void updatePassengers() {
        updatePassengers(false);
    }

    public void updatePassengers(boolean sendLinks) {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
            }
        }

        Entity ent;

        if (passengers.size() == 1) {
            (ent = this.passengers.get(0)).setSeatPosition(getMountedOffset(ent));
            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }
        } else if (passengers.size() == 2) {
            if (!((ent = passengers.get(0)) instanceof Player)) { //swap
                Entity passenger2 = passengers.get(1);

                if (passenger2 instanceof Player) {
                    this.passengers.set(0, passenger2);
                    this.passengers.set(1, ent);

                    ent = passenger2;
                }
            }

            ent.setSeatPosition(getMountedOffset(ent).add(RIDER_PASSENGER_OFFSET));
            super.updatePassengerPosition(ent);
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE);
            }

            (ent = this.passengers.get(1)).setSeatPosition(getMountedOffset(ent).add(PASSENGER_OFFSET));

            super.updatePassengerPosition(ent);

            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_PASSENGER);
            }

            float yawDiff = ent.getId() % 2 == 0 ? 90 : 270;
            ent.setRotation(this.yaw + yawDiff, ent.pitch);
            ent.updateMovement();
        } else {
            for (Entity passenger : passengers) {
                super.updatePassengerPosition(passenger);
            }
        }
    }

    public double getWaterLevel() {
        double maxY = this.boundingBox.getMinY() + getBaseOffset();
        AxisAlignedBB.BBConsumer<Double> consumer = new AxisAlignedBB.BBConsumer<Double>() {

            private double diffY = Double.MAX_VALUE;

            @Override
            public void accept(int x, int y, int z) {
                Block block = EntityBoat.this.level.getBlock(x, y, z);

                if (block instanceof BlockWater) {
                    double level = block.getMaxY();

                    diffY = Math.min(maxY - level, diffY);
                }
            }

            @Override
            public Double get() {
                return diffY;
            }
        };

        this.boundingBox.forEach(consumer);

        return consumer.get();
    }

    @Override
    public boolean mountEntity(Entity entity) {
        boolean player = !this.passengers.isEmpty() && this.passengers.get(0) instanceof Player;
        byte mode = SetEntityLinkPacket.TYPE_PASSENGER;

        if (!player && (entity instanceof Player || this.passengers.isEmpty())) {
            mode = SetEntityLinkPacket.TYPE_RIDE;
        }

        boolean r = super.mountEntity(entity, mode);

        if (entity.riding != null) {
            updatePassengers(true);

            this.onMountEntity(entity);
        }

        return r;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger) {
        updatePassengers();
    }

    @Override
    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        boolean r = super.dismountEntity(entity, sendLinks);

        updatePassengers();
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));

        return r;
    }

    @Override
    public boolean isControlling(Entity entity) {
        return entity instanceof Player && this.passengers.indexOf(entity) == 0;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.passengers.size() >= 2) {
            return false;
        }

        this.mountEntity(player, true);
        return super.onInteract(player, item, clickedPos);
    }

    public void onPaddle(AnimatePacket.Action animation, float value) {
        int propertyId = animation == AnimatePacket.Action.ROW_RIGHT ? DATA_PADDLE_TIME_RIGHT : DATA_PADDLE_TIME_LEFT;

        if (getDataPropertyFloat(propertyId) != value) {
            this.setDataProperty(new FloatEntityData(propertyId, value));
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (entity instanceof EntityArmorStand) {
            return;
        }
        if (this.riding == null && entity.riding != this && !entity.passengers.contains(this)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))
                    || entity instanceof Player && ((Player) entity).isSpectator()) {
                return;
            }

            double diffX = entity.x - this.x;
            double diffZ = entity.z - this.z;

            double direction = Math.max(Math.abs(diffX), Math.abs(diffZ));

            if (direction >= 0.009999999776482582D) {
                direction = Math.sqrt(direction);
                diffX /= direction;
                diffZ /= direction;

                double d3 = Math.min(1 / direction, 1);

                diffX *= d3;
                diffZ *= d3;
                diffX *= 0.05000000074505806;
                diffZ *= 0.05000000074505806;
                diffX *= 1 + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= diffX;
                    motionZ -= diffZ;
                }
            }
        }
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public void kill() {
        super.kill();

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return;
            }
        }

        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.dropItem();
        }
    }

    protected void dropItem() {
        this.level.dropItem(this, Item.get(Item.BOAT, this.woodID));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("woodID", this.woodID);
    }

    public void onInput(double x, double y, double z, double rotation) {
        this.setPositionAndRotation(this.temporalVector.setComponents(x, y - this.getBaseOffset(), z), rotation, 0);
    }

    @Override
    protected void onMountEntity(Entity entity) {
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 1));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 90));
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 1));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, -90));
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        super.addMovement(x, y, z, yaw, 0, 0);
    }

    public boolean isFull() {
        return this.passengers.size() >= 2;
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return !isFull();
    }

    @Override
    public String getInteractButtonText(Player player) {
        return !this.isFull() ? "action.interact.ride.boat" : "";
    }
}
