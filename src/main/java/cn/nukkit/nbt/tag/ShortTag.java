package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class ShortTag extends NumberTag<Integer> {
    public int data;

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    public ShortTag(String name) {
        super(name);
    }

    public ShortTag(int data) {
        super("");
        this.data = data;
    }

    public ShortTag(String name, int data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeShort(data);
    }

    @Override
    void load(NBTInputStream dis, int maxDepth) throws IOException {
        data = dis.readShort();
    }

    @Override
    public Integer parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Short;
    }

    @Override
    public String toString() {
        return "ShortTag " + this.getName() + " (data: " + data + ")";
    }

    @Override
    public Tag copy() {
        return new ShortTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ShortTag o = (ShortTag) obj;
            return data == o.data;
        }
        return false;
    }

    @Override
    public String toJson(boolean pretty) {
        return String.valueOf(data);
    }

    @Override
    public String toMojangson(boolean pretty) {
        return data + "s";
    }
}
