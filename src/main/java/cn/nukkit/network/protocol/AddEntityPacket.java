package cn.nukkit.network.protocol;

import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.types.EntityLink;
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
public class AddEntityPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ADD_ACTOR_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int type;
    public String id;
    public float x;
    public float y;
    public float z;
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    public EntityMetadata metadata = new EntityMetadata();
    public Int2IntMap intProperties = new Int2IntOpenHashMap();
    public Int2FloatMap floatProperties = new Int2FloatOpenHashMap();
    public Attribute[] attributes = new Attribute[0];
    public EntityLink[] links = new EntityLink[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putUnsignedVarInt(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putAttributeList(this.attributes);
        this.putEntityMetadata(this.metadata);
        this.putUnsignedVarInt(this.links.length);
        for (EntityLink link : this.links) {
            this.putEntityLink(link);
        }
    }
}
