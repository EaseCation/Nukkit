package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntityPhantom extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = EntityID.PHANTOM;

    public EntityPhantom(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        this.setDataFlag(DATA_FLAG_RENDERS_WHEN_INVISIBLE, true, false);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "Phantom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
//                Item.get(Item.PHANTOM_MEMBRANE, 0, ThreadLocalRandom.current().nextInt(2)),
        };
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
