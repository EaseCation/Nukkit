package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityParrot extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.PARROT;

    public static final int PARROT_VARIANT_RED = 0;
    public static final int PARROT_VARIANT_BLUE = 1;
    public static final int PARROT_VARIANT_GREEN = 2;
    public static final int PARROT_VARIANT_CYAN = 3;
    public static final int PARROT_VARIANT_SILVER = 4;

    public EntityParrot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public String getName() {
        return "Parrot";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        int variant;
        if (namedTag.contains("Variant")) {
            variant = namedTag.getInt("Variant");
        } else {
            variant = ThreadLocalRandom.current().nextInt(5);
        }
        dataProperties.putInt(DATA_VARIANT, variant);

        this.setMaxHealth(6);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{
                Item.get(Item.FEATHER, 0, ThreadLocalRandom.current().nextInt(1, 3)),
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
