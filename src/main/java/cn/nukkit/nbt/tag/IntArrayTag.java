package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.IntFunction;

public class IntArrayTag extends Tag {
    public int[] data;

    public IntArrayTag(String name) {
        super(name);
    }

    public IntArrayTag(int[] data) {
        super("");
        this.data = data;
    }

    public IntArrayTag(String name, int[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(data.length);
        for (int aData : data) {
            dos.writeInt(aData);
        }
    }

    @Override
    void load(NBTInputStream dis, int maxDepth) throws IOException {
        int length = dis.readInt();
        data = new int[length];
        for (int i = 0; i < length; i++) {
            data[i] = dis.readInt();
        }
    }

    public int[] getData() {
        return data;
    }

    @Override
    public int[] parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Int_Array;
    }

    @Override
    public String toString() {
        return "IntArrayTag " + this.getName() + " [" + data.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntArrayTag intArrayTag = (IntArrayTag) obj;
            return ((data == null && intArrayTag.data == null) || (data != null && Arrays.equals(data, intArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        int[] cp = new int[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new IntArrayTag(getName(), cp);
    }

    @Override
    public String toJson(boolean pretty) {
        return asJson(String::valueOf, pretty);
    }

    @Override
    public String toMojangson(boolean pretty) {
        return asJson(n -> n + "I", pretty);
    }

    private String asJson(IntFunction<String> elementStringifier, boolean pretty) {
        StringJoiner joiner = new StringJoiner(pretty ? ", " : ",");
        for (int n : data) {
            joiner.add(elementStringifier.apply(n));
        }
        return "[" + joiner + "]";
    }
}
