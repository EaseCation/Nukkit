package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityVillager extends EntityCreature implements EntityAgeable {

    public static final int NETWORK_ID = EntityID.VILLAGER_V2;

    public static final int PROFESSION_UNSKILLED = 0;
    public static final int PROFESSION_FARMER = 1;
    public static final int PROFESSION_FISHERMAN = 2;
    public static final int PROFESSION_SHEPHERD = 3;
    public static final int PROFESSION_FLETCHER = 4;
    public static final int PROFESSION_LIBRARIAN = 5;
    public static final int PROFESSION_CARTOGRAPHER = 6;
    public static final int PROFESSION_CLERIC = 7;
    public static final int PROFESSION_ARMORER = 8;
    public static final int PROFESSION_WEAPON_SMITH = 9;
    public static final int PROFESSION_TOOL_SMITH = 10;
    public static final int PROFESSION_BUTCHER = 11;
    public static final int PROFESSION_LEATHERWORKER = 12;
    public static final int PROFESSION_STONEMASON = 13;
    public static final int PROFESSION_NITWIT = 14;

    public static final int BIOME_PLAINS = 0;
    public static final int BIOME_DESERT = 1;
    public static final int BIOME_JUNGLE = 2;
    public static final int BIOME_SAVANNA = 3;
    public static final int BIOME_SNOW = 4;
    public static final int BIOME_SWAMP = 5;
    public static final int BIOME_TAIGA = 6;

    public EntityVillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public String getName() {
        return "Villager";
    }

    @Override
    public void initEntity() {
        super.initEntity();

        dataProperties.putInt(DATA_VARIANT, namedTag.getInt("Variant", PROFESSION_UNSKILLED));
        dataProperties.putInt(DATA_MARK_VARIANT, namedTag.getInt("MarkVariant", BIOME_PLAINS));

        this.setMaxHealth(20);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
        namedTag.putInt("MarkVariant", getDataPropertyInt(DATA_MARK_VARIANT));
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAG_BABY);
    }

    public void setBaby(boolean baby) {
        this.setDataFlag(DATA_FLAG_BABY, baby);
        this.setScale(baby ? 0.5f : 1);
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
