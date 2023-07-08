package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class Vector3fEntityData extends EntityData<Vector3f> {
    public float x;
    public float y;
    public float z;

    public Vector3fEntityData(int id, float x, float y, float z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3fEntityData(int id, Vector3f pos) {
        this(id, pos.x, pos.y, pos.z);
    }

    public Vector3fEntityData(int id, Vector3fEntityData data) {
        this(id, data.x, data.y, data.z);
    }

    @Override
    public Vector3f getData() {
        return new Vector3f(x, y, z);
    }

    @Override
    public Vector3f getDataAsVec3() {
        return getData();
    }

    @Override
    public void setData(Vector3f data) {
        if (data != null) {
            this.x = data.x;
            this.y = data.y;
            this.z = data.z;
        } else {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_VECTOR3F;
    }

    @Override
    protected boolean equalsData(EntityData<?> data) {
        Vector3fEntityData vec3 = (Vector3fEntityData) data;
        return this.x == vec3.x && this.y == vec3.y && this.z == vec3.z;
    }
}
