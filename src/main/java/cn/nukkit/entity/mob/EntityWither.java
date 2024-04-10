package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.potion.Effect;

/**
 * @author PikyCZ
 */
public class EntityWither extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = EntityID.WITHER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityWither(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 3;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(600);
        fireProof = true;
    }

    @Override
    public String getName() {
        return "Wither";
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

        addEntity.attributes = new Attribute[]{
                Attribute.getAttribute(Attribute.HEALTH)
                        .setMaxValue(getMaxHealth())
                        .setValue(getHealth()),
        };

        return addEntity;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(Item.NETHER_STAR),
        };
    }

    @Override
    public boolean canBeAffected(int effectId) {
        return effectId == Effect.INSTANT_DAMAGE || effectId == Effect.INSTANT_HEALTH;
    }

    @Override
    public int getBaseArmorValue() {
        return 4;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }
}
