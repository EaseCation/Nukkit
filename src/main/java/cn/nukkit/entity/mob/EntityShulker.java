package cn.nukkit.entity.mob;

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
public class EntityShulker extends EntityMob {

    public static final int NETWORK_ID = EntityID.SHULKER;

    public static final int SHULKER_VARIANT_BLACK = 0;
    public static final int SHULKER_VARIANT_RED = 1;
    public static final int SHULKER_VARIANT_GREEN = 2;
    public static final int SHULKER_VARIANT_BROWN = 3;
    public static final int SHULKER_VARIANT_BLUE = 4;
    public static final int SHULKER_VARIANT_PURPLE = 5;
    public static final int SHULKER_VARIANT_CYAN = 6;
    public static final int SHULKER_VARIANT_SILVER = 7;
    public static final int SHULKER_VARIANT_GRAY = 8;
    public static final int SHULKER_VARIANT_PINK = 9;
    public static final int SHULKER_VARIANT_LIME = 10;
    public static final int SHULKER_VARIANT_YELLOW = 11;
    public static final int SHULKER_VARIANT_LIGHT_BLUE = 12;
    public static final int SHULKER_VARIANT_MAGENTA = 13;
    public static final int SHULKER_VARIANT_ORANGE = 14;
    public static final int SHULKER_VARIANT_WHITE = 15;
    public static final int SHULKER_VARIANT_UNDYED = 16;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityShulker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", SHULKER_VARIANT_UNDYED));

        this.setMaxHealth(30);

        fireProof = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }

    @Override
    public String getName() {
        return "Shulker";
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
                Item.get(Item.SHULKER_SHELL, 0, random.nextInt(2)),
        };
    }

    @Override
    public int getBaseArmorValue() {
        if (getDataPropertyInt(DATA_SHULKER_PEEK_ID) == 0) {
            return 20;
        }
        return 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.FALL) {
            return false;
        }
        return super.attack(source);
    }
}
