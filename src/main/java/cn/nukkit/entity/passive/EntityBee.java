package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kaooot
 * @version 1.0
 */
public class EntityBee extends EntityAnimal implements EntityArthropod {

    public static final int NETWORK_ID = EntityID.BEE;

    public static final int BEE_MARK_DEFAULT = 0;
    public static final int BEE_MARK_COUNTDOWN_TO_PERISH = 1;

    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Bee";
    }

    @Override
    public float getWidth() {
        return 0.55f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_MARK_VARIANT, namedTag.getInt("MarkVariant", BEE_MARK_DEFAULT));

        this.setMaxHealth(10);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("MarkVariant", getDataPropertyInt(DATA_MARK_VARIANT));
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
