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
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntityPig extends EntityAnimal implements EntityInteractable, EntityRideable {

    public static final int NETWORK_ID = EntityID.PIG;

    public EntityPig(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);

        movementSpeed = 0.25f;

        setDataFlag(DATA_FLAG_SADDLED, namedTag.getBoolean("Saddled"), false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("Saddled", getDataFlag(DATA_FLAG_SADDLED));
    }

    @Override
    public String getName() {
        return "Pig";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(this.isOnFire() ? Item.COOKED_PORKCHOP : Item.PORKCHOP, 0, ThreadLocalRandom.current().nextInt(1, 4)),
                Item.get(Item.SADDLE, 0, getDataFlag(DATA_FLAG_SADDLED) ? 1 : 0),
        };
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.CARROT || id == Item.POTATO || id == Item.BEETROOT;
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
        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : EntityLink.TYPE_PASSENGER, false, false, 0f);
        }

        int maxHealth = getMaxHealth();
        addEntity.attributes = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH).setMaxValue(maxHealth).setDefaultMaxValue(maxHealth).setDefaultValue(maxHealth).setValue(getHealth()),
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
            return new Vector3f(0, 1.85001f, 0);
        }
        return new Vector3f(0, 0.63f + entity.getRidingOffset(), 0);
    }

    @Override
    protected void onMountEntity(Entity entity) {
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION, 0));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_LOCK_PASSENGER_ROTATION_DEGREES, 181));
        entity.setDataProperty(new ByteEntityData(DATA_SEAT_ROTATION_OFFSET, 0));
        entity.setDataProperty(new FloatEntityData(DATA_SEAT_ROTATION_OFFSET_DEGREES, 0));
    }

    @Override
    public boolean canDoInteraction(Player player) {
        if (isBaby()) {
            return false;
        }
        if (!getDataFlag(DATA_FLAG_SADDLED)) {
            return player.getInventory().getItemInHand().is(Item.SADDLE);
        }
        if (player.isSneaking()) {
            return false;
        }
        return passengers.isEmpty();
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (isBaby()) {
            return "";
        }
        if (!getDataFlag(DATA_FLAG_SADDLED)) {
            return player.getInventory().getItemInHand().is(Item.SADDLE) ? "action.interact.saddle" : "";
        }
        if (player.isSneaking()) {
            return "";
        }
        return passengers.isEmpty() ? "action.interact.ride.horse" : "";
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (isBaby()) {
            return false;
        }

        if (!getDataFlag(DATA_FLAG_SADDLED)) {
            if (!item.is(Item.SADDLE)) {
                return false;
            }

            setDataFlag(DATA_FLAG_SADDLED, true);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SADDLE);
            return true;
        }

        if (player.isSneaking()) {
            return false;
        }

        if (!passengers.isEmpty()) {
            return false;
        }

        mountEntity(player);
        return false;
    }
}
