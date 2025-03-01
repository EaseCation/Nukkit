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
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityStrider extends EntityAnimal implements EntityInteractable, EntityRideable {

    public final static int NETWORK_ID = EntityID.STRIDER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityStrider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        fireProof = true;

        movementSpeed = 0.16f;
        lavaMovementSpeed = 0.32f;

        setDataFlag(DATA_FLAG_SADDLED, namedTag.getBoolean("Saddled"), false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("Saddled", getDataFlag(DATA_FLAG_SADDLED));
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public String getName() {
        return "Strider";
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
    public Item[] getDrops() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Item[]{
                Item.get(Item.STRING, 0, random.nextInt(2, 6)),
                Item.get(Item.SADDLE, 0, getDataFlag(DATA_FLAG_SADDLED) ? 1 : 0),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        switch (entity.getNetworkId()) {
            case -1:
                return new Vector3f(0, 2.8200102f, -0.2f);
            case EntityID.STRIDER:
                return new Vector3f(0, 1.6f + entity.getRidingOffset(), 0);
        }
        return new Vector3f(0, 1.65f + entity.getRidingOffset(), -0.2f);
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
        return passengers.isEmpty() ? "action.interact.ride.strider" : "";
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
