package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class EntityTropicalFish extends EntityWaterAnimal {

    public static final int NETWORK_ID = EntityID.TROPICALFISH;

    public static final int FISH_VARIANT_A = 0;
    public static final int FISH_VARIANT_B = 1;

    public static final int FISH_MARK_1 = 0;
    public static final int FISH_MARK_2 = 1;
    public static final int FISH_MARK_3 = 2;
    public static final int FISH_MARK_4 = 3;
    public static final int FISH_MARK_5 = 4;
    public static final int FISH_MARK_6 = 5;

    public EntityTropicalFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public String getName() {
        return "Tropical Fish";
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        int variant;
        if (namedTag.contains("Variant")) {
            variant = namedTag.getInt("Variant");
        } else {
            variant = ThreadLocalRandom.current().nextInt(2);
        }
        dataProperties.putInt(DATA_VARIANT, variant);

        int pattern;
        if (namedTag.contains("MarkVariant")) {
            pattern = namedTag.getInt("MarkVariant");
        } else {
            pattern = ThreadLocalRandom.current().nextInt(6);
        }
        dataProperties.putInt(DATA_MARK_VARIANT, pattern);

        int baseColor;
        if (namedTag.contains("Color")) {
            baseColor = namedTag.getByte("Color");
        } else {
            baseColor = ThreadLocalRandom.current().nextInt(15); // exclude black (15)
        }
        dataProperties.putByte(DATA_COLOR, baseColor);

        int patternColor;
        if (namedTag.contains("Color2")) {
            patternColor = namedTag.getByte("Color2");
        } else {
            patternColor = ThreadLocalRandom.current().nextInt(15); // exclude black (15)
        }
        dataProperties.putByte(DATA_COLOR_2, patternColor);

        this.setMaxHealth(3);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
        namedTag.putInt("MarkVariant", getDataPropertyInt(DATA_MARK_VARIANT));
        namedTag.putByte("Color", getDataPropertyByte(DATA_COLOR));
        namedTag.putByte("Color2", getDataPropertyByte(DATA_COLOR_2));
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
        return new Item[]{
                Item.get(Item.TROPICAL_FISH),
        };
    }
}
