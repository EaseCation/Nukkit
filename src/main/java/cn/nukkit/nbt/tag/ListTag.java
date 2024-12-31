package cn.nukkit.nbt.tag;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ListTag<T extends Tag> extends Tag implements Iterable<T> {

    private final List<T> list;

    public byte type;

    public ListTag() {
        this("");
    }

    public ListTag(String name) {
        super(name);
        this.list = new ObjectArrayList<>();
    }

    public ListTag(int initialCapacity) {
        this("", initialCapacity);
    }

    public ListTag(String name, int initialCapacity) {
        super(name);
        this.list = new ObjectArrayList<>(initialCapacity);
    }

    public ListTag(List<T> list) {
        this("", list);
    }

    public ListTag(String name, List<T> list) {
        super(name);
        this.list = list;

        if (!list.isEmpty()) {
            type = list.get(0).getId();
        }
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        if (!list.isEmpty()) {
            type = list.get(0).getId();
        } else {
            type = TAG_Byte;
        }

        dos.writeByte(type);
        dos.writeInt(list.size());
        for (T tag : list) {
            tag.write(dos);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    void load(NBTInputStream dis) throws IOException {
        type = dis.readByte();
        int size = dis.readInt();

        list.clear();
        for (int i = 0; i < size; i++) {
            Tag tag = Tag.newTag(type, null);
            tag.load(dis);
            tag.setName("");
            list.add((T) tag);
        }
    }

    @Override
    public byte getId() {
        return TAG_List;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n\t");
        list.forEach(tag -> joiner.add(tag.toString().replace("\n", "\n\t")));
        return "ListTag '" + this.getName() + "' (" + list.size() + " entries of type " + Tag.getTagName(type) + ") {\n\t" + joiner + "\n}";
    }

    @Override
    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);

        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (T aList : list) aList.print(prefix, out);
        out.println(orgPrefix + "}");
    }

    public ListTag<T> add(T tag) {
        type = tag.getId();
        tag.setName("");
        list.add(tag);
        return this;
    }

    public ListTag<T> add(int index, T tag) {
        type = tag.getId();
        tag.setName("");
        setOrAdd(index, tag);
        return this;
    }

    private void setOrAdd(int index, T tag) {
        if (index >= list.size()) {
            list.add(index, tag);
        } else {
            list.set(index, tag);
        }
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addByte(int data) {
        type = TAG_Byte;
        list.add((T) new ByteTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addByte(int index, int data) {
        type = TAG_Byte;
        setOrAdd(index, (T) new ByteTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addShort(int data) {
        type = TAG_Short;
        list.add((T) new ShortTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addShort(int index, int data) {
        type = TAG_Short;
        setOrAdd(index, (T) new ShortTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addInt(int data) {
        type = TAG_Int;
        list.add((T) new IntTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addInt(int index, int data) {
        type = TAG_Int;
        setOrAdd(index, (T) new IntTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addLong(long data) {
        type = TAG_Long;
        list.add((T) new LongTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addLong(int index, long data) {
        type = TAG_Long;
        setOrAdd(index, (T) new LongTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addFloat(float data) {
        type = TAG_Float;
        list.add((T) new FloatTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addFloat(int index, float data) {
        type = TAG_Float;
        setOrAdd(index, (T) new FloatTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addDouble(double data) {
        type = TAG_Double;
        list.add((T) new DoubleTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addDouble(int index, double data) {
        type = TAG_Double;
        setOrAdd(index, (T) new DoubleTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addString(String data) {
        type = TAG_String;
        list.add((T) new StringTag("", data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addString(int index, String data) {
        type = TAG_String;
        setOrAdd(index, (T) new StringTag("", data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addByteArray(byte[] data) {
        type = TAG_Byte_Array;
        list.add((T) new ByteArrayTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addByteArray(int index, byte[] data) {
        type = TAG_Byte_Array;
        setOrAdd(index, (T) new ByteArrayTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addIntArray(int[] data) {
        type = TAG_Int_Array;
        list.add((T) new IntArrayTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addIntArray(int index, int[] data) {
        type = TAG_Int_Array;
        setOrAdd(index, (T) new IntArrayTag(data));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addList(ListTag<? extends Tag> data) {
        type = TAG_List;
        data.setName("");
        list.add((T) data);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addList(int index, ListTag<? extends Tag> data) {
        type = TAG_List;
        data.setName("");
        setOrAdd(index, (T) data);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addCompound(CompoundTag data) {
        type = TAG_Compound;
        data.setName("");
        list.add((T) data);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ListTag<T> addCompound(int index, CompoundTag data) {
        type = TAG_Compound;
        data.setName("");
        setOrAdd(index, (T) data);
        return this;
    }

    public ListTag<T> addBoolean(boolean data) {
        return addByte(data ? 1 : 0);
    }

    public ListTag<T> addBoolean(int index, boolean data) {
        return addByte(index, data ? 1 : 0);
    }

    @Override
    public List<Object> parseValue() {
        List<Object> value = new ObjectArrayList<>(this.list.size());

        for (T t : this.list) {
            value.add(t.parseValue());
        }

        return value;
    }

    public T get(int index) {
        return list.get(index);
    }

    public List<T> getAll() {
        return new ObjectArrayList<>(list);
    }

    public List<T> getAllUnsafe() {
        return list;
    }

    public boolean remove(T tag) {
        return list.remove(tag);
    }

    public T remove(int index) {
        return list.remove(index);
    }

    public boolean removeAll(Collection<T> tags) {
        return list.removeAll(tags);
    }

    public boolean removeAll(ListTag<T> o) {
        return list.removeAll(o.list);
    }

    public boolean removeIf(Predicate<T> filter) {
        return list.removeIf(filter);
    }

    public boolean retainAll(Collection<T> tags) {
        return list.retainAll(tags);
    }

    public boolean retainAll(ListTag<T> o) {
        return list.retainAll(o.list);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(T tag) {
        return list.contains(tag);
    }

    public boolean containsAll(Collection<T> tags) {
        return list.containsAll(tags);
    }

    public boolean containsAll(ListTag<T> o) {
        return list.containsAll(o.list);
    }

    public T[] toArray(T[] arr) {
        return list.toArray(arr);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    public Stream<T> stream() {
        return list.stream();
    }

    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public Tag copy() {
        ListTag<T> res = new ListTag<>(getName(), list.size());
        res.type = type;
        for (T t : list) {
            @SuppressWarnings("unchecked")
            T copy = (T) t.copy();
            res.list.add(copy);
        }
        return res;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ListTag o = (ListTag) obj;
            if (type == o.type) {
                return list.equals(o.list);
            }
        }
        return false;
    }

    public boolean equalsTags(ListTag<T> o) {
        if (o == null) {
            return false;
        }
        return list.equals(o.list);
    }

    @SuppressWarnings("unchecked")
    public Vector3 asDoublePos() {
        return Vector3.fromNbt((ListTag<DoubleTag>) this);
    }

    @SuppressWarnings("unchecked")
    public Vector3 asFloatPos() {
        return Vector3.fromNbtf((ListTag<FloatTag>) this);
    }

    @SuppressWarnings("unchecked")
    public BlockVector3 asBlockPos() {
        return BlockVector3.fromNbt((ListTag<IntTag>) this);
    }

    @Override
    public String toJson(boolean pretty) {
        return asJson(Tag::toJson, pretty);
    }

    @Override
    public String toMojangson(boolean pretty) {
        return asJson(Tag::toMojangson, pretty);
    }

    private String asJson(CompoundTag.Stringifier stringifier, boolean pretty) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        if (pretty) {
            builder.append('[').append('\n');
            for (; ; ) {
                builder.append(CompoundTag.indent(stringifier.stringify(iterator.next(), true)));
                if (!iterator.hasNext()) {
                    return builder.append('\n').append(']').toString();
                }
                builder.append(',').append('\n');
            }
        } else {
            builder.append('[');
            for (; ; ) {
                builder.append(stringifier.stringify(iterator.next(), false));
                if (!iterator.hasNext()) {
                    return builder.append(']').toString();
                }
                builder.append(',');
            }
        }
    }
}
