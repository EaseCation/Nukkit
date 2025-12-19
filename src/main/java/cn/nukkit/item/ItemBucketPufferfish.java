package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntityPufferfish;

public class ItemBucketPufferfish extends ItemBucketWater {
    public ItemBucketPufferfish() {
        this( 0, 1);
    }

    public ItemBucketPufferfish(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketPufferfish(Integer meta, int count) {
        super(PUFFERFISH_BUCKET, meta, count, "Bucket of Pufferfish");
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityPufferfish::new;
    }
}
