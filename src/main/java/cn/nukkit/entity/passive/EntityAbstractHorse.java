package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.inventory.HorseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public abstract class EntityAbstractHorse extends EntityAnimal implements EntityRideable, InventoryHolder {
    protected HorseInventory inventory;

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
        // Not working at now for some reason. Disable temporary
        // this.setDataFlag(DATA_FLAGS, DATA_FLAG_SADDLED, saddled);
        // this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_POWER_JUMP, saddled);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.inventory = new HorseInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }
        this.setDataProperty(new ByteEntityData(DATA_CONTAINER_TYPE, inventory.getType().getNetworkType()), false);
        this.setDataProperty(new IntEntityData(DATA_CONTAINER_BASE_SIZE, inventory.getSize()), false);
        this.setDataProperty(new IntEntityData(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0), false);
        dataProperties.putByte(DATA_CONTROLLING_SEAT_INDEX, 0);

        this.setDataFlag(DATA_FLAG_CAN_WALK, true, false);
        this.setDataFlag(DATA_FLAG_TAMED, true, false);
        // Disable due to protocol compatibility issues. Only temporary measures.
        // this.setDataFlag(DATA_FLAGS, DATA_FLAG_WASD_CONTROLLED, true, false);
        this.setDataFlag(DATA_FLAG_GRAVITY, true, false);
    }

    @Override
    public boolean mountEntity(Entity entity) {
        boolean mounted = super.mountEntity(entity) && entity.riding != null;
        if (mounted) {
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 0f));
            entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));
            entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, 0f));
        }
        return mounted;
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
    public boolean onInteract(Player player, Item item) {
        if (player.isSneaking()) {
            openInventory(player);
        } else {
            if (!passengers.isEmpty()) return false;
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
            return this.inventory.getContents().values().toArray(new Item[0]);
    }

    @Override
    public HorseInventory getInventory() {
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
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    public boolean canRide() {
        // Player cannot ride horse without saddle
        return getInventory().getItem(0).getId() == Item.SADDLE;
    }

    @Override
    public void onPlayerInput(Player player, double motionX, double motionY) {
        if (!canRide()) return;

        motionX *= 0.4;

        double f = motionX * motionX + motionY * motionY;
        double friction = 0.6;

        this.yaw = player.yaw;

        if (f >= 1.0E-4) {
            f = Math.sqrt(f);

            if (f < 1) {
                f = 1;
            }

            f = friction / f;
            motionX = motionX * f;
            motionY = motionY * f;
            double f1 = Mth.sin(this.yaw * 0.017453292);
            double f2 = Mth.cos(this.yaw * 0.017453292);
            this.motionX = (motionX * f2 - motionY * f1);
            this.motionZ = (motionY * f2 + motionX * f1);
        } else {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }
}
