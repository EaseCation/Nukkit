package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.passive.EntityAxolotl;

public class ItemBucketAxolotl extends ItemBucketWater {
    public ItemBucketAxolotl() {
        this( 0, 1);
    }

    public ItemBucketAxolotl(Integer meta) {
        this(meta, 1);
    }

    public ItemBucketAxolotl(Integer meta, int count) {
        super(AXOLOTL_BUCKET, meta, count, "Bucket of Axolotl");
    }

    @Override
    public String getDescriptionId() {
        return "item.bucketAxolotl.name";
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityAxolotl::new;
    }
}
