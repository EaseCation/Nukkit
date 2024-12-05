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
public class EntitySpider extends EntityMob implements EntityArthropod {

    public static final int NETWORK_ID = EntityID.SPIDER;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
        this.setDataFlag(DATA_FLAG_RENDERS_WHEN_INVISIBLE, true, false);
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public String getName() {
        return "Spider";
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
            case EntityID.WITHER_SKELETON:
                return new Vector3f(0, 0.54f + entity.getRidingOffset(), 0);
        }
        return new Vector3f(0, 0.54f + entity.getRidingOffset(), -0.1f);
    }
}
