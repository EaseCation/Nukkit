package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class IntPositionEntityData extends EntityData<BlockVector3> {
    public int x;
    public int y;
    public int z;

    public IntPositionEntityData(int id, int x, int y, int z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntPositionEntityData(int id, BlockVector3 pos) {
        this(id, pos.x, pos.y, pos.z);
    }

    public IntPositionEntityData(int id, IntPositionEntityData data) {
        this(id, data.x, data.y, data.z);
    }

    @Override
    public BlockVector3 getData() {
        return new BlockVector3(x, y, z);
    }

    @Override
    public BlockVector3 getDataAsBlockPos() {
        return getData();
    }

    @Override
    public void setData(BlockVector3 data) {
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
        return Entity.DATA_TYPE_POS;
    }

    @Override
    protected boolean equalsData(EntityData<?> data) {
        IntPositionEntityData blockPos = (IntPositionEntityData) data;
        return this.x == blockPos.x && this.y == blockPos.y && this.z == blockPos.z;
    }
}
