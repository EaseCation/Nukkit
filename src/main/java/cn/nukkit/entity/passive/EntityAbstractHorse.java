package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EntityAbstractHorse extends EntityAnimal implements EntityInteractable, EntityRideable, InventoryHolder {

    public static final int HORSE_TYPE_DEFAULT = 0;
    public static final int HORSE_TYPE_DONKEY = 1;
    public static final int HORSE_TYPE_MULE = 2;
    public static final int HORSE_TYPE_ZOMBIE = 3;
    public static final int HORSE_TYPE_SKELETON = 4;

    public static final int HORSE_FLAG_NONE = 0;
    public static final int HORSE_FLAG_TAME = 0b10;
    public static final int HORSE_FLAG_SADDLE = 0b100;
    public static final int HORSE_FLAG_BRED = 0b10000;
    public static final int HORSE_FLAG_EATING = 0b100000;
    public static final int HORSE_FLAG_STANDING = 0b1000000;
    public static final int HORSE_FLAG_OPEN_MOUTH = 0b10000000;

    protected HorseInventory inventory;

    private int countEating;
    private int mouthCounter;
    private int standCounter;

    private int jumpTicks;

    public EntityAbstractHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected double getStepHeight() {
        return 1;
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        if (entity instanceof EntityHuman) {
            float mountedYOffset = getHeight() * 0.75f - 0.1875f;
            float playerYOffset = entity.getEyeHeight() - 0.35f;
            return new Vector3f(0.0f, mountedYOffset + playerYOffset, 0.0f);
        } else {
            return super.getMountedOffset(entity);
        }
    }

    public void updateSaddled(boolean saddled) {
        this.updateSaddled(saddled, true);
    }

    public void updateSaddled(boolean saddled, boolean send) {
        this.setDataFlag(DATA_FLAG_SADDLED, saddled, send);
        this.setDataFlag(DATA_FLAG_WASD_CONTROLLED, saddled, send);
        this.setDataFlag(DATA_FLAG_CAN_POWER_JUMP, saddled, send);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_HORSE_FLAGS, HORSE_FLAG_NONE);

        this.inventory = new HorseInventory(this);
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

        this.setDataFlag(DATA_FLAG_CAN_WALK, true, false);
        this.setDataFlag(DATA_FLAG_GRAVITY, true, false);
    }

    @Override
    public boolean mountEntity(Entity entity) {
        boolean mounted = super.mountEntity(entity) && entity.riding != null;
        if (mounted) {
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 181f));
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, 0f));
        }
        return mounted;
    }

    protected void onMountEntity(Entity entity) {
        jumpTicks = 0;
        setEating(false);
        setStanding(false);
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        boolean dismounted = super.dismountEntity(entity) && entity.riding == null;
        if (dismounted) {
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 0f));
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, 0f));
        }
        return dismounted;
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
        if (isBaby()) {
            return false;
        }
        return player.isSneaking() && isTamed() || passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (isBaby()) {
            return "";
        }
        if (player.isSneaking() && isTamed()) {
            return "action.interact.opencontainer";
        }
        if (!passengers.isEmpty()) {
            return "";
        }
        return isTamed() ? "action.interact.ride.horse" : "action.interact.mount";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (isBaby()) {
            return false;
        }
        if (player.isSneaking()) {
            openInventory(player);
        } else {
            if (!passengers.isEmpty()) return false;
            this.mountEntity(player);
        }
        return super.onInteract(player, item);
    }

    @Override
    public void openInventory(Player player) {
        if (!isTamed()) {
            return;
        }

        player.addWindow(getInventory());
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    public Item[] getDrops() {
            return this.inventory.getContents().values().toArray(new Item[0]);
    }

    @Override
    public HorseInventory getInventory() {
        return inventory;
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
        if (mouthCounter > 0 && ++mouthCounter > 30) {
            mouthCounter = 0;
            setHorseFlag(HORSE_FLAG_OPEN_MOUTH, false);
        }
        if (standCounter > 0 && ++standCounter > 20) {
            standCounter = 0;
            setStanding(false);
        }

        aiStep();

        if (canRide() && getPassenger() instanceof Player player) {
            this.pitch = Mth.clamp(player.pitch, -44.949997f, 44.949997f);
            this.yaw = player.yaw;
        }
    }

    protected void aiStep() {
        if (!isEating() && passengers.isEmpty() && ThreadLocalRandom.current().nextInt(300) == 0 && level.getBlock(downVec()).is(Block.GRASS_BLOCK)) {
            countEating = 1;
            setEating(true);
        }

        if (countEating > 0 && ++countEating > 50) {
            countEating = 0;
            setEating(false);
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (isClientPredictedMovement() && getPassenger() instanceof Player) {
            return;
        }
        super.applyEntityCollision(entity);
    }

    protected boolean isClientPredictedMovement() {
        return true;
    }

    public boolean canRide() {
        // Player cannot ride horse without saddle
        return getInventory().getItem(0).getId() == Item.SADDLE;
    }

    @Override
    public void onPlayerInput(Player player, double x, double y, double z, double yaw, double pitch) {
        setPositionAndRotation(temporalVector.setComponents(x, y, z), yaw, pitch);
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

        int maxHealth = getMaxHealth();
        float jumpStrength = getJumpStrength();
        addEntity.attributes = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
                Attribute.getAttribute(Attribute.MOVEMENT).setDefaultValue(movementSpeed).setValue(movementSpeed),
                Attribute.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setDefaultValue(jumpStrength).setValue(jumpStrength),
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
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
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
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maximumHealth).setDefaultValue(maximumHealth).setValue(getHealth()),
        };
        Server.broadcastPacket(getViewers().values(), packet);
    }

    @Override
    protected void onHurt(EntityDamageEvent source) {
        openMouth();
        super.onHurt(source);
    }

    public float getJumpStrength() {
        return 0.5f;
    }

    public void updatePlayerJump(boolean jumping) {
        if (!isTamed()) {
            return;
        }
        if (!canRide()) {
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

        standIfPossible();

        if (!isClientPredictedMovement()) {
            float jumpScale = jumpTicks < 10 ? jumpTicks * 0.1f : 0.8f + 2f / (jumpTicks - 9) * 0.1f;
            motionY += getJumpStrength() * (jumpScale >= 0.9f ? 1 : 0.4f + 0.4f * jumpScale / 0.9f);

            Effect jumpBoost = getEffect(Effect.JUMP_BOOST);
            if (jumpBoost != null) {
                motionY += 0.1f * (jumpBoost.getAmplifier() + 1);
            }
        }

        jumpTicks = 0;
    }

    public boolean getHorseFlag(int flag) {
        return (getDataPropertyInt(DATA_HORSE_FLAGS) & flag) != 0;
    }

    public boolean setHorseFlag(int flag, boolean value) {
        int current = getDataPropertyInt(DATA_HORSE_FLAGS);
        int flags;
        if (value) {
            flags = current | flag;
        } else {
            flags = current & ~flag;
        }
        if (current == flags) {
            return false;
        }
        return setDataProperty(new IntEntityData(DATA_HORSE_FLAGS, flags));
    }

    public boolean openMouth() {
        mouthCounter = 1;
        return setHorseFlag(HORSE_FLAG_OPEN_MOUTH, true);
    }

    public boolean isBred() {
        return getHorseFlag(HORSE_FLAG_BRED);
    }

    public boolean setBred(boolean value) {
        return setHorseFlag(HORSE_FLAG_BRED, value);
    }

    public boolean isEating() {
        return getHorseFlag(HORSE_FLAG_EATING);
    }

    public boolean setEating(boolean value) {
        return setHorseFlag(HORSE_FLAG_EATING, value);
    }

    public boolean standIfPossible() {
        standCounter = 1;
        setEating(false);
        return setStanding(true);
    }

    public boolean isStanding() {
        return getDataFlag(DATA_FLAG_REARING);
    }

    public boolean setStanding(boolean value) {
        return setDataFlag(DATA_FLAG_REARING, value);
    }

    public boolean isSaddled() {
        return getDataFlag(DATA_FLAG_SADDLED);
    }

    public boolean setSaddled(boolean value) {
        return setDataFlag(DATA_FLAG_SADDLED, value);
    }

    public boolean isTamed() {
        return getDataFlag(DATA_FLAG_TAMED);
    }

    public boolean setTamed(boolean value) {
        return setDataFlag(DATA_FLAG_TAMED, value);
    }

    public void makeMad() {
        standIfPossible();
        openMouth();
        level.addLevelSoundEvent(getEyePosition(), LevelSoundEventPacket.SOUND_MAD, getIdentifier());
    }
}
