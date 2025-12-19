package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntityCod;

public class ItemBucketCod extends ItemBucketWater {
    public ItemBucketCod() {
        this( 0, 1);
    }

    public ItemBucketCod(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketCod(Integer meta, int count) {
        super(COD_BUCKET, meta, count, "Bucket of Cod");
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityCod::new;
    }
}
