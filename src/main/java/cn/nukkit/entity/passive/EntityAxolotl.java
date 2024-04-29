package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityAxolotl extends EntityAnimal {

    public static final int NETWORK_ID = EntityID.AXOLOTL;

    public static final int AXOLOTL_VARIANT_LUCY = 0;
    public static final int AXOLOTL_VARIANT_CYAN = 1;
    public static final int AXOLOTL_VARIANT_GOLD = 2;
    public static final int AXOLOTL_VARIANT_WILD = 3;
    public static final int AXOLOTL_VARIANT_BLUE = 4;

    public EntityAxolotl(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        int variant;
        if (namedTag.contains("Variant")) {
            variant = namedTag.getInt("Variant");
        } else {
            variant = ThreadLocalRandom.current().nextInt(4);
        }
        dataProperties.putInt(DATA_VARIANT, variant);

        this.setMaxHealth(14);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Variant", getDataPropertyInt(DATA_VARIANT));
    }

    @Override
    public String getName() {
        return "Axolotl";
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
