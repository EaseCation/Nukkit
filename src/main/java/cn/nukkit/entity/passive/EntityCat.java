package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityCat extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.CAT;

    public static final int CAT_VARIANT_WHITE = 0;
    public static final int CAT_VARIANT_TUXEDO = 1;
    public static final int CAT_VARIANT_RED = 2;
    public static final int CAT_VARIANT_SIAMESE = 3;
    public static final int CAT_VARIANT_BRITISH = 4;
    public static final int CAT_VARIANT_CALICO = 5;
    public static final int CAT_VARIANT_PERSIAN = 6;
    public static final int CAT_VARIANT_RAGDOLL = 7;
    public static final int CAT_VARIANT_TABBY = 8;
    public static final int CAT_VARIANT_BLACK = 9;
    public static final int CAT_VARIANT_JELLIE = 10;

    public EntityCat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Cat";
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        int variant;
        if (namedTag.contains("Variant")) {
            variant = namedTag.getInt("Variant");
        } else {
            variant = ThreadLocalRandom.current().nextInt(11);
        }
        dataProperties.putInt(DATA_VARIANT, variant);

        this.setMaxHealth(10);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
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
    public Item[] getDrops() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Item[]{
                Item.get(Item.STRING, 0, random.nextInt(3)),
        };
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, 0.35f + entity.getRidingOffset(), 0);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }
}
