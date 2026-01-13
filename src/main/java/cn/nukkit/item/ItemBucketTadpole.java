package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntityTadpole;

public class ItemBucketTadpole extends ItemBucketWater {
    public ItemBucketTadpole() {
        this( 0, 1);
    }

    public ItemBucketTadpole(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketTadpole(Integer meta, int count) {
        super(TADPOLE_BUCKET, meta, count, "Bucket of Tadpole");
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketTadpole.name";
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityTadpole::new;
    }
}
