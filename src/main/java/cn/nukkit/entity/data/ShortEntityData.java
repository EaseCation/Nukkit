package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ShortEntityData extends EntityData<Integer> {
    public int data;

    public ShortEntityData(int id, int data) {
        super(id);
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public int getDataAsShort() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        if (data == null) {
            this.data = 0;
        } else {
            this.data = data;
        }
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SHORT;
    }

    @Override
    protected boolean equalsData(EntityData<?> data) {
        return this.data == ((ShortEntityData) data).data;
    }
}
