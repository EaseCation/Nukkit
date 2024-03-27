package cn.nukkit.entity.data;

import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class EntityMetadata {

    private final Int2ObjectMap<EntityData> map = new Int2ObjectOpenHashMap<>();

    @Nullable
    public EntityData get(int id) {
        return this.getOrDefault(id, null);
    }

    @Nullable
    public EntityData getOrDefault(int id, @Nullable EntityData defaultValue) {
        EntityData<?> data = this.map.getOrDefault(id, defaultValue);
        if (data == null) {
            return null;
        }
        return data.setId(id);
    }

    public boolean exists(int id) {
        return this.map.containsKey(id);
    }

    public EntityMetadata put(EntityData data) {
        this.map.put(data.getId(), data);
        return this;
    }

    public int getByte(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return 0;
        }
        return data.getDataAsByte();
    }

    public int getShort(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return 0;
        }
        return data.getDataAsShort();
    }

    public int getInt(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return 0;
        }
        return data.getDataAsInt();
    }

    public long getLong(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return 0;
        }
        return data.getDataAsLong();
    }

    public float getFloat(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return 0;
        }
        return data.getDataAsFloat();
    }

    public boolean getBoolean(int id) {
        return this.getByte(id) == 1;
    }

    public CompoundTag getNBT(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return new CompoundTag();
        }
        return data.getDataAsNbt();
    }

    public Item getSlot(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return Items.air();
        }
        return data.getDataAsItemStack();
    }

    public String getString(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return "";
        }
        return data.getDataAsString();
    }

    public BlockVector3 getPosition(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return new BlockVector3();
        }
        return data.getDataAsBlockPos();
    }

    public Vector3f getFloatPosition(int id) {
        EntityData<?> data = this.get(id);
        if (data == null) {
            return new Vector3f();
        }
        return data.getDataAsVec3();
    }

    public EntityMetadata putByte(int id, int value) {
        return this.put(new ByteEntityData(id, value));
    }

    public EntityMetadata putShort(int id, int value) {
        return this.put(new ShortEntityData(id, value));
    }

    public EntityMetadata putInt(int id, int value) {
        return this.put(new IntEntityData(id, value));
    }

    public EntityMetadata putLong(int id, long value) {
        return this.put(new LongEntityData(id, value));
    }

    public EntityMetadata putFloat(int id, float value) {
        return this.put(new FloatEntityData(id, value));
    }

    public EntityMetadata putBoolean(int id, boolean value) {
        return this.putByte(id, value ? 1 : 0);
    }

    public EntityMetadata putNBT(int id, CompoundTag value) {
        return this.put(new NBTEntityData(id, value));
    }

    public EntityMetadata putSlot(int id, int blockId, int meta, int count) {
        return this.put(new SlotEntityData(id, blockId, (byte) meta, count));
    }

    public EntityMetadata putSlot(int id, Item value) {
        return this.put(new SlotEntityData(id, value));
    }

    public EntityMetadata putString(int id, String value) {
        return this.put(new StringEntityData(id, value));
    }

    public EntityMetadata putPosition(int id, BlockVector3 value) {
        return this.put(new IntPositionEntityData(id, value));
    }

    public EntityMetadata putPosition(int id, int x, int y, int z) {
        return this.put(new IntPositionEntityData(id, x, y, z));
    }

    public EntityMetadata putFloatPosition(int id, Vector3f value) {
        return this.put(new Vector3fEntityData(id, value));
    }

    public EntityMetadata putFloatPosition(int id, float x, float y, float z) {
        return this.put(new Vector3fEntityData(id, x, y, z));
    }

    public Int2ObjectMap<EntityData> getMap() {
        return new Int2ObjectOpenHashMap<>(map);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
