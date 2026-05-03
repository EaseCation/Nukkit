package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.mob.EntitySulfurCube;

public class ItemBucketSulfurCube extends ItemBucketEntity {
    public ItemBucketSulfurCube() {
        this( 0, 1);
    }

    public ItemBucketSulfurCube(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketSulfurCube(Integer meta, int count) {
        super(SULFUR_CUBE_BUCKET, meta, count, "Bucket of Sulfur Cube");
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketSulfurCube.name";
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntitySulfurCube::new;
    }
}
