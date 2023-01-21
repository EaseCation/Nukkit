package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.utils.Binary;
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

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
//        this.metadata = Binary.readMetadata(this.get()); //FIXME
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.put(Binary.writeMetadata(this.metadata));
    }
}
