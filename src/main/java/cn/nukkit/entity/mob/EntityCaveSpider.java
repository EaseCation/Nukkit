package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public class EntityCaveSpider extends EntityMob implements EntityArthropod {

    public static final int NETWORK_ID = EntityID.CAVE_SPIDER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityCaveSpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(12);
        this.setDataFlag(DATA_FLAG_RENDERS_WHEN_INVISIBLE, true, false);
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "Cave Spider";
    }

    @Override
    public Item[] getDrops() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Item[]{
                Item.get(Item.STRING, 0, random.nextInt(3)),
        };
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
    public boolean canBeAffected(int effectId) {
        if (effectId == Effect.POISON) {
            return false;
        }
        return super.canBeAffected(effectId);
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        switch (entity.getNetworkId()) {
            case EntityID.SKELETON:
            case EntityID.STRAY:
            case EntityID.BOGGED:
            case EntityID.PARCHED:
            case EntityID.WITHER_SKELETON:
                return new Vector3f(0, 0.325f + entity.getRidingOffset(), -0.1f);
        }
        return new Vector3f(0, 0.325f + entity.getRidingOffset(), 0);
    }
}
