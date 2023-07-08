package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "tag")
public class SlotEntityData extends EntityData<Item> {
    public int blockId;
    public int meta;
    public int count;
    public byte[] tag;

    public SlotEntityData(int id, int blockId, int meta, int count) {
        this(id, blockId, meta, count, null);
    }

    public SlotEntityData(int id, int blockId, int meta, int count, byte[] tag) {
        super(id);
        this.blockId = blockId;
        this.meta = meta;
        this.count = count;
        this.tag = tag;
    }

    public SlotEntityData(int id, Item item) {
        this(id, item.getId(), (byte) (item.hasMeta() ? item.getDamage() : 0), item.getCount(), item.getCompoundTag());
    }

    public SlotEntityData(int id, SlotEntityData item) {
        this(id, item.blockId, item.meta, item.count, item.tag);
    }

    @Override
    public Item getData() {
        return Item.get(blockId, meta, count, tag);
    }

    @Override
    public Item getDataAsItemStack() {
        return getData();
    }

    @Override
    public void setData(Item data) {
        if (data != null) {
            this.blockId = data.getId();
            this.meta = data.hasMeta() ? data.getDamage() : 0;
            this.count = data.getCount();
            this.tag = data.getCompoundTag();
        } else {
            this.blockId = 0;
            this.meta = 0;
            this.count = 0;
            this.tag = null;
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SLOT;
    }
}
