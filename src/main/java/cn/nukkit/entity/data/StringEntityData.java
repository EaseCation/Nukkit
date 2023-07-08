package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class StringEntityData extends EntityData<String> {
    public String data;

    public StringEntityData(int id, String data) {
        super(id);
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getDataAsString() {
        return getData();
    }

    @Override
    public void setData(String data) {
        if (data != null) {
            this.data = data;
        } else {
            this.data = "";
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_STRING;
    }
}
