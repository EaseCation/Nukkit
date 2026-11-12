package cn.nukkit.item;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class ItemBundle extends Item {
    public static final int MAX_STORAGE_SLOTS = 64;
    public static final int MAX_CONTENT_WEIGHT = 64;
    public static final int BASE_WEIGHT = 4;
    public static final int MAX_NESTING_DEPTH = 16;
    // 玩家存档的根 Compound、Inventory List、物品 Compound 位于物品自身 tag 之外。
    private static final int PLAYER_NBT_WRAPPER_DEPTH = 3;

    public static final String CONTENTS_TAG = "storage_item_component_content";
    public static final String WEIGHT_TAG = "bundle_weight";
    public static final String DYNAMIC_ID_TAG = "bundle_id";

    public ItemBundle() {
        this(0, 1);
    }

    public ItemBundle(Integer meta) {
        this(meta, 1);
    }

    public ItemBundle(Integer meta, int count) {
        super(BUNDLE, meta, count, "Bundle");
    }

    protected ItemBundle(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getDescriptionId() {
        return "item." + getShortName();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public ItemBundle setBundleWeight(int weight) {
        setNamedTag(getOrCreateNamedTag().putInt(WEIGHT_TAG, weight));
        return this;
    }

    public int getBundleWeight() {
        CompoundTag tag = getNamedTag();
        return tag == null ? BASE_WEIGHT : tag.getInt(WEIGHT_TAG, BASE_WEIGHT);
    }

    @Nullable
    public ListTag<CompoundTag> getBundleContents() {
        CompoundTag tag = getNamedTag();
        return tag == null ? null : tag.getList(CONTENTS_TAG, (ListTag<CompoundTag>) null);
    }

    /**
     * 返回独立的槽位快照；非法内容、重量、槽位或嵌套返回空引用。
     */
    @Nullable
    public Int2ObjectMap<Item> getContents() {
        Contents decoded = readContents(0, Collections.newSetFromMap(new IdentityHashMap<>()));
        return decoded == null ? null : decoded.items();
    }

    @Nullable
    private Contents readContents(int depth, Set<ItemBundle> visiting) {
        if (depth > MAX_NESTING_DEPTH || getCount() != 1 || !visiting.add(this)) {
            return null;
        }
        try {
            CompoundTag tag = getNamedTag();
            if (!isSafeTag(tag)) {
                return null;
            }
            Int2ObjectMap<Item> contents = new Int2ObjectOpenHashMap<>();
            if (tag == null) {
                return new Contents(contents, 0);
            }
            Tag encoded = tag.get(CONTENTS_TAG);
            if (encoded != null) {
                if (!(encoded instanceof ListTag<?> list) || list.size() > MAX_STORAGE_SLOTS
                        || !list.isEmpty() && list.type != Tag.TAG_Compound) {
                    return null;
                }
                for (Tag entry : list.getAllUnsafe()) {
                    if (!(entry instanceof CompoundTag stack) || !(stack.get("Slot") instanceof ByteTag slotTag)) {
                        return null;
                    }
                    int slot = slotTag.data & 0xff;
                    if (slot >= MAX_STORAGE_SLOTS || contents.containsKey(slot)) {
                        return null;
                    }
                    // 升级器可能改写物品存档，不能把收纳袋持有的原始 NBT 传入。
                    Item item = NBTIO.getItemHelper(stack.clone());
                    if (item == null || item.isNull()) {
                        return null;
                    }
                    contents.put(slot, item);
                }
            }
            int contentWeight = contentWeight(contents, depth, visiting);
            Tag encodedWeight = tag.get(WEIGHT_TAG);
            if (contentWeight < 0 || encodedWeight != null
                    && (!(encodedWeight instanceof IntTag weight) || weight.data != BASE_WEIGHT + contentWeight)) {
                return null;
            }
            Tag dynamicId = tag.get(DYNAMIC_ID_TAG);
            if (dynamicId != null && !(dynamicId instanceof IntTag)) {
                return null;
            }
            return new Contents(contents, contentWeight);
        } finally {
            visiting.remove(this);
        }
    }

    public boolean setContents(Int2ObjectMap<Item> contents) {
        if (getCount() != 1) {
            return false;
        }
        Set<ItemBundle> visiting = Collections.newSetFromMap(new IdentityHashMap<>());
        visiting.add(this);
        int weight = contentWeight(contents, 0, visiting);
        CompoundTag current = getNamedTag();
        if (weight < 0 || !isSafeTag(current) || current != null && current.get(DYNAMIC_ID_TAG) != null
                && !(current.get(DYNAMIC_ID_TAG) instanceof IntTag)) {
            return false;
        }
        ListTag<CompoundTag> encoded = new ListTag<>(CONTENTS_TAG);
        for (int slot = 0; slot < MAX_STORAGE_SLOTS; slot++) {
            Item item = contents.get(slot);
            if (item == null || item.isNull()) {
                continue;
            }
            CompoundTag stack = NBTIO.putItemHelper(item.clone(), slot).clone();
            stack.putByte("Slot", slot);
            encoded.add(stack);
        }
        CompoundTag tag = current == null ? new CompoundTag() : current.clone();
        tag.putList(CONTENTS_TAG, encoded).putInt(WEIGHT_TAG, BASE_WEIGHT + weight);
        if (!isSafeTag(tag)) {
            return false;
        }
        setNamedTag(tag);
        return true;
    }

    public int getContentWeight() {
        Contents decoded = readContents(0, Collections.newSetFromMap(new IdentityHashMap<>()));
        return decoded == null ? -1 : decoded.weight();
    }

    public int getRemainingCapacity(Item item) {
        int used = getContentWeight();
        int unitWeight = itemWeight(item, 0, Collections.newSetFromMap(new IdentityHashMap<>()));
        if (used < 0 || unitWeight <= 0 || item.getCount() <= 0) {
            return 0;
        }
        unitWeight /= item.getCount();
        return unitWeight <= 0 ? 0 : (MAX_CONTENT_WEIGHT - used) / unitWeight;
    }

    /**
     * 插入指定数量并返回实际插入数；调用者应在收纳袋副本上构建事务。
     */
    public int insert(Item item, int count) {
        Int2ObjectMap<Item> contents = getContents();
        if (contents == null || count <= 0 || item.isNull() || item == this) {
            return 0;
        }
        int accepted = Math.min(Math.min(count, item.getCount()), getRemainingCapacity(item));
        int remaining = accepted;
        for (int slot = 0; slot < MAX_STORAGE_SLOTS && remaining > 0; slot++) {
            Item existing = contents.get(slot);
            if (existing == null || !existing.equals(item, true, true, true)) {
                continue;
            }
            int added = Math.min(remaining, existing.getMaxStackSize() - existing.getCount());
            existing.setCount(existing.getCount() + added);
            remaining -= added;
        }
        for (int slot = 0; slot < MAX_STORAGE_SLOTS && remaining > 0; slot++) {
            if (contents.containsKey(slot)) {
                continue;
            }
            Item added = item.clone();
            added.setCount(Math.min(remaining, item.getMaxStackSize()));
            contents.put(slot, added);
            remaining -= added.getCount();
        }
        return accepted > remaining && setContents(contents) ? accepted - remaining : 0;
    }

    @Nullable
    public Item remove(int slot, int count) {
        Int2ObjectMap<Item> contents = getContents();
        if (contents == null || slot < 0 || slot >= MAX_STORAGE_SLOTS || count <= 0) {
            return null;
        }
        Item existing = contents.get(slot);
        if (existing == null || existing.getCount() < count) {
            return null;
        }
        Item removed = existing.clone();
        removed.setCount(count);
        existing.setCount(existing.getCount() - count);
        if (existing.isNull()) {
            contents.remove(slot);
        }
        return setContents(contents) ? removed : null;
    }

    public ItemBundle getShell() {
        ItemBundle shell = (ItemBundle) clone();
        CompoundTag tag = shell.getNamedTag();
        if (tag != null) {
            shell.setNamedTag(tag.remove(CONTENTS_TAG).remove(WEIGHT_TAG).remove(DYNAMIC_ID_TAG));
        }
        return shell;
    }

    public ItemBundle setDynamicId(int id) {
        setNamedTag(getOrCreateNamedTag().putInt(DYNAMIC_ID_TAG, id));
        return this;
    }

    public int getDynamicId() {
        CompoundTag tag = getNamedTag();
        return tag == null ? -1 : tag.getInt(DYNAMIC_ID_TAG, -1);
    }

    private static int contentWeight(Int2ObjectMap<Item> contents, int depth, Set<ItemBundle> visiting) {
        if (contents.size() > MAX_STORAGE_SLOTS) {
            return -1;
        }
        int weight = 0;
        for (Int2ObjectMap.Entry<Item> entry : contents.int2ObjectEntrySet()) {
            if (entry.getIntKey() < 0 || entry.getIntKey() >= MAX_STORAGE_SLOTS || entry.getValue() == null) {
                return -1;
            }
            Item item = entry.getValue();
            if (item.getCount() < 0) {
                return -1;
            }
            if (item.isNull()) {
                continue;
            }
            int itemWeight = itemWeight(item, depth, visiting);
            if (itemWeight < 0 || itemWeight > MAX_CONTENT_WEIGHT - weight) {
                return -1;
            }
            weight += itemWeight;
        }
        return weight;
    }

    private static int itemWeight(Item item, int depth, Set<ItemBundle> visiting) {
        if (item.isNull() || item.isShulkerBox() || !isSafeTag(item.getNamedTag())) {
            return -1;
        }
        if (item instanceof ItemBundle bundle) {
            Contents nested = bundle.readContents(depth + 1, visiting);
            return nested == null ? -1 : BASE_WEIGHT + nested.weight();
        }
        int maximum = item.getMaxStackSize();
        if (maximum != 1 && maximum != 16 && maximum != 64 || item.getCount() > maximum) {
            return -1;
        }
        return MAX_CONTENT_WEIGHT / maximum * item.getCount();
    }

    private static boolean isSafeTag(@Nullable Tag tag) {
        return tag == null || isSafeTag(tag, NBTIO.MAX_DEPTH - PLAYER_NBT_WRAPPER_DEPTH, Collections.newSetFromMap(new IdentityHashMap<>()), new int[]{4096});
    }

    private static boolean isSafeTag(Tag tag, int remainingDepth, Set<Tag> visiting, int[] remaining) {
        if ((tag instanceof CompoundTag || tag instanceof ListTag<?>) && --remainingDepth < 0) {
            return false;
        }
        if (--remaining[0] < 0 || !visiting.add(tag)) {
            return false;
        }
        try {
            if (tag instanceof CompoundTag compound) {
                for (Tag child : compound.getAllTags()) {
                    if (!isSafeTag(child, remainingDepth, visiting, remaining)) {
                        return false;
                    }
                }
            } else if (tag instanceof ListTag<?> list) {
                for (Tag child : list.getAllUnsafe()) {
                    if (!isSafeTag(child, remainingDepth, visiting, remaining)) {
                        return false;
                    }
                }
            }
            return true;
        } finally {
            visiting.remove(tag);
        }
    }

    @Override
    public boolean isBundle() {
        return true;
    }

    private record Contents(Int2ObjectMap<Item> items, int weight) {
    }
}
