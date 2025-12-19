package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntityTropicalFish;

public class ItemBucketTropicalFish extends ItemBucketWater {
    public ItemBucketTropicalFish() {
        this( 0, 1);
    }

    public ItemBucketTropicalFish(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketTropicalFish(Integer meta, int count) {
        super(TROPICAL_FISH_BUCKET, meta, count, "Bucket of Tropical Fish");
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityTropicalFish::new;
    }
}
