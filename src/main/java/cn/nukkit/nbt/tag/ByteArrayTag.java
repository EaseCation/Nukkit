package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.utils.Binary;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.IntFunction;

public class ByteArrayTag extends Tag {
    public byte[] data;

    public ByteArrayTag(String name) {
        super(name);
    }

    public ByteArrayTag(byte[] data) {
        super("");
        this.data = data;
    }

    public ByteArrayTag(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        if (data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(data.length);
        dos.write(data);
    }

    @Override
    void load(NBTInputStream dis, int maxDepth) throws IOException {
        int length = dis.readInt();
        data = new byte[length];
        dis.readFully(data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte getId() {
        return TAG_Byte_Array;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + this.getName() + " (data: 0x" + Binary.bytesToHexString(data, true) + " [" + data.length + " bytes])";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
            return ((data == null && byteArrayTag.data == null) || (data != null && Arrays.equals(data, byteArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ByteArrayTag(getName(), cp);
    }

    @Override
    public byte[] parseValue() {
        return this.data;
    }

    @Override
    public String toJson(boolean pretty) {
        return asJson(String::valueOf, pretty);
    }

    @Override
    public String toMojangson(boolean pretty) {
        return asJson(n -> n + "B", pretty);
    }

    private String asJson(IntFunction<String> elementStringifier, boolean pretty) {
        StringJoiner joiner = new StringJoiner(pretty ? ", " : ",");
        for (int n : data) {
            joiner.add(elementStringifier.apply(n));
        }
        return "[" + joiner + "]";
    }
}
