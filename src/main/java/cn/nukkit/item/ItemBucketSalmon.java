package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntitySalmon;

public class ItemBucketSalmon extends ItemBucketWater {
    public ItemBucketSalmon() {
        this( 0, 1);
    }

    public ItemBucketSalmon(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketSalmon(Integer meta, int count) {
        super(SALMON_BUCKET, meta, count, "Bucket of Salmon");
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntitySalmon::new;
    }
}
