package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockToolType;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Item implements Cloneable, ItemID {

    protected static final String UNKNOWN_STR = "Unknown";

    private static boolean initialized;
    public static final Class<?>[] list = new Class[Short.MAX_VALUE];

    private static final List<Item> creative = new ObjectArrayList<>();

    protected Block block = null;
    protected final int id;
    protected int meta;
    protected boolean hasMeta = true;
    private byte[] tags = new byte[0];
    private CompoundTag cachedNBT = null;
    public int count;
    private final String name;
    private boolean pickedUp;
    @Nullable
    private Set<String> canPlaceOnBlocks;
    @Nullable
    private Set<String> canDestroyBlocks;

    private int itemStackId = Integer.MIN_VALUE;

    public Item(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public Item(int id, int meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public Item(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public Item(int id, int meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public Item(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public Item(int id, int meta, int count, String name) {
        this.id = id;
        if (meta >= 0) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = name;
        this.initItem();
    }

    public Item(int id, Integer meta, int count, String name) {
        this.id = id;
        if (meta != null && meta >= 0) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = name;
        /*f (this.block != null && this.id <= 0xff && Block.list[id] != null) { //probably useless
            this.block = Block.get(this.id, this.meta);
            this.name = this.block.getName();
        }*/
        this.initItem();
    }

    public void initItem() {
    }

    public boolean hasMeta() {
        return hasMeta;
    }

    public boolean hasAnyDamageValue() {
        return this.meta == -1;
    }

    public boolean canBeActivated() {
        return false;
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        Items.registerVanillaItems();

        for (int i = 0; i < 256; ++i) {
            if (Block.list[i] != null) {
                list[i] = Block.list[i];
            }
        }

/*
        initCreativeItems();
*/
    }

    @SuppressWarnings("unchecked")
    private static void initCreativeItems() {
        clearCreativeItems();

        Config config = new Config(Config.JSON);
        config.load(Server.class.getClassLoader().getResourceAsStream("creativeitems.json"));
        List<Map<?, ?>> list = config.getMapList("items");

        for (Map map : list) {
            try {
                Item item = fromJson(map, true);
                if (item != null) {
                    addCreativeItem(item);
                }
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
    }

    public static void clearCreativeItems() {
        Item.creative.clear();
    }

    public static List<Item> getCreativeItems() {
        return new ObjectArrayList<>(Item.creative);
    }

    public static void addCreativeItem(Item item) {
        Item.creative.add(item.clone());
    }

    public static void removeCreativeItem(Item item) {
        int index = getCreativeItemIndex(item);
        if (index != -1) {
            Item.creative.remove(index);
        }
    }

    public static boolean isCreativeItem(Item item) {
        for (Item aCreative : Item.creative) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        return false;
    }

    public static Item getCreativeItem(int index) {
        return (index >= 0 && index < Item.creative.size()) ? Item.creative.get(index) : null;
    }

    public static int getCreativeItemIndex(Item item) {
        for (int i = 0; i < Item.creative.size(); i++) {
            if (item.equals(Item.creative.get(i), !item.isTool())) {
                return i;
            }
        }
        return -1;
    }

    public static Item get(int id) {
        return Items.get(id, 0, 1, new byte[0]);
    }

    public static Item get(int id, int meta) {
        return Items.get(id, meta, 1, new byte[0]);
    }

    public static Item get(int id, Integer meta) {
        return Items.get(id, meta, 1, new byte[0]);
    }

    public static Item get(int id, int meta, int count) {
        return Items.get(id, meta, count, new byte[0]);
    }

    public static Item get(int id, Integer meta, int count) {
        return Items.get(id, meta, count, new byte[0]);
    }

    public static Item get(int id, int meta, int count, byte[] tags) {
        return Items.get(id, meta, count, tags);
    }

    public static Item get(int id, Integer meta, int count, byte[] tags) {
        return Items.get(id, meta, count, tags);
    }

    @Nullable
    public static Item getCraftingItem(int id, int meta, int count, byte[] tags) {
        try {
            Class<?> c = id > 0 ? list[id] : Block.list[0xff - id];
            Item item;

            if (c == null) {
//                item = new Item(id, meta, count);
                return null;
            } else if (id < 256 && id != 166) {
                item = new ItemBlock(Block.fromItemId(id, meta != -1 ? meta : 0), meta, count);
            } else {
                item = ((Item) c.getConstructor(Integer.class, int.class).newInstance(meta, count));
            }

            if (tags.length != 0) {
                item.setCompoundTag(tags);
            }

            return item;
        } catch (Exception e) {
//            return new Item(id, meta, count).setCompoundTag(tags);
            return null;
        }
    }

    @Nullable
    public static Item getCraftingItem(int id, int meta, int count, CompoundTag tags) {
        try {
            Class<?> c = id > 0 ? list[id] : Block.list[0xff - id];
            Item item;

            if (c == null) {
//                item = new Item(id, meta, count);
                return null;
            } else if (id < 256 && id != 166) {
                item = new ItemBlock(Block.fromItemId(id, meta != -1 ? meta : 0), meta, count);
            } else {
                item = ((Item) c.getConstructor(Integer.class, int.class).newInstance(meta, count));
            }

            if (tags != null) {
                item.setCompoundTag(tags);
            }

            return item;
        } catch (Exception e) {
//            return new Item(id, meta, count).setCompoundTag(tags);
            return null;
        }
    }

    public static Item fromString(String str) {
        return fromString(str, false);
    }

    public static Item fromString(String str, boolean lookupAlias) {
        String[] split = str.split(":", 3);

        String name;
        int meta;
        if (split.length > 2) {
            name = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]) & 0xffff;
            } catch (NumberFormatException e) {
                meta = 0;
            }
        } else if (split.length == 2) {
            try {
                meta = Integer.parseInt(split[1]) & 0xFFFF;
                name = split[0];
            } catch (NumberFormatException e) {
                name = str;
                meta = 0;
            }
        } else {
            name = str;
            meta = 0;
        }

        int id;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            int fullId = Items.getFullIdByName(name, true, lookupAlias);
            if (fullId != Integer.MIN_VALUE) {
                id = Item.getIdFromFullId(fullId);
                int auxVal = Item.getMetaFromFullId(fullId);
                if (auxVal != 0) {
                    meta = auxVal;
                }
            } else {
                id = AIR;
            }
        }

        return get(id, meta);
    }

    @Nullable
    public static Item fromStringNullable(String str) {
        return fromStringNullable(str, false);
    }

    @Nullable
    public static Item fromStringNullable(String str, boolean lookupAlias) {
        String[] split = str.split(":", 3);

        String name;
        int meta;
        if (split.length > 2) {
            name = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]) & 0xffff;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (split.length == 2) {
            try {
                meta = Integer.parseInt(split[1]) & 0xffff;
                name = split[0];
            } catch (NumberFormatException e) {
                name = str;
                meta = 0;
            }
        } else {
            name = str;
            meta = 0;
        }

        int id;
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            int fullId = Items.getFullIdByName(name, true, lookupAlias);
            if (fullId == Integer.MIN_VALUE) {
                return null;
            }
            id = Item.getIdFromFullId(fullId);
            int auxVal = Item.getMetaFromFullId(fullId);
            if (auxVal != 0) {
                meta = auxVal;
            }
        }

        Item item = get(id, meta);
        if (id != AIR && item.getId() == AIR) {
            return null;
        }
        return item;
    }

    @Nullable
    public static Item fromIdentifier(String identifier) {
        return fromIdentifier(identifier, 0);
    }

    @Nullable
    public static Item fromIdentifier(String identifier, boolean lookupAlias) {
        return fromIdentifier(identifier, 0, lookupAlias);
    }

    @Nullable
    public static Item fromIdentifier(String identifier, int meta) {
        return fromIdentifier(identifier, meta, false);
    }

    @Nullable
    public static Item fromIdentifier(String identifier, int meta, boolean lookupAlias) {
        int fullId = Items.getFullIdByName(identifier, true, lookupAlias);
        if (fullId == Integer.MIN_VALUE) {
            return null;
        }

        int auxVal = Item.getMetaFromFullId(fullId);
        if (auxVal != 0) {
            meta = auxVal;
        }

        return get(Item.getIdFromFullId(fullId), meta);
    }

    @Nullable
    public static Item fromJson(Map<String, Object> data) {
        return fromJson(data, false);
    }

    @Nullable
    public static Item fromJson(Map<String, Object> data, boolean ignoreUnsupported) {
        String nbt = (String) data.get("nbt_b64");
        byte[] nbtBytes;
        if (nbt != null) {
            nbtBytes = Base64.getDecoder().decode(nbt);
        } else { // Support old format for backwards compat
            nbt = (String) data.getOrDefault("nbt_hex", null);
            if (nbt == null) {
                nbtBytes = new byte[0];
            } else {
                nbtBytes = Utils.parseHexBinary(nbt);
            }
        }

        int id = Utils.toInt(data.get("id"));

//        if (ignoreUnsupported && id < 0) return null;

        return getCraftingItem(id, Utils.toInt(data.getOrDefault("damage", 0)), Utils.toInt(data.getOrDefault("count", 1)), nbtBytes);
    }

    public static Item[] fromStringMultiple(String str) {
        String[] b = str.split(",");
        Item[] items = new Item[b.length - 1];
        for (int i = 0; i < b.length; i++) {
            items[i] = fromString(b[i]);
        }
        return items;
    }

    public static int parseFullId(String str) {
        return parseFullId(str, false);
    }

    public static int parseFullId(String str, boolean lookupAlias) {
        try {
            return Item.getFullId(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Items.getFullIdByName(str, true, lookupAlias);
        }
    }

    public Item setCompoundTag(CompoundTag tag) {
        this.setNamedTag(tag);
        return this;
    }

    public Item setCompoundTag(byte[] tags) {
        if (tags == null) {
            tags = new byte[0];
        }
        this.tags = tags;
        this.cachedNBT = null;
        return this;
    }

    public Item clearCompoundTag() {
        tags = null;
        return this;
    }

    public byte[] getCompoundTag() {
        return tags;
    }

    public boolean hasCompoundTag() {
        return this.tags != null && this.tags.length > 0;
    }

    public boolean hasCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        return tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag;

    }

    public Item clearCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return this;
        }
        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag) {
            tag.remove("BlockEntityTag");
            this.setNamedTag(tag);
        }

        return this;
    }

    public Item setCustomBlockData(CompoundTag compoundTag) {
        CompoundTag tags = compoundTag.copy();
        tags.setName("BlockEntityTag");

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        tag.putCompound("BlockEntityTag", tags);
        this.setNamedTag(tag);

        return this;
    }

    @Nullable
    public CompoundTag getCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag")) {
            Tag bet = tag.get("BlockEntityTag");
            if (bet instanceof CompoundTag) {
                return (CompoundTag) bet;
            }
        }

        return null;
    }

    public boolean hasEnchantments() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("ench")) {
            Tag enchTag = tag.get("ench");
            return enchTag instanceof ListTag;
        }

        return false;
    }

    @Nullable
    public Enchantment getEnchantment(int id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAllUnsafe()) {
            if (entry.getShort("id") == id) {
                Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
                if (e != null) {
                    int level = entry.getShort("lvl");
                    if (level <= 0) {
                        return null;
                    }
                    e.setLevel(level, false);
                    return e;
                }
            }
        }

        return null;
    }

    public int getEnchantmentLevel(int id) {
        Enchantment enchant = getEnchantment(id);
        return enchant == null ? 0 : enchant.getLevel();
    }

    public int getValidEnchantmentLevel(int id) {
        Enchantment enchant = getEnchantment(id);
        return enchant == null ? 0 : enchant.getValidLevel();
    }

    public Item addEnchantment(Enchantment... enchantments) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        ListTag<CompoundTag> ench;
        if (!tag.contains("ench")) {
            ench = new ListTag<>("ench");
            tag.putList(ench);
        } else {
            ench = tag.getList("ench", CompoundTag.class);
        }

        boolean changed = false;
        for (Enchantment enchantment : enchantments) {
            boolean remove = enchantment.getLevel() <= 0;
            boolean found = false;

            for (int k = 0; k < ench.size(); k++) {
                CompoundTag entry = ench.get(k);
                if (entry.getShort("id") == enchantment.getId()) {
                    if (remove) {
                        changed = true;
                        ench.remove(k);
                    } else if (entry.getShort("lvl") != enchantment.getLevel()) {
                        changed = true;
                        ench.add(k, new CompoundTag()
                                .putShort("id", enchantment.getId())
                                .putShort("lvl", enchantment.getLevel())
                        );
                    }

                    found = true;
                    break;
                }
            }

            if (!found && !remove) {
                changed = true;
                ench.add(new CompoundTag()
                        .putShort("id", enchantment.getId())
                        .putShort("lvl", enchantment.getLevel())
                );
            }
        }
        if (!changed) {
            return this;
        }

        this.setNamedTag(tag);
        return this;
    }

    public boolean removeEnchantment(int id) {
        return removeEnchantment(id, 0);
    }

    public boolean removeEnchantment(int id, int level) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return false;
        }

        ListTag<CompoundTag> ench = tag.getList("ench", (ListTag<CompoundTag>) null);
        if (ench == null || ench.isEmpty()) {
            return false;
        }

        boolean removed = false;
        Iterator<CompoundTag> iter = ench.iterator();
        while (iter.hasNext()) {
            CompoundTag entry = iter.next();
            if (entry.getShort("id") != id) {
                continue;
            }

            if (level > 0 && entry.getShort("lvl") != level) {
                break;
            }

            removed = true;
            iter.remove();
            break;
        }
        if (!removed) {
            return false;
        }
        if (ench.isEmpty()) {
            tag.remove("ench");
        }

        setNamedTag(tag);
        return true;
    }

    public boolean removeEnchantments() {
        CompoundTag tag = getNamedTag();
        if (tag == null || tag.removeAndGet("ench") == null) {
            return false;
        }
        setNamedTag(tag);
        return true;
    }

    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return Enchantment.EMPTY;
        }

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", CompoundTag.class);
        List<Enchantment> enchantments = new ObjectArrayList<>(ench.size());

        for (CompoundTag entry : ench.getAllUnsafe()) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            if (e != null) {
                int level = entry.getShort("lvl");
                if (level <= 0) {
                    continue;
                }
                e.setLevel(level, false);
                enchantments.add(e);
            }
        }

        return enchantments.toArray(Enchantment.EMPTY);
    }

    public boolean hasEnchantment(int id) {
        Enchantment enchantment = this.getEnchantment(id);
        return enchantment != null && enchantment.getLevel() > 0;
    }

    public int getRepairCost() {
        if (this.hasCompoundTag()) {
            CompoundTag tag = this.getNamedTag();
            if (tag.contains("RepairCost")) {
                Tag repairCost = tag.get("RepairCost");
                if (repairCost instanceof IntTag) {
                    return ((IntTag) repairCost).data;
                }
            }
        }
        return 0;
    }

    public Item setRepairCost(int cost) {
        if (cost <= 0) {
            if (!this.hasCompoundTag()) {
                return this;
            }
            return this.setNamedTag(this.getNamedTag().remove("RepairCost"));
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        return this.setNamedTag(tag.putInt("RepairCost", cost));
    }

    public boolean isKeepOnDeath() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (!tag.contains("minecraft:keep_on_death")) {
            return false;
        }

        return tag.getBoolean("minecraft:keep_on_death");
    }

    public Item setKeepOnDeath(boolean keepOnDeath) {
        if (!keepOnDeath) {
            if (hasCompoundTag()) {
                return setNamedTag(getNamedTag().remove("minecraft:keep_on_death"));
            }
            return this;
        }

        return setNamedTag(getOrCreateNamedTag().putBoolean("minecraft:keep_on_death", true));
    }

    /**
     * @since 1.16.100
     */
    public boolean hasLock() {
        if (!hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = getNamedTag();
        if (!tag.contains("minecraft:item_lock")) {
            return false;
        }

        return tag.getByte("minecraft:item_lock") != 0;
    }

    /**
     * @since 1.16.100
     */
    public Item lockInSlot() {
        return setNamedTag(getOrCreateNamedTag().putByte("minecraft:item_lock", 1));
    }

    /**
     * @since 1.16.100
     */
    public boolean isLockInSlot() {
        if (!hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = getNamedTag();
        if (!tag.contains("minecraft:item_lock")) {
            return false;
        }

        return tag.getByte("minecraft:item_lock") == 1;
    }

    /**
     * @since 1.16.100
     */
    public Item lockInInventory() {
        return setNamedTag(getOrCreateNamedTag().putByte("minecraft:item_lock", 2));
    }

    /**
     * @since 1.16.100
     */
    public boolean isLockInInventory() {
        if (!hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = getNamedTag();
        if (!tag.contains("minecraft:item_lock")) {
            return false;
        }

        return tag.getByte("minecraft:item_lock") == 2;
    }

    /**
     * @since 1.16.100
     */
    public Item unlock() {
        if (!hasCompoundTag()) {
            return this;
        }

        CompoundTag tag = getNamedTag();
        if (!tag.contains("minecraft:item_lock")) {
            return this;
        }

        return setNamedTag(tag.remove("minecraft:item_lock"));
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            return tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag;
        }

        return false;
    }

    public String getCustomName() {
        if (!this.hasCompoundTag()) {
            return "";
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return ((CompoundTag) tag1).getString("Name");
            }
        }

        return "";
    }

    public Item setCustomName(String name) {
        if (name == null || name.isEmpty()) {
            return this.clearCustomName();
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").putString("Name", name);
        } else {
            tag.putCompound("display", new CompoundTag("display")
                    .putString("Name", name)
            );
        }
        this.setNamedTag(tag);
        return this;
    }

    public Item clearCustomName() {
        if (!this.hasCompoundTag()) {
            return this;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").remove("Name");
            if (tag.getCompound("display").isEmpty()) {
                tag.remove("display");
            }

            this.setNamedTag(tag);
        }

        return this;
    }

    public String[] getLore() {
        Tag tag = this.getNamedTagEntry("display");
        List<String> lines = new ObjectArrayList<>();

        if (tag instanceof CompoundTag) {
            CompoundTag nbt = (CompoundTag) tag;
            ListTag<StringTag> lore = nbt.getList("Lore", StringTag.class);

            if (!lore.isEmpty()) {
                for (StringTag stringTag : lore.getAllUnsafe()) {
                    lines.add(stringTag.data);
                }
            }
        }

        return lines.toArray(new String[0]);
    }

    public Item setLore(String... lines) {
        if (lines == null || lines.length == 0) {
            return clearLore();
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        ListTag<StringTag> lore = new ListTag<>("Lore");

        for (String line : lines) {
            lore.add(new StringTag("", line));
        }

        if (!tag.contains("display")) {
            tag.putCompound("display", new CompoundTag("display").putList(lore));
        } else {
            tag.getCompound("display").putList(lore);
        }

        this.setNamedTag(tag);
        return this;
    }

    public Item clearLore() {
        CompoundTag tag = getNamedTag();
        if (tag == null || !(tag.get("display") instanceof CompoundTag display)
                || display.removeAndGet("Lore") == null) {
            return this;
        }

        if (display.isEmpty()) {
            tag.remove("display");
        }

        return setNamedTag(tag);
    }

    public String[] getDynamicPropertyIds(String module) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return new String[0];
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return new String[0];
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return new String[0];
        }

        return moduleProperties.keySet().toArray(new String[0]);
    }

    public boolean hasDynamicProperty(String module, String name) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return false;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return false;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return false;
        }

        return moduleProperties.contains(name);
    }

    public double getDynamicPropertyDouble(String module, String name) {
        return getDynamicPropertyDouble(module, name, 0);
    }

    public double getDynamicPropertyDouble(String module, String name, double defaultValue) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return defaultValue;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return defaultValue;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return defaultValue;
        }

        return moduleProperties.getDouble(name, defaultValue);
    }

    public boolean getDynamicPropertyBoolean(String module, String name) {
        return getDynamicPropertyBoolean(module, name, false);
    }

    public boolean getDynamicPropertyBoolean(String module, String name, boolean defaultValue) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return defaultValue;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return defaultValue;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return defaultValue;
        }

        return moduleProperties.getBoolean(name, defaultValue);
    }

    public String getDynamicPropertyString(String module, String name) {
        return getDynamicPropertyString(module, name, "");
    }

    public String getDynamicPropertyString(String module, String name, String defaultValue) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return defaultValue;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return defaultValue;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return defaultValue;
        }

        return moduleProperties.getString(name, defaultValue);
    }

    public Vector3f getDynamicPropertyVector3f(String module, String name) {
        return getDynamicPropertyVector3f(module, name, new Vector3f());
    }

    public Vector3f getDynamicPropertyVector3f(String module, String name, Vector3f defaultValue) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return defaultValue;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return defaultValue;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return defaultValue;
        }

        return Vector3f.fromNbt(moduleProperties.getList(name, defaultValue.toNbt()));
    }

    /// Pair<RootTag, ModuleDynamicProperties>
    private Pair<CompoundTag, CompoundTag> createDynamicPropertyModule(String module) {
        CompoundTag tag = getOrCreateNamedTag();
        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            dynamicProperties = new CompoundTag();
            tag.putCompound("DynamicProperties", dynamicProperties);
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            moduleProperties = new CompoundTag();
            dynamicProperties.putCompound(module, moduleProperties);
        }
        return Pair.of(tag, moduleProperties);
    }

    public Item setDynamicProperty(String module, String name, double value) {
        Pair<CompoundTag, CompoundTag> result = createDynamicPropertyModule(module);
        result.right().putDouble(name, value);
        setNamedTag(result.left());
        return this;
    }

    public Item setDynamicProperty(String module, String name, boolean value) {
        Pair<CompoundTag, CompoundTag> result = createDynamicPropertyModule(module);
        result.right().putBoolean(name, value);
        setNamedTag(result.left());
        return this;
    }

    public Item setDynamicProperty(String module, String name, String value) {
        if (value == null) {
            return clearDynamicProperty(module, name);
        }

        Pair<CompoundTag, CompoundTag> result = createDynamicPropertyModule(module);
        result.right().putString(name, value);
        setNamedTag(result.left());
        return this;
    }

    public Item setDynamicProperty(String module, String name, Vector3f value) {
        if (value == null) {
            return clearDynamicProperty(module, name);
        }

        Pair<CompoundTag, CompoundTag> result = createDynamicPropertyModule(module);
        result.right().putList(name, value.toNbt());
        setNamedTag(result.left());
        return this;
    }

    public Item clearDynamicProperty(String module, String name) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return this;
        }

        CompoundTag dynamicProperties = tag.getCompound("DynamicProperties", null);
        if (dynamicProperties == null) {
            return this;
        }

        CompoundTag moduleProperties = dynamicProperties.getCompound(module, null);
        if (moduleProperties == null) {
            return this;
        }

        if (moduleProperties.removeAndGet(name) != null) {
            if (moduleProperties.isEmpty()) {
                dynamicProperties.remove(module);
                if (dynamicProperties.isEmpty()) {
                    tag.remove("DynamicProperties");
                }
            }
            setNamedTag(tag);
        }
        return this;
    }

    public Item clearDynamicProperties(String module) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return this;
        }

        CompoundTag properties = tag.getCompound("DynamicProperties", null);
        if (properties == null) {
            return this;
        }

        if (properties.removeAndGet(module) != null) {
            if (properties.isEmpty()) {
                tag.remove("DynamicProperties");
            }
            setNamedTag(tag);
        }
        return this;
    }

    public Item clearDynamicProperties() {
        CompoundTag tag = getNamedTag();
        if (tag != null && tag.removeAndGet("DynamicProperties") != null) {
            setNamedTag(tag);
        }
        return this;
    }

    @Nullable
    public Tag getNamedTagEntry(String name) {
        CompoundTag tag = this.getNamedTag();
        if (tag != null) {
            return tag.contains(name) ? tag.get(name) : null;
        }

        return null;
    }

    public CompoundTag getOrCreateNamedTag() {
        if (!this.hasCompoundTag()) {
            return new CompoundTag();
        }
        return this.getNamedTag();
    }

    @Nullable
    public CompoundTag getNamedTag() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        if (this.cachedNBT == null) {
            this.cachedNBT = parseCompoundTag(this.tags);
        }

        this.cachedNBT.setName("");

        return this.cachedNBT;
    }

    public Item setNamedTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            return this.clearNamedTag();
        }
        tag.setName(null);

        this.cachedNBT = tag;
        this.tags = writeCompoundTag(tag);

        return this;
    }

    public Item clearNamedTag() {
        return this.setCompoundTag(new byte[0]);
    }

    public static CompoundTag parseCompoundTag(byte[] tag) {
        try {
            return NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] writeCompoundTag(CompoundTag tag) {
        try {
            tag.setName("");
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isNull() {
        return this.count <= 0 || this.id == AIR;
    }

    final public String getName() {
        return this.hasCustomName() ? this.getCustomName() : this.name;
    }

    final public boolean canBePlaced() {
        return ((this.block != null) && this.block.canBePlaced());
    }

    public Block getBlock() {
        if (this.block != null) {
            return this.block.clone();
        } else {
            return Block.get(BlockID.AIR);
        }
    }

    public Block getBlockUnsafe() {
        return this.block;
    }

    public void setBlockUnsafe(Block block) {
        this.block = block;
    }

    public int getId() {
        return id;
    }

    public int getBlockId() {
        return id < 0 ? 0xff - id : id;
    }

    public int getDamage() {
        return meta;
    }

    public void setDamage(int meta) {
        this.meta = meta & 0xffff;
    }

    public void setDamage(Integer meta) {
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
    }

    public int getDefaultMeta() {
        return 0;
    }

    public boolean isValidMeta(int meta) {
        return isStackedByData() || meta == getDefaultMeta();
    }

    public boolean isStackedByData() {
        return false;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public int getFuelTime() {
        return 0;
    }

    public boolean useOn(Entity entity) {
        return false;
    }

    public boolean useOn(Block block) {
        return false;
    }

    public boolean isTool() {
        return false;
    }

    public int getBlockToolType() {
        return BlockToolType.NONE;
    }

    public int getMaxDurability() {
        return -1;
    }

    public boolean keepDamageTag() {
        return false;
    }

    public int getTier() {
        return 0;
    }

    public boolean isPickaxe() {
        return false;
    }

    public boolean isAxe() {
        return false;
    }

    public boolean isSword() {
        return false;
    }

    public boolean isShovel() {
        return false;
    }

    public boolean isHoe() {
        return false;
    }

    public boolean isShears() {
        return false;
    }

    public boolean isSpear() {
        return false;
    }

    public boolean isArmor() {
        return false;
    }

    public boolean isHelmet() {
        return false;
    }

    public boolean isChestplate() {
        return false;
    }

    public boolean isLeggings() {
        return false;
    }

    public boolean isBoots() {
        return false;
    }

    public boolean isHorseArmor() {
        return false;
    }

    public boolean isNautilusArmor() {
        return false;
    }

    public int getEnchantAbility() {
        return 0;
    }

    public int getAttackDamage() {
        return 0;
    }

    public int getArmorPoints() {
        return 0;
    }

    public int getToughness() {
        return 0;
    }

    public float getKnockbackResistance() {
        return 0;
    }

    public boolean isUnbreakable() {
        return false;
    }

    public void onUsing(Player player, int ticksUsed) {
    }

    public boolean onUse(Player player, int ticksUsed) {
        return false;
    }

    public boolean onRelease(Player player, int ticksUsed) {
        return false;
    }

    @Override
    final public String toString() {
        return (itemStackId != Integer.MIN_VALUE ? "<" + itemStackId + ">" : "") + "Item " + this.name + " (" + this.id + ":" + (!this.hasMeta ? "?" : this.meta) + ")x" + this.count + (this.hasCompoundTag() ? " tags:0x" + Binary.bytesToHexString(this.getCompoundTag()) : "");
    }

    public boolean canDestroy(Block block) {
        Set<String> canDestroyBlocks = this.canDestroyBlocks;
        if (canDestroyBlocks == null || canDestroyBlocks.isEmpty()) {
            return false;
        }
        int blockId = block.getId();
        for (String blockName : canDestroyBlocks) {
            if (Blocks.getIdByBlockName(blockName, true) == blockId) {
                return true;
            }
        }
        return false;
    }

    public boolean canPlaceOn(Block block) {
        Set<String> canPlaceOnBlocks = this.canPlaceOnBlocks;
        if (canPlaceOnBlocks == null || canPlaceOnBlocks.isEmpty()) {
            return false;
        }
        int blockId = block.getId();
        for (String blockName : canPlaceOnBlocks) {
            if (Blocks.getIdByBlockName(blockName, true) == blockId) {
                return true;
            }
        }
        return false;
    }

    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, float fx, float fy, float fz) {
        return false;
    }

    /**
     * Called when a player uses the item on air, for example throwing a projectile.
     * Returns whether the item was changed, for example count decrease or durability change.
     *
     * @param player player
     * @param directionVector direction
     * @return item changed
     */
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return false;
    }

    /**
     * @since 1.19.50
     */
    public boolean isItemBlock() {
        return false;
    }

    public boolean isBlockItem() {
        return id < 256 && id != GLOW_STICK;
    }

    public Item split(int count) {
        Item newItem = clone();

        int newCount = Math.min(count, this.count);
        newItem.count = newCount;
        this.count -= newCount;

        return newItem;
    }

    public void grow(int count) {
        this.count += count;
    }

    public void shrink(int count) {
        this.count -= count;
    }

    public void pop() {
        this.count--;
    }

    public float getFurnaceXpMultiplier() {
        return 0;
    }

    public int getCompostableChance() {
        return 0;
    }

    public int getDamageChance(int unbreaking) {
        return 100 / (unbreaking + 1);
    }

    public int hurtAndBreak(int deltaDamage) {
        if (deltaDamage <= 0) {
            return 0;
        }

        if (!(this instanceof ItemDurable)) {
            return 0;
        }

        if (isUnbreakable()) {
            return 0;
        }

        int durability = getEnchantmentLevel(Enchantment.UNBREAKING);
        if (durability > 0 && ThreadLocalRandom.current().nextInt(100) >= getDamageChance(durability)) {
            return 0;
        }

        int oldDamage = getDamage();
        int newDamage = oldDamage + deltaDamage;
        setDamage(newDamage);
        if (newDamage <= getMaxDurability()) {
            return deltaDamage;
        }
        return -1;
    }

    public boolean canRelease() {
        return false;
    }

    public int getUseDuration() {
        return 0;
    }

    public int getSwingDuration() {
        return 6;
    }

    public boolean canDualWield() {
        return false;
    }

    @Override
    public final boolean equals(Object item) {
        return item instanceof Item && this.equals((Item) item, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        return equals(item, checkDamage, checkCompound, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound, boolean checkComponents) {
        if (item != null && this.getId() == item.getId() && (!checkDamage || this.getDamage() == item.getDamage() || id == AIR)) {
            if (checkCompound) {
                if (Arrays.equals(this.getCompoundTag(), item.getCompoundTag())) {
                    if (!checkComponents) {
                        return true;
                    }
                    return Objects.equals(this.getCanPlaceOnBlocks(), item.getCanPlaceOnBlocks())
                            && Objects.equals(this.getCanDestroyBlocks(), item.getCanDestroyBlocks());
                }
                if (this.hasCompoundTag() && item.hasCompoundTag() && this.getNamedTag().equals(item.getNamedTag())) {
                    if (!checkComponents) {
                        return true;
                    }
                    return Objects.equals(this.getCanPlaceOnBlocks(), item.getCanPlaceOnBlocks())
                            && Objects.equals(this.getCanDestroyBlocks(), item.getCanDestroyBlocks());
                }
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the specified item stack has the same ID, damage, NBT and count as this item stack.
     *
     * @param other item
     * @return equal
     */
    public final boolean equalsExact(Item other) {
        return this.equals(other, true, true) && (this.count == other.count || id == AIR);
    }

    @Deprecated
    public final boolean deepEquals(Item item) {
        return equals(item, true);
    }

    @Deprecated
    public final boolean deepEquals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    @Deprecated
    public final boolean deepEquals(Item item, boolean checkDamage, boolean checkCompound) {
        return equals(item, checkDamage, checkCompound);
    }

    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.tags = this.tags != null ? this.tags.clone() : new byte[0];
//            item.cachedNBT = null;
            //TODO: 临时处理, 确保所有插件的nbt修改调用setCompoundTag后改回去 -- 02/27/2023
            item.cachedNBT = this.cachedNBT != null ? this.cachedNBT.copy() : null;
            if (this.canPlaceOnBlocks != null) {
                item.canPlaceOnBlocks = new ObjectOpenHashSet<>(this.canPlaceOnBlocks);
            }
            if (this.canDestroyBlocks != null) {
                item.canDestroyBlocks = new ObjectOpenHashSet<>(this.canDestroyBlocks);
            }
            item.initItem();
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public CompoundTag save() {
        return NBTIO.putItemHelper(this);
    }

    public CompoundTag save(int slot) {
        return NBTIO.putItemHelper(this, slot);
    }

    public static Item of(CompoundTag saved) {
        return NBTIO.getItemHelper(saved);
    }

    public int getFullId() {
        return getFullId(getId(), isStackedByData() ? getDamage() : 0);
    }

    public static int getFullId(int id) {
        return id << 16;
    }

    public static int getFullId(int id, int meta) {
        return getFullId(id) | meta;
    }

    public static int getIdFromFullId(int fullId) {
        return fullId >> 16;
    }

    public static int getMetaFromFullId(int fullId) {
        return fullId & 0xffff;
    }

    public static Item fromFullId(int fullId) {
        return get(getIdFromFullId(fullId), getMetaFromFullId(fullId));
    }

    public static Item fromFullId(int fullId, int count) {
        return get(getIdFromFullId(fullId), getMetaFromFullId(fullId), count);
    }

    public static Item fromFullId(int fullId, int count, byte[] tags) {
        return get(getIdFromFullId(fullId), getMetaFromFullId(fullId), count, tags);
    }

    public long asHash() {
        return toHash(id, meta);
    }

    public long asHashWithCount() {
        return toHash(id, meta, count);
    }

    public static long toHash(int id, int meta) {
        return (((long) id << 16) | (meta & 0xffffL)) << 7;
    }

    public static long toHash(int id, int meta, int count) {
        return ((((long) id << 16) | (meta & 0xffffL)) << 7) | (count & 0x7fL);
    }

    public static int getId(long hash) {
        return (int) (hash >> (16 + 7));
    }

    public static int getMeta(long hash) {
        return (int) (hash >> 7) & 0xffff;
    }

    public static int getCount(long hash) {
        return (int) hash & 0x7f;
    }

    public boolean shouldDespawn() {
        return true;
    }

    public boolean isFireResistant() {
        return false;
    }

    public boolean isExplodable() {
        return true;
    }

    public boolean isChemistryFeature() {
        return false;
    }

    public final Item getItem() {
        return this;
    }

    public boolean is(int id) {
        return getId() == id;
    }

    public boolean is(int id, int damage) {
        return getId() == id && getDamage() == damage;
    }

    public boolean is(Item item) {
        return getId() == item.getId();
    }

    public boolean is(Block block) {
        return getId() == block.getItemId();
    }

    public boolean isFertilizer() {
        return false;
    }

    public boolean isPotion() {
        return false;
    }

    public boolean isBook() {
        return false;
    }

    public boolean isMap() {
        return false;
    }

    public boolean isPotterySherd() {
        return false;
    }

    public boolean isBundle() {
        return false;
    }

    public boolean isHarness() {
        return false;
    }

    public boolean isEgg() {
        return false;
    }

    public boolean isRecord() {
        return false;
    }

    public boolean isShulkerBox() {
        return false;
    }

    public boolean isHangingSign() {
        return false;
    }

    public boolean isWool() {
        return false;
    }

    public boolean isBannerPattern() {
        return false;
    }

    public boolean isBoat() {
        return false;
    }

    public boolean isBucket() {
        return false;
    }

    public boolean isCoal() {
        return false;
    }

    public boolean isDye() {
        return false;
    }

    public boolean isSpawnEgg() {
        return false;
    }

    public boolean wasPickedUp() {
        return pickedUp;
    }

    public Item setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
        return this;
    }

    @Nullable
    public Set<String> getCanPlaceOnBlocks() {
        return canPlaceOnBlocks;
    }

    public Item setCanPlaceOnBlocks(@Nullable Set<String> canPlaceOnBlocks) {
        this.canPlaceOnBlocks = canPlaceOnBlocks;
        return this;
    }

    @Nullable
    public Set<String> getCanDestroyBlocks() {
        return canDestroyBlocks;
    }

    public Item setCanDestroyBlocks(@Nullable Set<String> canDestroyBlocks) {
        this.canDestroyBlocks = canDestroyBlocks;
        return this;
    }

    public int getItemStackId() {
        return itemStackId;
    }

    public Item setItemStackId(int itemStackId) {
        this.itemStackId = itemStackId;
        return this;
    }

    public boolean hasItemTag(String tag) {
        return Items.hasTag(tag, getId(), getDamage());
    }

    public Set<String> getItemTags() {
        return Items.getTags(getId(), getDamage());
    }

    public boolean isVanilla() {
        return true;
    }

    @Nullable
    public String getTrimPatternName() {
        return null;
    }

    @Nullable
    public String getTrimMaterialName() {
        return null;
    }

    public int getEquippingSound() {
        return -1;
    }

    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE;
    }

    public int getAttackHitSound() {
        return -1;
    }
}
