package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetEntityDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_ACTOR_DATA_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public long eid;
    public EntityMetadata metadata;

    public Int2IntMap intProperties = new Int2IntOpenHashMap();
    public Int2FloatMap floatProperties = new Int2FloatOpenHashMap();

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
//        this.metadata = Binary.readMetadata(this.get()); //FIXME
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putEntityMetadata(this.metadata);
    }
}
