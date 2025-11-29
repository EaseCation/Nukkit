package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.property.EntityPropertyRegistry;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FloatingTextParticle extends Particle {

    protected final Level level;
    protected long entityId = -1;
    protected boolean invisible = false;
    protected String text;
    protected EntityMetadata metadata = new EntityMetadata();

    public FloatingTextParticle(Location location, String text) {
        this(location.getLevel(), location, text);
    }

    public FloatingTextParticle(Vector3 pos, String text) {
        this(null, pos, text);
    }

    private FloatingTextParticle(Level level, Vector3 pos, String text) {
        super(pos.x, pos.y, pos.z);
        this.level = level;
        this.text = text;

        long flags = 0;
        flags ^= 1 << Entity.DATA_FLAG_NO_AI;
        flags ^= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;

        this.metadata.putLong(Entity.DATA_FLAGS, flags)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putByte(Entity.DATA_ALWAYS_SHOW_NAMETAG, 1)
                .putFloat(Entity.DATA_SCALE, 0.001f) // Zero causes problems on debug builds?
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0.01f)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0.01f);

        updateNameTag();
    }

    public String getText() {
        return this.text == null ? "" : this.text;
    }

    public void setText(String text) {
        if (this.text == null) {
            if (text == null) {
                return;
            }
        } else if (this.text.equals(text)) {
            return;
        }
        this.text = text;

        updateNameTag();
        sendMetadata();
    }

    private void updateNameTag() {
        this.metadata.putString(Entity.DATA_NAMETAG, getText());
    }

    private void sendMetadata() {
        if (this.level != null) {
            SetEntityDataPacket packet = new SetEntityDataPacket();
            packet.eid = entityId;
            packet.metadata = this.metadata;
            this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        if (invisible == this.invisible) {
            return;
        }
        this.invisible = invisible;

        if (this.level != null) {
            if (invisible) {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getRemovePacket());
            } else {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getAddPacket());
            }
        }
    }

    public long getEntityId() {
        return this.entityId;
    }

    @Override
    public DataPacket[] encode() {
        List<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = Entity.entityCount++;
        } else {
            packets.add(getRemovePacket());
        }

        if (!this.invisible) {
            packets.add(getAddPacket());
        }

        return packets.toArray(new DataPacket[0]);
    }

    private AddEntityPacket getAddPacket() {
        AddEntityPacket pk = new AddEntityPacket();
        pk.id = EntityFullNames.ARMOR_STAND;
        pk.entityUniqueId = this.entityId;
        pk.entityRuntimeId = this.entityId;
        pk.x = (float) this.x;
        pk.y = (float) this.y - 0.75f;
        pk.z = (float) this.z;
        pk.metadata = this.metadata;
        Pair<Int2IntMap, Int2FloatMap> propertyValues = EntityPropertyRegistry.getProperties(EntityID.ARMOR_STAND).getDefaultValues();
        if (propertyValues != null) {
            pk.intProperties = propertyValues.left();
            pk.floatProperties = propertyValues.right();
        }
        return pk;
    }

    private RemoveEntityPacket getRemovePacket() {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = this.entityId;
        return pk;
    }
}