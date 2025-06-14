package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

public class EndTag extends Tag {
    public static final EndTag INSTANCE = new EndTag();

    EndTag() {
        super(null);
    }

    @Override
    void load(NBTInputStream dis, int maxDepth) {
    }

    @Override
    void write(NBTOutputStream dos) {
    }

    @Override
    public byte getId() {
        return TAG_End;
    }

    @Override
    public String toString() {
        return "EndTag";
    }

    @Override
    public Tag copy() {
        return this;
    }

    @Override
    public Object parseValue() {
        return null;
    }

    @Override
    public String toJson(boolean pretty) {
        return "null";
    }

    @Override
    public String toMojangson(boolean pretty) {
        if (!pretty) {
            return "";
        }
        return "\n";
    }
}
