package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class FloatEntityData extends EntityData<Float> {
    public float data;

    public FloatEntityData(int id, float data) {
        super(id);
        this.data = data;
    }

    @Override
    public Float getData() {
        return data;
    }

    @Override
    public float getDataAsFloat() {
        return data;
    }

    @Override
    public void setData(Float data) {
        if (data == null) {
            this.data = 0;
        } else {
            this.data = data;
        }
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_FLOAT;
    }

    @Override
    protected boolean equalsData(EntityData<?> data) {
        return this.data == ((FloatEntityData) data).data;
    }
}
