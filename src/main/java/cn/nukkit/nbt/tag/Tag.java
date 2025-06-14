package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.io.PrintStream;

public abstract class Tag {
    public static final byte TAG_End = 0;
    public static final byte TAG_Byte = 1;
    public static final byte TAG_Short = 2;
    public static final byte TAG_Int = 3;
    public static final byte TAG_Long = 4;
    public static final byte TAG_Float = 5;
    public static final byte TAG_Double = 6;
    public static final byte TAG_Byte_Array = 7;
    public static final byte TAG_String = 8;
    public static final byte TAG_List = 9;
    public static final byte TAG_Compound = 10;
    public static final byte TAG_Int_Array = 11;
    static final int TAG_TYPE_COUNT = 12;

    private String name;

    abstract void write(NBTOutputStream dos) throws IOException;

    abstract void load(NBTInputStream dis, int maxDepth) throws IOException;

    public abstract String toString();

    public abstract byte getId();

    protected Tag(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag o = (Tag) obj;
        return getId() == o.getId() && !(name == null && o.name != null || name != null && o.name == null) && !(name != null && !name.equals(o.name));
    }

    public void print(PrintStream out) {
        print("", out);
    }

    public void print(String prefix, PrintStream out) {
        String name = getName();

        out.print(prefix);
        out.print(getTagName(getId()));
        if (!name.isEmpty()) {
            out.print("(\"" + name + "\")");
        }
        out.print(": ");
        out.println(this);
    }

    public Tag setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        return this;
    }

    public String getName() {
        if (name == null) return "";
        return name;
    }

    public static Tag readNamedTag(NBTInputStream dis) throws IOException {
        return readNamedTag(dis, NBTIO.MAX_DEPTH);
    }

    public static Tag readNamedTag(NBTInputStream dis, int maxDepth) throws IOException {
        byte type = dis.readByte();
        if (type == TAG_End) {
            return EndTag.INSTANCE;
        }

        String name = dis.readUTF(1024);

        Tag tag = newTag(type, name);

        tag.load(dis, maxDepth);
        return tag;
    }

    public static void writeNamedTag(Tag tag, NBTOutputStream dos) throws IOException {
        writeNamedTag(tag, tag.getName(), dos);
    }

    public static void writeNamedTag(Tag tag, String name, NBTOutputStream dos) throws IOException {
        dos.writeByte(tag.getId());
        if (tag.getId() == Tag.TAG_End) return;

        dos.writeUTF(name);

        tag.write(dos);
    }

    public static Tag newTag(byte type, String name) {
        if (type < 1 || type >= TAG_TYPE_COUNT) {
            return EndTag.INSTANCE;
        }
        return Tags.REGISTRY[type].factory().apply(name);
    }

    public static String getTagName(byte type) {
        if (type < 0 || type >= TAG_TYPE_COUNT) {
            return "UNKNOWN";
        }
        return Tags.REGISTRY[type].name();
    }

    public abstract Tag copy();

    public abstract Object parseValue();

    public String toJson() {
        return toJson(false);
    }

    public abstract String toJson(boolean pretty);

    public String toMojangson() {
        return toMojangson(false);
    }

    public abstract String toMojangson(boolean pretty);
}
