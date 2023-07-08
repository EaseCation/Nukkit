package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class LongEntityData extends EntityData<Long> {
    public long data;

    public LongEntityData(int id, long data) {
        super(id);
        this.data = data;
    }

    @Override
    public Long getData() {
        return data;
    }

    @Override
    public long getDataAsLong() {
        return data;
    }

    @Override
    public void setData(Long data) {
        this.data = data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_LONG;
    }

    @Override
    protected boolean equalsData(EntityData<?> data) {
        return this.data == ((LongEntityData) data).data;
    }
}
