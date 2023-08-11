package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

public class CompoundTag extends Tag implements Cloneable {
    public static final byte[] EMPTY;

    static {
        try {
            EMPTY = NBTIO.writeNetwork(new CompoundTag(Collections.emptyMap()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, Tag> tags;

    public CompoundTag() {
        this("");
    }

    public CompoundTag(String name) {
        this(name, new Object2ObjectOpenHashMap<>());
    }

    public CompoundTag(int initialCapacity) {
        this("", initialCapacity);
    }

    public CompoundTag(String name, int initialCapacity) {
        this(name, new Object2ObjectOpenHashMap<>(initialCapacity));
    }

    public CompoundTag(Map<String, Tag> tags) {
        this("", tags);
    }

    public CompoundTag(String name, Map<String, Tag> tags) {
        super(name);
        this.tags = tags;
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        for (Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            Tag.writeNamedTag(entry.getValue(), entry.getKey(), dos);
        }

        dos.writeByte(Tag.TAG_End);
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        tags.clear();
        Tag tag;
        while ((tag = Tag.readNamedTag(dis)).getId() != Tag.TAG_End) {
            tags.put(tag.getName(), tag);
        }
    }

    public Set<Entry<String, Tag>> entrySet() {
        return tags.entrySet();
    }

    public Set<String> keySet() {
        return tags.keySet();
    }

    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    @Override
    public byte getId() {
        return TAG_Compound;
    }

    public CompoundTag put(String name, Tag tag) {
        tags.put(name, tag.setName(name));
        return this;
    }

    public Tag putIfAbsent(String name, Tag tag) {
        return tags.putIfAbsent(name, tag.setName(name));
    }

    public CompoundTag putByte(String name, int value) {
        tags.put(name, new ByteTag(name, value));
        return this;
    }

    public Tag putByteIfAbsent(String name, int value) {
        return tags.putIfAbsent(name, new ByteTag(name, value));
    }

    public CompoundTag putShort(String name, int value) {
        tags.put(name, new ShortTag(name, value));
        return this;
    }

    public Tag putShortIfAbsent(String name, int value) {
        return tags.putIfAbsent(name, new ShortTag(name, value));
    }

    public CompoundTag putInt(String name, int value) {
        tags.put(name, new IntTag(name, value));
        return this;
    }

    public Tag putIntIfAbsent(String name, int value) {
        return tags.putIfAbsent(name, new IntTag(name, value));
    }

    public CompoundTag putLong(String name, long value) {
        tags.put(name, new LongTag(name, value));
        return this;
    }

    public Tag putLongIfAbsent(String name, long value) {
        return tags.putIfAbsent(name, new LongTag(name, value));
    }

    public CompoundTag putFloat(String name, float value) {
        tags.put(name, new FloatTag(name, value));
        return this;
    }

    public Tag putFloatIfAbsent(String name, float value) {
        return tags.putIfAbsent(name, new FloatTag(name, value));
    }

    public CompoundTag putDouble(String name, double value) {
        tags.put(name, new DoubleTag(name, value));
        return this;
    }

    public Tag putDoubleIfAbsent(String name, double value) {
        return tags.putIfAbsent(name, new DoubleTag(name, value));
    }

    public CompoundTag putString(String name, String value) {
        tags.put(name, new StringTag(name, value));
        return this;
    }

    public Tag putStringIfAbsent(String name, String value) {
        return tags.putIfAbsent(name, new StringTag(name, value));
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        tags.put(name, new ByteArrayTag(name, value));
        return this;
    }

    public Tag putByteArrayIfAbsent(String name, byte[] value) {
        return tags.putIfAbsent(name, new ByteArrayTag(name, value));
    }

    public CompoundTag putIntArray(String name, int[] value) {
        tags.put(name, new IntArrayTag(name, value));
        return this;
    }

    public Tag putIntArrayIfAbsent(String name, int[] value) {
        return tags.putIfAbsent(name, new IntArrayTag(name, value));
    }

    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        tags.put(listTag.getName(), listTag);
        return this;
    }

    public CompoundTag putList(String name, ListTag<? extends Tag> value) {
        tags.put(name, value.setName(name));
        return this;
    }

    public Tag putListIfAbsent(ListTag<? extends Tag> listTag) {
        return tags.putIfAbsent(listTag.getName(), listTag);
    }

    public Tag putListIfAbsent(String name, ListTag<? extends Tag> value) {
        return tags.putIfAbsent(name, value.setName(name));
    }

    public CompoundTag putCompound(CompoundTag compound) {
        tags.put(compound.getName(), compound);
        return this;
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        tags.put(name, value.setName(name));
        return this;
    }

    public Tag putCompoundIfAbsent(CompoundTag compound) {
        return tags.putIfAbsent(compound.getName(), compound);
    }

    public Tag putCompoundIfAbsent(String name, CompoundTag value) {
        return tags.putIfAbsent(name, value.setName(name));
    }

    public CompoundTag putBoolean(String string, boolean val) {
        putByte(string, val ? 1 : 0);
        return this;
    }

    public Tag putBooleanIfAbsent(String string, boolean val) {
        return putByteIfAbsent(string, val ? 1 : 0);
    }

    @Nullable
    public Tag get(String name) {
        return tags.get(name);
    }

    public boolean contains(String name) {
        return tags.containsKey(name);
    }

    public CompoundTag remove(String name) {
        tags.remove(name);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Tag> T removeAndGet(String name) {
        return (T) tags.remove(name);
    }

    public int getByte(String name) {
        return getByte(name, 0);
    }

    public int getByte(String name, int defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof ByteTag)) {
            return defaultValue;
        }
        return ((ByteTag) tag).data;
    }

    public int getShort(String name) {
        return getShort(name, 0);
    }

    public int getShort(String name, int defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof ShortTag)) {
            return defaultValue;
        }
        return ((ShortTag) tag).data;
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof IntTag)) {
            return defaultValue;
        }
        return ((IntTag) tag).data;
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public long getLong(String name, long defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof LongTag)) {
            return defaultValue;
        }
        return ((LongTag) tag).data;
    }

    public float getFloat(String name) {
        return getFloat(name, 0);
    }

    public float getFloat(String name, float defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof FloatTag)) {
            return defaultValue;
        }
        return ((FloatTag) tag).data;
    }

    public double getDouble(String name) {
        return getDouble(name, 0);
    }

    public double getDouble(String name, double defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof DoubleTag)) {
            return defaultValue;
        }
        return ((DoubleTag) tag).data;
    }

    public String getString(String name) {
        return getString(name, "");
    }

    public String getString(String name, String defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof StringTag)) {
            return defaultValue;
        }
        return ((StringTag) tag).data;
    }

    public byte[] getByteArray(String name) {
        return getByteArray(name, new byte[0]);
    }

    public byte[] getByteArray(String name, byte[] defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof ByteArrayTag)) {
            return defaultValue;
        }
        return ((ByteArrayTag) tag).data;
    }

    public int[] getIntArray(String name) {
        return getIntArray(name, new int[0]);
    }

    public int[] getIntArray(String name, int[] defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof IntArrayTag)) {
            return defaultValue;
        }
        return ((IntArrayTag) tag).data;
    }

    public CompoundTag getCompound(String name) {
        return getCompound(name, new CompoundTag(name));
    }

    public CompoundTag getCompound(String name, CompoundTag defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof CompoundTag)) {
            return defaultValue;
        }
        return (CompoundTag) tag;
    }

    public ListTag<? extends Tag> getList(String name) {
        return getList(name, new ListTag<>(name));
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(String name, ListTag<T> defaultValue) {
        Tag tag = get(name);
        if (!(tag instanceof ListTag)) {
            return defaultValue;
        }
        return (ListTag<T>) tag;
    }

    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        return getList(name, new ListTag<>(name));
    }

    public Map<String, Tag> getTags() {
        return new Object2ObjectOpenHashMap<>(this.tags);
    }

    public Map<String, Tag> getTagsUnsafe() {
        return this.tags;
    }

    @Override
    public Map<String, Object> parseValue() {
        Map<String, Object> value = new Object2ObjectOpenHashMap<>(this.tags.size());

        for (Entry<String, Tag> entry : this.tags.entrySet()) {
            value.put(entry.getKey(), entry.getValue().parseValue());
        }

        return value;
    }

    public boolean getBoolean(String name) {
        return getByte(name) != 0;
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n\t");
        tags.forEach((key, tag) -> joiner.add("'" + key + "' : " + tag.toString().replace("\n", "\n\t")));
        return "CompoundTag '" + this.getName() + "' (" + tags.size() + " entries) {\n\t" + joiner + "\n}";
    }

    @Override
    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (Tag tag : tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    public int size() {
        return tags.size();
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public void clear() {
        tags.clear();
    }

    public CompoundTag copy() {
        CompoundTag tag = new CompoundTag(getName(), tags.size());
        for (Entry<String, Tag> entry : tags.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().copy());
        }
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            CompoundTag o = (CompoundTag) obj;
            return tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }

    public boolean equalsTags(CompoundTag o) {
        if (o == null) {
            return false;
        }
        return tags.entrySet().equals(o.tags.entrySet());
    }

    /**
     * Check existence of NBT tag
     *
     * @param name - NBT tag Id.
     * @return - true, if tag exists
     */
    public boolean exist(String name) {
        return contains(name);
    }

    @Override
    public CompoundTag clone() {
        CompoundTag nbt = new CompoundTag(this.tags.size());
        this.tags.forEach((key, value) -> nbt.put(key, value.copy()));
        return nbt;
    }
}
