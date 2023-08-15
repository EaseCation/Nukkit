package cn.nukkit.utils;

import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.RecipeType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.InputInteractionModel;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BinaryStream {

    public int offset;
    private byte[] buffer;
    private int count;

    protected BinaryStreamHelper helper;
    public boolean neteaseMode = false;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(int initialCapacity) {
        this.buffer = new byte[initialCapacity];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public void setHelper(BinaryStreamHelper helper) {
        this.helper = helper;
    }

    public void reuse() {
        this.offset = 0;
        this.count = 0;
    }

    public void reset() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public final void superReset() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer == null ? -1 : buffer.length;
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getBuffer() {
        return Arrays.copyOf(buffer, count);
    }

    public byte[] getBuffer(int from) {
        return Arrays.copyOfRange(buffer, from, count);
    }

    public byte[] getBuffer(int from, int to) {
        return Arrays.copyOfRange(buffer, from, to);
    }

    public byte[] getBufferUnsafe() {
        return buffer;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public byte[] get() {
        return this.get(this.count - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return new byte[0];
        }
        len = Math.min(len, this.count - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void skip(int len) {
        if (len <= 0) {
            return;
        }
        this.offset += Math.min(len, this.count - this.offset);
    }

    public void put(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }

        this.ensureCapacity(this.count + bytes.length);

        System.arraycopy(bytes, 0, this.buffer, this.count, bytes.length);
        this.count += bytes.length;
    }

    public void put(byte[] bytes, int length) {
        if (bytes == null) {
            return;
        }
        length = Math.min(bytes.length, length);
        if (length <= 0) {
            return;
        }

        this.ensureCapacity(this.count + length);

        System.arraycopy(bytes, 0, this.buffer, this.count, length);
        this.count += length;
    }

    /**
     * 仅用于很小的空数组. 例如1个字节的空数组.
     */
    public void putEmptyBytes(int length) {
        if (length <= 0) {
            return;
        }
        int newLength = this.count + length;
        this.ensureCapacity(newLength);

        for (int i = this.count; i < newLength; i++) {
            this.buffer[i] = 0;
        }
        this.count = newLength;
    }

    private int prepareWriterIndex(int length) {
        this.ensureCapacity(this.count + length);
        int writerIndex = this.count;
        this.count += length;
        return writerIndex;
    }

    public long getLong() {
        return Binary.readLong(this.get(8));
    }

    public void putLong(long l) {
        int index = this.prepareWriterIndex(8);
        Binary.writeLong(l, this.buffer, index);
    }

    public int getInt() {
        return Binary.readInt(this.get(4));
    }

    public void putInt(int i) {
        int index = this.prepareWriterIndex(4);
        Binary.writeInt(i, this.buffer, index);
    }

    public long getLLong() {
        return Binary.readLLong(this.get(8));
    }

    public void putLLong(long l) {
        int index = this.prepareWriterIndex(8);
        Binary.writeLLong(l, this.buffer, index);
    }

    public int getLInt() {
        return Binary.readLInt(this.get(4));
    }

    public void putLInt(int i) {
        int index = this.prepareWriterIndex(4);
        Binary.writeLInt(i, this.buffer, index);
    }

    public int getShort() {
        return Binary.readShort(this.get(2));
    }

    public void putShort(int s) {
        int index = this.prepareWriterIndex(2);
        Binary.writeShort(s, this.buffer, index);
    }

    public int getLShort() {
        return Binary.readLShort(this.get(2));
    }

    public void putLShort(int s) {
        int index = this.prepareWriterIndex(2);
        Binary.writeLShort(s, this.buffer, index);
    }

    public float getFloat() {
        return Binary.readFloat(this.get(4));
    }

    public void putFloat(float v) {
        int index = this.prepareWriterIndex(4);
        Binary.writeFloat(v, this.buffer, index);
    }

    public float getLFloat() {
        return Binary.readLFloat(this.get(4));
    }

    public void putLFloat(float v) {
        int index = this.prepareWriterIndex(4);
        Binary.writeLFloat(v, this.buffer, index);
    }

    public double getDouble() {
        return Binary.readDouble(this.get(8));
    }

    public void putDouble(double v) {
        int index = this.prepareWriterIndex(8);
        Binary.writeDouble(v, this.buffer, index);
    }

    public double getLDouble() {
        return Binary.readLDouble(this.get(8));
    }

    public void putLDouble(double v) {
        int index = this.prepareWriterIndex(8);
        Binary.writeLDouble(v, this.buffer, index);
    }

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    public void putTriad(int triad) {
        int index = this.prepareWriterIndex(3);
        Binary.writeTriad(triad, this.buffer, index);
    }

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    public void putLTriad(int triad) {
        int index = this.prepareWriterIndex(3);
        Binary.writeLTriad(triad, this.buffer, index);
    }

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }

    public byte getSingedByte() {
        return this.buffer[this.offset++];
    }

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
    }

    public void putByte(byte b) {
        this.ensureCapacity(this.count + 1);
        this.buffer[this.count++] = b;
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    public Attribute[] getAttributeList() throws Exception {
        List<Attribute> list = new ObjectArrayList<>();
        int count = (int) this.getUnsignedVarInt();

        for (int i = 0; i < count; ++i) {
            String name = this.getString();
            Attribute attr = Attribute.getAttributeByName(name);
            if (attr != null) {
                attr.setMinValue(this.getLFloat());
                attr.setValue(this.getLFloat());
                attr.setMaxValue(this.getLFloat());
                list.add(attr);
            } else {
                throw new Exception("Unknown attribute type \"" + name + "\"");
            }
        }

        return list.toArray(new Attribute[0]);
    }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    public void putAttributeList(Attribute[] attributes) {
        this.putUnsignedVarInt(attributes.length);
        for (Attribute attribute : attributes) {
            this.putString(attribute.getName());
            this.putLFloat(attribute.getMinValue());
            this.putLFloat(attribute.getValue());
            this.putLFloat(attribute.getMaxValue());
        }
    }

    public void putUUID(UUID uuid) {
        this.put(Binary.writeUUID(uuid));
    }

    public UUID getUUID() {
        return Binary.readUUID(this.get(16));
    }

    public void putSkinLegacy(Skin skin) {
        this.putString(skin.getSkinId());
        if (skin.isValid()) {
            this.putByteArray(skin.getSkinData().data);
        } else {
            this.putByteArray(Skin.FULL_WHITE_SKIN);
        }
        this.putByteArray(skin.getCapeData().data);
        this.putString(skin.getGeometryName());
        this.putString(skin.getGeometryData());
    }

    public Skin getSkinLegacy() {
        Skin skin = new Skin();
        skin.setSkinId(this.getString());
        skin.setSkinData(this.getByteArray());
        skin.setCapeData(this.getByteArray());
        skin.setGeometryName(this.getString());
        skin.setGeometryData(this.getString());
        return skin;
    }

    public void putSkin(Skin skin) {
        if (helper != null) helper.putSkin(this, skin);
        else this.putSkinV2(skin);
    }

    public void putSkinV2(Skin skin) {
        this.putString(skin.getSkinId());
        this.putString(skin.getSkinResourcePatch());
        this.putImage(skin.getSkinData());

        List<SkinAnimation> animations = skin.getAnimations();
        this.putLInt(animations.size());
        for (SkinAnimation animation : animations) {
            this.putImage(animation.image);
            this.putLInt(animation.type);
            this.putLFloat(animation.frames);
        }

        this.putImage(skin.getCapeData());
        this.putString(skin.getGeometryData());
        this.putString(skin.getAnimationData());
        this.putBoolean(skin.isPremium());
        this.putBoolean(skin.isPersona());
        this.putBoolean(skin.isCapeOnClassic());
        this.putString(skin.getCapeId());
        this.putString(skin.getFullSkinId());
    }

    public Skin getSkin() {
        if (helper != null) return helper.getSkin(this);
        else return this.getSkinV2();
    }

    public Skin getSkinV2() {
        Skin skin = new Skin();
        skin.setSkinId(this.getString());
        skin.setSkinResourcePatch(this.getString());
        skin.setSkinData(this.getImage());

        int animationCount = this.getLInt();
        for (int i = 0; i < animationCount; i++) {
            SerializedImage image = this.getImage();
            int type = this.getLInt();
            float frames = this.getLFloat();
            skin.getAnimations().add(new SkinAnimation(image, type, frames));
        }

        skin.setCapeData(this.getImage());
        skin.setGeometryData(this.getString());
        skin.setAnimationData(this.getString());
        skin.setPremium(this.getBoolean());
        skin.setPersona(this.getBoolean());
        skin.setCapeOnClassic(this.getBoolean());
        skin.setCapeId(this.getString());
        skin.setFullSkinId(this.getString());
        return skin;
    }

    public void putImage(SerializedImage image) {
        this.putLInt(image.width);
        this.putLInt(image.height);
        this.putByteArray(image.data);
    }

    public SerializedImage getImage() {
        int width = this.getLInt();
        int height = this.getLInt();
        byte[] data = this.getByteArray();
        return new SerializedImage(width, height, data);
    }

    public Item getSlot() {
        if (this.helper != null) {
            return this.helper.getSlot(this);
        }

        int id = this.getVarInt();
        if (id == ItemID.AIR) {
            return Item.get(ItemID.AIR, 0, 0);
        }

        if (id < Short.MIN_VALUE || id >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item ID received: " + id);
        }

        int auxValue = this.getVarInt();
        int data = auxValue >> 8;
        if (data == Short.MAX_VALUE) {
            data = -1;
        }
        int cnt = auxValue & 0xff;

        int nbtLen = this.getLShort();
        byte[] nbt = new byte[0];
        if (nbtLen < Short.MAX_VALUE) {
            nbt = this.get(nbtLen);
        } else if (nbtLen == 65535) {
            int nbtTagCount = (int) getUnsignedVarInt();
            int offset = getOffset();
            FastByteArrayInputStream stream = new FastByteArrayInputStream(get());
            for (int i = 0; i < nbtTagCount; i++) {
                try {
                    // TODO: 05/02/2019 This hack is necessary because we keep the raw NBT tag. Try to remove it.
                    CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
                    // tool damage hack
                    if (tag.contains("Damage")) {
                        data = tag.getInt("Damage");
                        tag.remove("Damage");
                    }
                    if (tag.contains("__DamageConflict__")) {
                        tag.put("Damage", tag.removeAndGet("__DamageConflict__"));
                    }
                    if (!tag.isEmpty()) {
                        nbt = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, false);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            setOffset(offset + (int) stream.position());
        }

        if (data < 0 || data >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item meta received: " + data);
        }

        ListTag<StringTag> canPlace;
        ListTag<StringTag> canBreak;

        int canPlaceCount = this.getVarInt();
        if (canPlaceCount > 0) {
            if (canPlaceCount > 4096) {
                throw new IndexOutOfBoundsException("Too many CanPlaceOn blocks");
            }
            canPlace = new ListTag<>("CanPlaceOn");
            for (int i = 0; i < canPlaceCount; i++) {
                canPlace.add(new StringTag("", this.getString()));
            }
        } else {
            canPlace = null;
        }

        int canBreakCount = this.getVarInt();
        if (canBreakCount > 0) {
            if (canBreakCount > 4096) {
                throw new IndexOutOfBoundsException("Too many CanDestroy blocks");
            }
            canBreak = new ListTag<>("CanDestroy");
            for (int i = 0; i < canBreakCount; i++) {
                canBreak.add(new StringTag("", this.getString()));
            }
        } else {
            canBreak = null;
        }

        Item item = Item.get(
                id, data, cnt, nbt
        );

        if (canPlace != null || canBreak != null) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canPlace != null) {
                namedTag.putList(canPlace);
            }
            if (canBreak != null) {
                namedTag.putList(canBreak);
            }

            item.setNamedTag(namedTag);
        }

        if (item.getId() == ItemID.SHIELD) { // TODO: Shields
            this.getVarLong();
        }

        return item;
    }

    public void putSlot(Item item) {
        if (this.helper != null) {
            this.helper.putSlot(this, item);
            return;
        }

        if (item == null || item.getId() == ItemID.AIR) {
            this.putVarInt(ItemID.AIR);
            return;
        }

        boolean isDurable = item instanceof ItemDurable;

        this.putVarInt(item.getId());

        int auxValue = item.getCount();
        if (!isDurable) {
            auxValue |= (((item.hasMeta() ? item.getDamage() : -1) & 0x7fff) << 8);
        }
        this.putVarInt(auxValue);

        if (item.hasCompoundTag() || isDurable) {
            try {
                // hack for tool damage
                byte[] nbt = item.getCompoundTag();
                CompoundTag tag;
                if (nbt == null || nbt.length == 0) {
                    tag = new CompoundTag();
                } else {
                    tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN, false);
                }
                if (tag.contains("Damage")) {
                    tag.put("__DamageConflict__", tag.removeAndGet("Damage"));
                }
                if (isDurable) {
                    tag.putInt("Damage", item.getDamage());
                }

                this.putLShort(0xffff);
                this.putByte((byte) 1);
                this.put(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.putLShort(0);
        }

        List<String> canPlaceOn = extractStringList(item, "CanPlaceOn");
        List<String> canDestroy = extractStringList(item, "CanDestroy");
        this.putVarInt(canPlaceOn.size());
        for (String block : canPlaceOn) {
            this.putString(block);
        }
        this.putVarInt(canDestroy.size());
        for (String block : canDestroy) {
            this.putString(block);
        }

        if (item.getId() == ItemID.SHIELD) { // TODO: Shields
            this.putVarLong(0);
        }
    }

    public Item getSlotLegacy() {
        int id = this.getVarInt();
        if (id == ItemID.AIR) {
            return Item.get(ItemID.AIR, 0, 0);
        }

        if (id < Short.MIN_VALUE || id >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item ID received: " + id);
        }

        int auxValue = this.getVarInt();
        int data = auxValue >> 8;
        if (data == Short.MAX_VALUE) {
            data = -1;
        }
        int cnt = auxValue & 0xff;

        if (data < 0 || data >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item meta received: " + data);
        }

        int nbtLen = this.getLShort();
        byte[] nbt = new byte[0];
        if (nbtLen > 0) {
            nbt = this.get(nbtLen);
        }

        ListTag<StringTag> canPlace;
        ListTag<StringTag> canBreak;

        int canPlaceCount = this.getVarInt();
        if (canPlaceCount > 0) {
            if (canPlaceCount > 4096) {
                throw new IndexOutOfBoundsException("Too many CanPlaceOn blocks");
            }
            canPlace = new ListTag<>("CanPlaceOn");
            for (int i = 0; i < canPlaceCount; i++) {
                canPlace.add(new StringTag("", this.getString()));
            }
        } else {
            canPlace = null;
        }

        int canBreakCount = this.getVarInt();
        if (canBreakCount > 0) {
            if (canBreakCount > 4096) {
                throw new IndexOutOfBoundsException("Too many CanDestroy blocks");
            }
            canBreak = new ListTag<>("CanDestroy");
            for (int i = 0; i < canBreakCount; i++) {
                canBreak.add(new StringTag("", this.getString()));
            }
        } else {
            canBreak = null;
        }

        Item item = Item.get(
                id, data, cnt, nbt
        );

        if (canPlace != null || canBreak != null) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canPlace != null) {
                namedTag.putList(canPlace);
            }
            if (canBreak != null) {
                namedTag.putList(canBreak);
            }

            item.setNamedTag(namedTag);
        }

        return item;
    }

    /**
     * 适用于 1.9 以下版本. 兼容 1.9 以下的版本要修改很多地方所以先不改了.
     */
    public void putSlotLegacy(Item item) {
        if (item == null || item.getId() == ItemID.AIR) {
            this.putVarInt(ItemID.AIR);
            return;
        }

        this.putVarInt(item.getId());

        int auxValue = (((item.hasMeta() ? item.getDamage() : -1) & 0x7fff) << 8) | item.getCount();
        this.putVarInt(auxValue);

        byte[] nbt = item.getCompoundTag();
        this.putLShort(nbt.length);
        this.put(nbt);

        List<String> canPlaceOn = extractStringList(item, "CanPlaceOn");
        List<String> canDestroy = extractStringList(item, "CanDestroy");
        this.putVarInt(canPlaceOn.size());
        for (String block : canPlaceOn) {
            this.putString(block);
        }
        this.putVarInt(canDestroy.size());
        for (String block : canDestroy) {
            this.putString(block);
        }
    }

    /**
     * 用于没有 NetID 的物品实例 (Creative Content and Recipe Book).
     */
    public Item getItemInstance() {
        if (this.helper != null) {
            return this.helper.getItemInstance(this);
        }

        return this.getSlot();
    }

    /**
     * 用于没有 NetID 的物品实例 (Creative Content and Recipe Book).
     */
    public void putItemInstance(Item item) {
        if (this.helper != null) {
            this.helper.putItemInstance(this, item);
            return;
        }

        this.putSlot(item);
    }

    public Item getCraftingRecipeIngredient() {
        if (this.helper != null) {
            return this.helper.getCraftingRecipeIngredient(this);
        }

        int id = this.getVarInt();
        if (id == ItemID.AIR) {
            return Item.get(ItemID.AIR, 0, 0);
        }

        if (id < Short.MIN_VALUE || id >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item ID received: " + id);
        }

        int damage = this.getVarInt();
        if (damage == 0x7fff) {
            damage = -1;
        }

        if (damage < Short.MIN_VALUE || damage >= Short.MAX_VALUE) {
            throw new RuntimeException("Invalid item meta received: " + id);
        }

        int count = this.getVarInt();
        return Item.get(id, damage, count);
    }

    public void putCraftingRecipeIngredient(Item ingredient) {
        if (this.helper != null) {
            this.helper.putCraftingRecipeIngredient(this, ingredient);
            return;
        }

        if (ingredient == null || ingredient.getId() == ItemID.AIR) {
            this.putVarInt(ItemID.AIR);
            return;
        }

        this.putVarInt(ingredient.getId());
        int damage;
        if (ingredient.hasMeta()) {
            damage = ingredient.getDamage();
        } else {
            damage = 0x7fff;
        }
        this.putVarInt(damage);
        this.putVarInt(ingredient.getCount());
    }

    private List<String> extractStringList(Item item, String tagName) {
        CompoundTag namedTag = item.getNamedTag();
        if (namedTag == null) {
            return Collections.emptyList();
        }

        Tag tag = namedTag.get(tagName);
        if (!(tag instanceof ListTag)) {
            return Collections.emptyList();
        }
        ListTag<? extends Tag> listTag = (ListTag<? extends Tag>) tag;

        int size = listTag.size();
        List<String> values = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Tag nbt = listTag.get(i);
            if (nbt instanceof StringTag) {
                values.add(((StringTag) nbt).data);
            }
        }

        return values;
    }

    public byte[] getByteArray() {
        int len = (int) this.getUnsignedVarInt();
        if (!isReadable(len)) {
            throw new IndexOutOfBoundsException("array length mismatch");
        }
        return this.get(len);
    }

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }

    public byte[] getLByteArray() {
        int len = this.getLInt();
        if (!isReadable(len)) {
            throw new IndexOutOfBoundsException("array length mismatch");
        }
        return this.get(len);
    }

    public void putLByteArray(byte[] b) {
        this.putLInt(b.length);
        this.put(b);
    }

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putByteArray(b);
    }

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }

    public void putVarInt(int v) {
        VarInt.writeVarInt(this, v);
    }

    public long getVarLong() {
        return VarInt.readVarLong(this);
    }

    public void putVarLong(long v) {
        VarInt.writeVarLong(this, v);
    }

    public long getUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }

    public void putUnsignedVarLong(long v) {
        VarInt.writeUnsignedVarLong(this, v);
    }

    public BlockVector3 getBlockVector3() {
        return new BlockVector3(this.getVarInt(), (int) this.getUnsignedVarInt(), this.getVarInt());
    }

    public BlockVector3 getSignedBlockPosition() {
        return new BlockVector3(getVarInt(), getVarInt(), getVarInt());
    }

    public void putSignedBlockPosition(BlockVector3 v) {
        putVarInt(v.x);
        putVarInt(v.y);
        putVarInt(v.z);
    }

    public void putSignedBlockPosition(int x, int y, int z) {
        this.putVarInt(x);
        this.putVarInt(y);
        this.putVarInt(z);
    }

    public void putBlockVector3(BlockVector3 v) {
        this.putBlockVector3(v.x, v.y, v.z);
    }

    public void putBlockVector3(int x, int y, int z) {
        this.putVarInt(x);
        this.putUnsignedVarInt(y);
        this.putVarInt(z);
    }

    public Vector3f getVector3f() {
        return new Vector3f(this.getLFloat(), this.getLFloat(), this.getLFloat());
    }

    public void putVector3f(Vector3f v) {
        this.putVector3f(v.x, v.y, v.z);
    }

    public void putVector3f(float x, float y, float z) {
        this.putLFloat(x);
        this.putLFloat(y);
        this.putLFloat(z);
    }

    public void putGameRules(GameRules gameRules) {
        if (this.helper != null) {
            this.helper.putGameRules(this, gameRules);
            return;
        }

        if (gameRules == null) {
            this.putUnsignedVarInt(0);
            return;
        }

        Map<GameRule, GameRules.Value> rules = gameRules.getGameRules();
        this.putUnsignedVarInt(rules.size());
        rules.forEach((gameRule, value) -> {
            putString(gameRule.getBedrockName());
            value.write(this);
        });
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public long getEntityUniqueId() {
        return this.getVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityUniqueId(long eid) {
        this.putVarLong(eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public long getEntityRuntimeId() {
        return this.getUnsignedVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityRuntimeId(long eid) {
        this.putUnsignedVarLong(eid);
    }

    @Nullable
    public BlockFace getBlockFace() {
        int face = this.getVarInt();
        return face != 255 ? BlockFace.fromIndex(face) : null;
    }

    public void putBlockFace(@Nullable BlockFace face) {
        this.putVarInt(face != null ? face.getIndex() : 255);
    }

    public void putEntityLink(EntityLink link) {
        if (this.helper != null) {
            this.helper.putEntityLink(this, link);
            return;
        }

        putEntityUniqueId(link.fromEntityUniquieId);
        putEntityUniqueId(link.toEntityUniquieId);
        putByte(link.type);
        putBoolean(link.immediate);
    }

    public void putEntityMetadata(EntityMetadata metadata) {
        Binary.writeMetadata(metadata, this);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getArray(Class<T> clazz, Function<BinaryStream, T> function) {
        int count = (int) getUnsignedVarInt();
        if (count > 4096) {
            throw new IndexOutOfBoundsException("too many array elements");
        }

        ArrayDeque<T> deque = new ArrayDeque<>();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArrayLInt(Class<T> clazz, Function<BinaryStream, T> function) {
        int count = this.getLInt();
        ArrayDeque<T> deque = new ArrayDeque<>();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArrayLShort(Class<T> clazz, Function<BinaryStream, T> function) {
        int count = this.getLShort();
        ArrayDeque<T> deque = new ArrayDeque<>();
        for (int i = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(int length, Class<T> clazz, Function<BinaryStream, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        for (int i = 0; i < length; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public boolean isReadable(int length) {
        return count - offset >= length;
    }

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;

        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    public static class BinaryStreamHelper {

        private static final BinaryStreamHelper INSTANCE = new BinaryStreamHelper();

        public static BinaryStreamHelper getInstance() {
            return INSTANCE;
        }

        private final Map<CommandParamType, Integer> commandParameterType2Id = new EnumMap<>(CommandParamType.class);

        protected BinaryStreamHelper() {
            this.registerCommandParameterTypes();
        }

        protected void registerCommandParameterTypes() {
            //TODO: 1.13前的重构
        }

        protected final void registerCommandParameterType(CommandParamType type, int id) {
            this.commandParameterType2Id.put(type, id);
        }

        public Enum<? extends Enum<?>> getProtocol() {
            return null;
        }

        public boolean isMicrosoft() {
            return true;
        }

        public boolean isNetEase() {
            return false;
        }

        /**
         * 用于1.13+. (e.g. "1.14.60")
         * @return network game version
         */
        public String getGameVersion() {
            return "1.12.0";
        }

        public void putSkin(BinaryStream stream, Skin skin) {
            stream.putSkinV2(skin);
        }

        public Skin getSkin(BinaryStream stream) {
            return stream.getSkinV2();
        }

        public void putGameRules(BinaryStream stream, GameRules gameRules) {
            if (gameRules == null) {
                stream.putUnsignedVarInt(0);
                return;
            }

            Map<GameRule, GameRules.Value> rules = gameRules.getGameRules();
            stream.putUnsignedVarInt(rules.size());
            rules.forEach((gameRule, value) -> {
                stream.putString(gameRule.getBedrockName());
                value.write(stream);
            });
        }

        public Item getSlot(BinaryStream stream) {
            return stream.getSlotLegacy();
        }

        public void putSlot(BinaryStream stream, Item item) {
            stream.putSlotLegacy(item);
        }

        public Item getItemInstance(BinaryStream stream) {
            return this.getSlot(stream);
        }

        public void putItemInstance(BinaryStream stream, Item item) {
            this.putSlot(stream, item);
        }

        public Item getCraftingRecipeIngredient(BinaryStream stream) {
            int id = stream.getVarInt();
            if (id == ItemID.AIR) {
                return Item.get(ItemID.AIR, 0, 0);
            }

            if (id < Short.MIN_VALUE || id >= Short.MAX_VALUE) {
                throw new RuntimeException("Invalid item ID received: " + id);
            }

            int damage = stream.getVarInt();
            if (damage == 0x7fff) {
                damage = -1;
            }

            if (damage < Short.MIN_VALUE || damage >= Short.MAX_VALUE) {
                throw new RuntimeException("Invalid item meta received: " + id);
            }

            int count = stream.getVarInt();
            return Item.get(id, damage, count);
        }

        public void putCraftingRecipeIngredient(BinaryStream stream, Item ingredient) {
            if (ingredient == null || ingredient.getId() == ItemID.AIR) {
                stream.putVarInt(ItemID.AIR);
                return;
            }

            stream.putVarInt(ingredient.getId());
            int damage;
            if (ingredient.hasMeta()) {
                damage = ingredient.getDamage();
            } else {
                damage = 0x7fff;
            }
            stream.putVarInt(damage);
            stream.putVarInt(ingredient.getCount());
        }

        public void putFurnaceRecipeIngredient(BinaryStream stream, Item ingredient, RecipeType type) {
            stream.putVarInt(ingredient.getId());

            if (type == RecipeType.FURNACE_DATA) {
                stream.putVarInt(ingredient.getDamage());
            }
        }

        /**
         * @since 1.16.0
         */
        public void putBrewingRecipeItem(BinaryStream stream, Item item) {
            stream.putVarInt(item.getId());
            stream.putVarInt(item.getDamage());
        }

        /**
         * @since 1.17.30
         */
        public void putMaterialReducerRecipeIngredient(BinaryStream stream, Item ingredient) {
            if (ingredient == null || ingredient.getId() == ItemID.AIR) {
                stream.putVarInt(ItemID.AIR);
                return;
            }

            stream.putVarInt((ingredient.getId() << 16) | ingredient.getDamage());
        }

        public void putEntityLink(BinaryStream stream, EntityLink link) {
            stream.putEntityUniqueId(link.fromEntityUniquieId);
            stream.putEntityUniqueId(link.toEntityUniquieId);
            stream.putByte(link.type);
            stream.putBoolean(link.immediate);
        }

        public void putCommandData(BinaryStream stream, Map<String, CommandDataVersions> commands, List<CommandEnum> enums, List<String> postFixes, List<CommandEnum> softEnums) {
            //TODO: 1.13前的重构
            stream.putUnsignedVarInt(0);
        }

        public int getItemNetworkId(BinaryStream stream, Item item) {
            return RuntimeItems.getNetworkId(RuntimeItems.getNetworkFullId(item));
        }

        public InputInteractionModel getInteractionModel(BinaryStream stream) {
            return null;
        }

        /**
         * @since 1.16.0
         */
        public int getItemStackRequest(BinaryStream stream) { //TODO: server authoritative inventory
            return -1;
        }

        public final int getCommandParameterTypeId(CommandParamType type) {
            return this.getCommandParameterTypeId(type, type.getId());
        }

        public final int getCommandParameterTypeId(CommandParamType type, int defaultValue) {
            return this.commandParameterType2Id.getOrDefault(type, defaultValue);
        }

        protected final List<String> extractStringList(BinaryStream stream, Item item, String tagName) {
            return stream.extractStringList(item, tagName);
        }
    }
}
