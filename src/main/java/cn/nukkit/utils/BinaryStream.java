package cn.nukkit.utils;

import cn.nukkit.command.data.ChainedSubCommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.RecipeType;
import cn.nukkit.inventory.recipe.descriptor.DefaultItemDescriptor;
import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.item.*;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.math.*;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.*;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortCollection;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;

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
        if (!isReadable(len)) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void skip(int len) {
        if (!isReadable(len)) {
            throw new IndexOutOfBoundsException("Buffer length mismatch");
        }
        this.offset += len;
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

    public int getInt(int maxLength) {
        return checkLength(getInt(), maxLength);
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

    public int getLInt(int maxLength) {
        return checkLength(getLInt(), maxLength);
    }

    public void putLInt(int i) {
        int index = this.prepareWriterIndex(4);
        Binary.writeLInt(i, this.buffer, index);
    }

    public short getSignedShort() {
        return Binary.readSignedShort(this.get(2));
    }

    public int getShort() {
        return Binary.readShort(this.get(2));
    }

    public void putShort(int s) {
        int index = this.prepareWriterIndex(2);
        Binary.writeShort(s, this.buffer, index);
    }

    public short getLSignedShort() {
        return Binary.readSignedLShort(this.get(2));
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

    public byte getSignedByte() {
        return this.buffer[this.offset++];
    }

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
    }

    public void putByte(byte b) {
        this.ensureCapacity(this.count + 1);
        this.buffer[this.count++] = b;
    }

    public void putByte(int b) {
        putByte((byte) b);
    }

    public void putByte(int b, int maxLimit) {
        if (b > maxLimit) {
            throw new IllegalArgumentException("Value must be less than or equal to " + maxLimit + ", got " + b);
        }
        putByte(b);
    }

    public void putByte(int b, int maxLimit, int minLimit) {
        if (b > maxLimit) {
            throw new IllegalArgumentException("Value must be less than or equal to " + maxLimit + ", got " + b);
        }
        if (b < minLimit) {
            throw new IllegalArgumentException("Value must be greater than or equal to " + minLimit + ", got " + b);
        }
        putByte(b);
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    public Attribute[] getAttributeList() throws Exception {
        List<Attribute> list = new ObjectArrayList<>();
        int count = this.getUnsignedVarInt(4096);

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

        int animationCount = this.getLInt(4096);
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

    public Item getItemStack() {
        if (this.helper != null) {
            return this.helper.getItemStack(this);
        }
        return this.getSlot();
    }

    public void putItemStack(Item item) {
        if (this.helper != null) {
            this.helper.putItemStack(this, item);
            return;
        }
        this.putSlot(item);
    }

    public Item getSlot() {
        if (this.helper != null) {
            return this.helper.getSlot(this);
        }

        int id = this.getVarInt();
        if (id == ItemID.AIR) {
            return Items.air();
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
            int nbtTagCount = getUnsignedVarInt(4096);
            int offset = getOffset();
            FastByteArrayInputStream stream = new FastByteArrayInputStream(get());
            for (int i = 0; i < nbtTagCount; i++) {
                try {
                    // TODO: 05/02/2019 This hack is necessary because we keep the raw NBT tag. Try to remove it.
                    CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
                    // tool damage hack
                    if (id != Item.GLOW_STICK && id != Item.SPARKLER) {
                        if (tag.contains("Damage")) {
                            data = tag.getInt("Damage");
                            tag.remove("Damage");
                        }
                        Tag nkDamageTag = tag.removeAndGet("__DamageConflict__");
                        if (nkDamageTag != null) {
                            tag.put("Damage", nkDamageTag);
                        }
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

        Set<String> canPlace;
        int canPlaceCount = this.getVarInt(4096);
        if (canPlaceCount > 0) {
            canPlace = new ObjectOpenHashSet<>();
            for (int i = 0; i < canPlaceCount; i++) {
                canPlace.add(this.getString());
            }
        } else {
            canPlace = null;
        }

        Set<String> canBreak;
        int canBreakCount = this.getVarInt(4096);
        if (canBreakCount > 0) {
            canBreak = new ObjectOpenHashSet<>();
            for (int i = 0; i < canBreakCount; i++) {
                canBreak.add(this.getString());
            }
        } else {
            canBreak = null;
        }

        Item item = Item.get(
                id, data, cnt, nbt
        );

        if (canPlace != null && !canPlace.isEmpty()) {
            item.setCanPlaceOnBlocks(canPlace);
        }

        if (canBreak != null && !canBreak.isEmpty()) {
            item.setCanDestroyBlocks(canBreak);
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
                if (!item.keepDamageTag()) {
                    Tag damageTag = tag.removeAndGet("Damage");
                    if (damageTag != null) {
                        tag.put("__DamageConflict__", damageTag);
                    }
                    if (isDurable) {
                        tag.putInt("Damage", item.getDamage());
                    }
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

        Set<String> canPlaceOn = item.getCanPlaceOnBlocks();
        if (canPlaceOn != null) {
            this.putVarInt(canPlaceOn.size());
            for (String block : canPlaceOn) {
                this.putString(block);
            }
        } else {
            this.putVarInt(0);
        }

        Set<String> canDestroy = item.getCanDestroyBlocks();
        if (canDestroy != null) {
            this.putVarInt(canDestroy.size());
            for (String block : canDestroy) {
                this.putString(block);
            }
        } else {
            this.putVarInt(0);
        }

        if (item.getId() == ItemID.SHIELD) { // TODO: Shields
            this.putVarLong(0);
        }
    }

    public Item getSlotLegacy() {
        int id = this.getVarInt();
        if (id == ItemID.AIR) {
            return Items.air();
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

        Set<String> canPlace;
        int canPlaceCount = this.getVarInt(4096);
        if (canPlaceCount > 0) {
            canPlace = new ObjectOpenHashSet<>();
            for (int i = 0; i < canPlaceCount; i++) {
                canPlace.add(this.getString());
            }
        } else {
            canPlace = null;
        }

        Set<String> canBreak;
        int canBreakCount = this.getVarInt(4096);
        if (canBreakCount > 0) {
            canBreak = new ObjectOpenHashSet<>();
            for (int i = 0; i < canBreakCount; i++) {
                canBreak.add(this.getString());
            }
        } else {
            canBreak = null;
        }

        Item item = Item.get(
                id, data, cnt, nbt
        );

        if (canPlace != null && !canPlace.isEmpty()) {
            item.setCanPlaceOnBlocks(canPlace);
        }

        if (canBreak != null && !canBreak.isEmpty()) {
            item.setCanDestroyBlocks(canBreak);
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

        Set<String> canPlaceOn = item.getCanPlaceOnBlocks();
        if (canPlaceOn != null) {
            this.putVarInt(canPlaceOn.size());
            for (String block : canPlaceOn) {
                this.putString(block);
            }
        } else {
            this.putVarInt(0);
        }

        Set<String> canDestroy = item.getCanDestroyBlocks();
        if (canDestroy != null) {
            this.putVarInt(canDestroy.size());
            for (String block : canDestroy) {
                this.putString(block);
            }
        } else {
            this.putVarInt(0);
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

    public void putRecipeIngredient(RecipeIngredient ingredient) {
        if (this.helper != null) {
            this.helper.putRecipeIngredient(this, ingredient);
            return;
        }
        if (ingredient == null || ingredient.getDescriptor().getType() == ItemDescriptorType.NONE) {
            putVarInt(ItemID.AIR);
            return;
        }
        if (!(ingredient.getDescriptor() instanceof DefaultItemDescriptor descriptor)) {
            throw new UnsupportedOperationException("Unsupported recipe ingredient descriptor: " + ingredient.getDescriptor().getType());
        }
        Item item = descriptor.getItem();
        if (item.getId() == ItemID.AIR) {
            putVarInt(ItemID.AIR);
            return;
        }
        putVarInt(item.getId());
        putVarInt(item.hasMeta() ? item.getDamage() : 0x7fff);
        putVarInt(ingredient.getCount());
    }

    public byte[] getByteArray() {
        return this.getByteArray(-1, false);
    }

    public byte[] getByteArray(int maxLength) {
        return this.getByteArray(maxLength, false);
    }

    public byte[] getByteArray(boolean nonEmpty) {
        return this.getByteArray(-1, nonEmpty);
    }

    public byte[] getByteArray(int maxLength, boolean nonEmpty) {
        long length = this.getUnsignedVarInt();
        int limit = maxLength > -1 ? Math.min(maxLength, this.count - this.offset) : this.count - this.offset;
        int len = checkLength(length, limit);
        if (nonEmpty && len == 0) {
            throw new IndexOutOfBoundsException("array length must be greater than 0");
        }
        return this.get(len);
    }

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }

    public void putByteArray(byte[] b, int maxLength) {
        int len = b.length;
        if (maxLength > -1 && len > maxLength) {
            throw new IndexOutOfBoundsException("array length exceeds max limit: " + len + " > " + maxLength);
        }
        this.putByteArray(b);
    }

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }

    public String getString(int maxLength) {
        return new String(this.getByteArray(maxLength), StandardCharsets.UTF_8);
    }

    public String getString(boolean nonEmpty) {
        return new String(this.getByteArray(nonEmpty), StandardCharsets.UTF_8);
    }

    public String getString(int maxLength, boolean nonEmpty) {
        return new String(this.getByteArray(maxLength, nonEmpty), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        this.putString(string, -1, false);
    }

    public void putString(String string, int maxLength) {
        this.putString(string, maxLength, false);
    }

    public void putString(String string, boolean nonEmpty) {
        this.putString(string, -1, nonEmpty);
    }

    public void putString(String string, int maxLength, boolean nonEmpty) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        if (nonEmpty && b.length == 0) {
            throw new IllegalArgumentException("string must not be empty");
        }
        this.putByteArray(b, maxLength);
    }

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    public int getUnsignedVarInt(int maxLength) {
        return checkLength(getUnsignedVarInt(), maxLength);
    }

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }

    public int getVarInt(int maxLength) {
        return checkLength(getVarInt(), maxLength);
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
        if (this.helper != null) {
            return this.helper.getBlockVector3(this);
        }

        return new BlockVector3(this.getVarInt(), (int) this.getUnsignedVarInt(), this.getVarInt());
    }

    public BlockVector3 getSignedBlockPosition() {
        return new BlockVector3(getVarInt(), getVarInt(), getVarInt());
    }

    public void putSignedBlockPosition(BlockVector3 v) {
        this.putSignedBlockPosition(v.x, v.y, v.z);
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
        if (this.helper != null) {
            this.helper.putBlockVector3(this, x, y, z);
            return;
        }

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

    public Vector2f getVector2f() {
        return new Vector2f(this.getLFloat(), this.getLFloat());
    }

    public void putVector2f(Vector2f v) {
        this.putVector2f(v.x, v.y);
    }

    public void putVector2f(float x, float y) {
        this.putLFloat(x);
        this.putLFloat(y);
    }

    public void putGameRules(GameRules gameRules) {
        putGameRules(gameRules, true);
    }

    public void putGameRules(GameRules gameRules, boolean network) {
        if (this.helper != null) {
            this.helper.putGameRules(this, gameRules, network);
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
            value.write(this, network);
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
    public <T> T[] getArray(T[] array, Function<BinaryStream, T> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public <T> T[] getArray(T[] array, Function<BinaryStream, T> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public <T> T[] getArray(ToIntFunction<BinaryStream> lengthReader, T[] array, Function<BinaryStream, T> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public <T> T[] getArray(ToIntFunction<BinaryStream> lengthReader, T[] array, Function<BinaryStream, T> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public <T> T[] getArray(int length, T[] array, Function<BinaryStream, T> function) {
        return getArray(length, array, function, 4096);
    }

    public <T> T[] getArray(int length, T[] array, Function<BinaryStream, T> function, int maxLength) {
        checkLength(length, maxLength);
        Collection<T> collection = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            collection.add(function.apply(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public byte[] getArray(byte[] array, Object2ByteFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public byte[] getArray(byte[] array, Object2ByteFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public byte[] getArray(ToIntFunction<BinaryStream> lengthReader, byte[] array, Object2ByteFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public byte[] getArray(ToIntFunction<BinaryStream> lengthReader, byte[] array, Object2ByteFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public byte[] getArray(int length, byte[] array, Object2ByteFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public byte[] getArray(int length, byte[] array, Object2ByteFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        ByteCollection collection = new ByteArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.getByte(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public short[] getArray(short[] array, Object2ShortFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public short[] getArray(short[] array, Object2ShortFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public short[] getArray(ToIntFunction<BinaryStream> lengthReader, short[] array, Object2ShortFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public short[] getArray(ToIntFunction<BinaryStream> lengthReader, short[] array, Object2ShortFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public short[] getArray(int length, short[] array, Object2ShortFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public short[] getArray(int length, short[] array, Object2ShortFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        ShortCollection collection = new ShortArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.getShort(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public char[] getArray(char[] array, Object2CharFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public char[] getArray(char[] array, Object2CharFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public char[] getArray(ToIntFunction<BinaryStream> lengthReader, char[] array, Object2CharFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public char[] getArray(ToIntFunction<BinaryStream> lengthReader, char[] array, Object2CharFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public char[] getArray(int length, char[] array, Object2CharFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public char[] getArray(int length, char[] array, Object2CharFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        CharCollection collection = new CharArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.getChar(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public int[] getArray(int[] array, ToIntFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public int[] getArray(int[] array, ToIntFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public int[] getArray(ToIntFunction<BinaryStream> lengthReader, int[] array, ToIntFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public int[] getArray(ToIntFunction<BinaryStream> lengthReader, int[] array, ToIntFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public int[] getArray(int length, int[] array, ToIntFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public int[] getArray(int length, int[] array, ToIntFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        IntCollection collection = new IntArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.applyAsInt(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public long[] getArray(long[] array, ToLongFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public long[] getArray(long[] array, ToLongFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public long[] getArray(ToIntFunction<BinaryStream> lengthReader, long[] array, ToLongFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public long[] getArray(ToIntFunction<BinaryStream> lengthReader, long[] array, ToLongFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public long[] getArray(int length, long[] array, ToLongFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public long[] getArray(int length, long[] array, ToLongFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        LongCollection collection = new LongArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.applyAsLong(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public float[] getArray(float[] array, Object2FloatFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public float[] getArray(float[] array, Object2FloatFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public float[] getArray(ToIntFunction<BinaryStream> lengthReader, float[] array, Object2FloatFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public float[] getArray(ToIntFunction<BinaryStream> lengthReader, float[] array, Object2FloatFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public float[] getArray(int length, float[] array, Object2FloatFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public float[] getArray(int length, float[] array, Object2FloatFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        FloatCollection collection = new FloatArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.getFloat(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public double[] getArray(double[] array, ToDoubleFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public double[] getArray(double[] array, ToDoubleFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public double[] getArray(ToIntFunction<BinaryStream> lengthReader, double[] array, ToDoubleFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public double[] getArray(ToIntFunction<BinaryStream> lengthReader, double[] array, ToDoubleFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public double[] getArray(int length, double[] array, ToDoubleFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public double[] getArray(int length, double[] array, ToDoubleFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        DoubleCollection collection = new DoubleArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.applyAsDouble(this));
        }
        return collection.toArray(array);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public boolean[] getArray(boolean[] array, Object2BooleanFunction<BinaryStream> function) {
        return getArray(array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public boolean[] getArray(boolean[] array, Object2BooleanFunction<BinaryStream> function, int maxLength) {
        return getArray(getUnsignedVarInt(maxLength), array, function, maxLength);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than 4096
     */
    public boolean[] getArray(ToIntFunction<BinaryStream> lengthReader, boolean[] array, Object2BooleanFunction<BinaryStream> function) {
        return getArray(lengthReader, array, function, 4096);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public boolean[] getArray(ToIntFunction<BinaryStream> lengthReader, boolean[] array, Object2BooleanFunction<BinaryStream> function, int maxLength) {
        return getArray(lengthReader.applyAsInt(this), array, function, maxLength);
    }

    public boolean[] getArray(int length, boolean[] array, Object2BooleanFunction<BinaryStream> function) {
        return getArray(length, array, function, 4096);
    }

    public boolean[] getArray(int length, boolean[] array, Object2BooleanFunction<BinaryStream> function, int maxLength) {
        checkLength(length, maxLength);
        BooleanCollection collection = new BooleanArrayList();
        for (int i = 0; i < length; i++) {
            collection.add(function.getBoolean(this));
        }
        return collection.toArray(array);
    }

    public <T> void putArray(T[] array, BiConsumer<BinaryStream, T> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public <T> void putArray(T[] array, BiConsumer<BinaryStream, T> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public <T> void putArray(ObjIntConsumer<BinaryStream> lengthWriter, T[] array, BiConsumer<BinaryStream, T> consumer) {
        lengthWriter.accept(this, array.length);
        for (T element : array) {
            consumer.accept(this, element);
        }
    }

    public <T> void putArray(Collection<T> array, BiConsumer<BinaryStream, T> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public <T> void putArray(Collection<T> array, BiConsumer<BinaryStream, T> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public <T> void putArray(ObjIntConsumer<BinaryStream> lengthWriter, Collection<T> array, BiConsumer<BinaryStream, T> consumer) {
        lengthWriter.accept(this, array.size());
        for (T element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(byte[] array, ObjectByteBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(byte[] array, ObjectByteBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, byte[] array, ObjectByteBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (byte element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(ByteCollection array, ObjectByteBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(ByteCollection array, ObjectByteBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, ByteCollection array, ObjectByteBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (byte element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(short[] array, ObjectShortBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(short[] array, ObjectShortBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, short[] array, ObjectShortBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (short element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(ShortCollection array, ObjectShortBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(ShortCollection array, ObjectShortBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, ShortCollection array, ObjectShortBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (short element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(char[] array, ObjectCharBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(char[] array, ObjectCharBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, char[] array, ObjectCharBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (char element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(CharCollection array, ObjectCharBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(CharCollection array, ObjectCharBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, CharCollection array, ObjectCharBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (char element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(int[] array, ObjIntConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(int[] array, ObjIntConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, int[] array, ObjIntConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (int element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(IntCollection array, ObjIntConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(IntCollection array, ObjIntConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, IntCollection array, ObjIntConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (int element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(long[] array, ObjLongConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(long[] array, ObjLongConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, long[] array, ObjLongConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (long element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(LongCollection array, ObjLongConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(LongCollection array, ObjLongConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, LongCollection array, ObjLongConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (long element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(float[] array, ObjectFloatBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(float[] array, ObjectFloatBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, float[] array, ObjectFloatBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (float element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(FloatCollection array, ObjectFloatBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(FloatCollection array, ObjectFloatBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, FloatCollection array, ObjectFloatBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (float element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(double[] array, ObjDoubleConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(double[] array, ObjDoubleConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, double[] array, ObjDoubleConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (double element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(DoubleCollection array, ObjDoubleConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(DoubleCollection array, ObjDoubleConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, DoubleCollection array, ObjDoubleConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (double element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(boolean[] array, ObjectBooleanBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(boolean[] array, ObjectBooleanBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, boolean[] array, ObjectBooleanBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.length);
        for (boolean element : array) {
            consumer.accept(this, element);
        }
    }

    public void putArray(BooleanCollection array, ObjectBooleanBiConsumer<BinaryStream> consumer) {
        putArray(BinaryStream::putUnsignedVarInt, array, consumer);
    }

    /**
     * @throws IndexOutOfBoundsException if the length of the array is greater than max length
     */
    public void putArray(BooleanCollection array, ObjectBooleanBiConsumer<BinaryStream> consumer, int maxLength) {
        putArray((stream, count) -> {
            if (count > maxLength) {
                throw new IndexOutOfBoundsException("array length exceeds max limit: " + count + " > " + maxLength);
            }
            stream.putUnsignedVarInt(count);
        }, array, consumer);
    }

    public void putArray(ObjIntConsumer<BinaryStream> lengthWriter, BooleanCollection array, ObjectBooleanBiConsumer<BinaryStream> consumer) {
        lengthWriter.accept(this, array.size());
        for (boolean element : array) {
            consumer.accept(this, element);
        }
    }

    public <T> T getEnum(Function<String, T> function) {
        return function.apply(getString());
    }

    public <T> void putEnum(T value, Function<T, String> function) {
        putString(function.apply(value));
    }

    @Nullable
    public <T> T getOptionalEnum(Function<String, T> function) {
        return getOptional(stream -> stream.getEnum(function));
    }

    public <T> void putOptionalEnum(@Nullable T value, Function<T, String> function) {
        putOptional(value, (stream, obj) -> stream.putEnum(obj, function));
    }

    @Nullable
    public <T> T getOptional(Function<BinaryStream, T> function) {
        return getOptional(function, null);
    }

    public <T> T getOptional(Function<BinaryStream, T> function, @Nullable T defaultValue) {
        return getBoolean() ? function.apply(this) : defaultValue;
    }

    public <T> void putOptional(@Nullable T obj, BiConsumer<BinaryStream, T> consumer) {
        if (obj == null) {
            putBoolean(false);
            return;
        }

        putBoolean(true);
        consumer.accept(this, obj);
    }

    public <T> void putOptional(boolean present, T obj, BiConsumer<BinaryStream, T> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, byte obj, ObjectByteBiConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, short obj, ObjectShortBiConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, char obj, ObjectCharBiConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, int obj, ObjIntConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, long obj, ObjLongConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, float obj, ObjectFloatBiConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, double obj, ObjDoubleConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public void putOptional(boolean present, boolean obj, ObjectBooleanBiConsumer<BinaryStream> consumer) {
        putBoolean(present);
        if (!present) {
            return;
        }
        consumer.accept(this, obj);
    }

    public StructureEditorData getStructureEditorData() {
        if (helper != null) {
            return helper.getStructureEditorData(this);
        }

        StructureEditorData data = new StructureEditorData();
        data.type = StructureBlockType.getValues()[(int) getUnsignedVarInt()];
        data.name = getString();
        data.dataField = getString();
        BlockVector3 offset = getSignedBlockPosition();
        BlockVector3 size = getSignedBlockPosition();
        boolean includeEntities = getBoolean();
        boolean ignoreBlocks = getBoolean();
        data.includePlayers = getBoolean();
        boolean showAir = getBoolean();
        data.settings = getStructureSettings();
        data.boundingBoxVisible = getBoolean();

        data.settings.size = size;
        data.settings.offset = offset;
        data.settings.ignoreBlocks = ignoreBlocks;
        return data;
    }

    public StructureSettings getStructureSettings() {
        if (helper != null) {
            return helper.getStructureSettings(this);
        }

        StructureSettings settings = new StructureSettings();
        settings.integrityValue = getLFloat();
        settings.integritySeed = (int) getUnsignedVarInt();
        settings.mirror = StructureMirror.getValues()[(int) getUnsignedVarInt()];
        settings.rotation = Rotation.getValues()[(int) getUnsignedVarInt()];
        settings.ignoreEntities = getBoolean();
        boolean ignoreStructureBlocks = getBoolean();
        BlockVector3 boundingBoxMin = getSignedBlockPosition();
        BlockVector3 boundingBoxMax = getSignedBlockPosition();
        return settings;
    }

    public BitSet getBitSet(int size) {
        long[] bitSet = new long[Math.ceilDiv(size, Long.SIZE)];
        int index = 0;
        int shift = 0;
        int num = 0;
        while (true) {
            if (num >= size) {
                throw new IllegalArgumentException("VarBitSet was too large: " + size);
            }
            byte b = getSignedByte();
            long bits = b & 0x7f;
            bitSet[index] |= bits << shift;
            int nextShift = shift + 7;
            if (nextShift >= 64) {
                nextShift -= 64;
                if (++index < bitSet.length) {
                    bitSet[index] = bits >>> (7 - nextShift);
                }
            }
            num += 7;
            if ((b & 0x80) == 0) {
                break;
            }
            shift = nextShift;
        }
        // Discard high bits beyond the declared size to match vanilla behavior.
        int lastWordBits = size % Long.SIZE;
        if (lastWordBits != 0) {
            bitSet[bitSet.length - 1] &= -1L >>> (Long.SIZE - lastWordBits);
        }
        return BitSet.valueOf(bitSet);
    }

    public static int checkLength(int length, int maxLength) {
        if (length < 0 || length > maxLength) {
            throw new IndexOutOfBoundsException("Invalid length: " + length + " (maximum " + maxLength + ")");
        }
        return length;
    }

    public static int checkLength(long length, int maxLength) {
        if (length < 0 || length > maxLength) {
            throw new IndexOutOfBoundsException("Invalid length: " + length + " (maximum " + maxLength + ")");
        }
        return (int) length;
    }

    public boolean isReadable(int length) {
        return length >= 0 && offset >= 0 && offset <= count && length <= count - offset;
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

        public int getProtocolVersion() {
            return ProtocolInfo.CURRENT_PROTOCOL;
        }

        public void putSkin(BinaryStream stream, Skin skin) {
            stream.putSkinV2(skin);
        }

        public Skin getSkin(BinaryStream stream) {
            return stream.getSkinV2();
        }

        public void putGameRules(BinaryStream stream, GameRules gameRules, boolean network) {
            if (gameRules == null) {
                stream.putUnsignedVarInt(0);
                return;
            }

            Map<GameRule, GameRules.Value> rules = gameRules.getGameRules();
            stream.putUnsignedVarInt(rules.size());
            rules.forEach((gameRule, value) -> {
                stream.putString(gameRule.getBedrockName());
                value.write(stream, network);
            });
        }

        public Item getItemStack(BinaryStream stream) {
            return this.getSlot(stream);
        }

        public void putItemStack(BinaryStream stream, Item item) {
            this.putSlot(stream, item);
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

        public RecipeIngredient getRecipeIngredient(BinaryStream stream) {
            return RecipeIngredient.EMPTY;
        }

        public void putRecipeIngredient(BinaryStream stream, RecipeIngredient ingredient) {
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

        public void putCommandData(BinaryStream stream, Map<String, CommandDataVersions> commands, List<CommandEnum> enums, List<String> postFixes, List<CommandEnum> softEnums, List<ChainedSubCommandData> chainedSubCommands) {
            //TODO: 1.13前的重构
            stream.putUnsignedVarInt(0);
        }

        public int getItemNetworkId(BinaryStream stream, Item item) {
            return RuntimeItems.getNetworkId(RuntimeItems.getNetworkFullId(item));
        }

        /**
         * @since 1.16.0
         */
        public ItemStackRequest getItemStackRequest(BinaryStream stream) {
            return null;
        }

        /**
         * @since 1.16.0
         */
        public void putItemStackResponse(BinaryStream stream, ItemStackResponse response) {
        }

        public final int getCommandParameterTypeId(CommandParamType type) {
            return this.getCommandParameterTypeId(type, type.getId());
        }

        public final int getCommandParameterTypeId(CommandParamType type, int defaultValue) {
            return this.commandParameterType2Id.getOrDefault(type, defaultValue);
        }

        public StructureEditorData getStructureEditorData(BinaryStream stream) {
            StructureEditorData data = new StructureEditorData();
            data.type = StructureBlockType.getValues()[(int) stream.getUnsignedVarInt()];
            data.name = stream.getString();
            data.dataField = stream.getString();
            BlockVector3 offset = stream.getSignedBlockPosition();
            BlockVector3 size = stream.getSignedBlockPosition();
            boolean includeEntities = stream.getBoolean();
            boolean ignoreBlocks = stream.getBoolean();
            data.includePlayers = stream.getBoolean();
            boolean showAir = stream.getBoolean();
            data.settings = getStructureSettings(stream);
            data.boundingBoxVisible = stream.getBoolean();

            data.settings.size = size;
            data.settings.offset = offset;
            data.settings.ignoreBlocks = ignoreBlocks;
            return data;
        }

        public StructureSettings getStructureSettings(BinaryStream stream) {
            StructureSettings settings = new StructureSettings();
            settings.integrityValue = stream.getLFloat();
            settings.integritySeed = (int) stream.getUnsignedVarInt();
            settings.mirror = StructureMirror.getValues()[(int) stream.getUnsignedVarInt()];
            settings.rotation = Rotation.getValues()[(int) stream.getUnsignedVarInt()];
            settings.ignoreEntities = stream.getBoolean();
            boolean ignoreStructureBlocks = stream.getBoolean();
            BlockVector3 boundingBoxMin = stream.getSignedBlockPosition();
            BlockVector3 boundingBoxMax = stream.getSignedBlockPosition();
            return settings;
        }

        /**
         * @since 1.19.10
         */
        public void putAbilityLayer(BinaryStream stream, AbilityLayer layer) {
        }

        public BlockVector3 getBlockVector3(BinaryStream stream) {
            return new BlockVector3(stream.getVarInt(), (int) stream.getUnsignedVarInt(), stream.getVarInt());
        }

        public void putBlockVector3(BinaryStream stream, int x, int y, int z) {
            stream.putVarInt(x);
            stream.putUnsignedVarInt(y);
            stream.putVarInt(z);
        }
    }
}
