package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.SkeletonHorseInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import java.util.stream.Stream;

/**
 * @author PikyCZ
 */
public class EntitySkeletonHorse extends EntityAnimal implements EntityRideable, InventoryHolder {

    public static final int NETWORK_ID = 26;

    protected SkeletonHorseInventory inventory;

    public EntitySkeletonHorse(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
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
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SADDLED, saddled);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_POWER_JUMP, saddled);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(15);

        this.inventory = new SkeletonHorseInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }
        this.setDataProperty(new ByteEntityData(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType()));
        this.setDataProperty(new IntEntityData(DATA_CONTAINER_BASE_SIZE, inventory.getSize()));
        this.setDataProperty(new IntEntityData(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0));

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_TAMED, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_WASD_CONTROLLED, true);
    }

    @Override
    public boolean mountEntity(Entity entity) {
        boolean mounted = super.mountEntity(entity) && entity.riding != null;
        if (mounted) {
            entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 0));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MAX_ROTATION, 0f));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MIN_ROTATION, 0f));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET, 0f));
        }
        return mounted;
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        boolean dismounted = super.dismountEntity(entity) && entity.riding == null;
        if (dismounted) {
            entity.setDataProperty(new ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 0));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MAX_ROTATION, 0f));
            entity.setDataProperty(new FloatEntityData(DATA_RIDER_MIN_ROTATION, 0f));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET, 0f));
        }
        return dismounted;
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (player.isSneaking()) {
            openInventory(player);
        } else {
            if (passengers.size() >= 1) return false;
            this.mountEntity(player);
        }
        return super.onInteract(player, item);
    }

    @Override
    public void kill() {
        super.kill();
        this.inventory.clearAll();
    }

    @Override
    public Item[] getDrops() {
        return Stream.concat(
                Stream.of(Item.get(Item.BONE)),
                this.inventory.getContents().values().stream()
        ).toArray(Item[]::new);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        UpdateAttributesPacket pk0 = new UpdateAttributesPacket();
        pk0.entityId = getId();
		pk0.entries = new Attribute[]{
				Attribute.getAttribute(Attribute.JUMP_STRENGTH).setMaxValue(2.0f).setValue(0.5f),
		};

        super.spawnTo(player);
    }

    @Override
    public SkeletonHorseInventory getInventory() {
        return inventory;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        if (this.inventory != null) {
            for (int slot = 0; slot < inventory.getSize(); ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) return false;

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) return true;

        this.lastUpdate = currentTick;
        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            super.onUpdate(currentTick);

            if (this.checkObstruction(this.x, this.y, this.z)) hasUpdate = true;

            this.move(this.motionX, this.motionY, this.motionZ);

            this.motionY -= this.getGravity();

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= friction;
            this.motionZ *= friction;

            this.updateMovement();

            if (hasControllingPassenger()) {
                for (Entity passenger : this.getPassengers()) {
                    passenger.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.y);
                }
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public void onPlayerRiding(Vector3 pos, double yaw, double pitch) {
        setPositionAndRotation(pos, yaw, pitch);
    }
}
