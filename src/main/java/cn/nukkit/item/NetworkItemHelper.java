package cn.nukkit.item;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Hash;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class NetworkItemHelper {
    public static final String SERVER_DATA_HASH = "__NK_rn_hash";

    private static final Set<String> ADDITIONAL_CLIENT_TAGS = new HashSet<>();
    private static final Set<String> DISPLAY_TAGS = Set.of("Name", "FilteredName", "Lore", "color");

    /**
     * 自定义客户端确实读取的根标签可在插件初始化阶段注册。
     */
    public static void registerClientTag(String name) {
        if (name == null || name.isEmpty() || SERVER_DATA_HASH.equals(name)) {
            throw new IllegalArgumentException("Invalid client item tag name");
        }
        ADDITIONAL_CLIENT_TAGS.add(name);
    }

    /**
     * 只生成网络视图，绝不将剥离结果写回真实物品或持久化库存。
     */
    public static Item project(Item item) {
        if (item == null || item.isNull()) {
            return item;
        }
        CompoundTag original = item.getNamedTag();
        if (original == null) {
            if (!(item instanceof ItemBundle)) {
                return item;
            }
            original = new CompoundTag();
        }
        if (!isDepthSafe(original, NBTIO.MAX_DEPTH)) {
            throw new IllegalArgumentException("Item NBT exceeds the supported network nesting depth");
        }
        boolean derivedWeight = false;
        if (item instanceof ItemBundle bundle && !original.contains(SERVER_DATA_HASH)) {
            int contentWeight = bundle.getContentWeight();
            if (contentWeight >= 0 && (!original.contains(ItemBundle.WEIGHT_TAG)
                    || original.getInt(ItemBundle.WEIGHT_TAG) != contentWeight + ItemBundle.BASE_WEIGHT)) {
                original = original.clone().putInt(ItemBundle.WEIGHT_TAG, contentWeight + ItemBundle.BASE_WEIGHT);
                derivedWeight = true;
            }
        }
        Projection projection = projectTag(original, item.isBundle());
        if (!projection.removed() && !derivedWeight && !original.contains(SERVER_DATA_HASH)) {
            return item;
        }
        Item projected = item.clone();
        projected.setNamedTag(projection.tag());
        return projected;
    }

    private static Projection projectTag(CompoundTag original, boolean bundle) {
        CompoundTag network = new CompoundTag();
        boolean removed = false;
        for (Map.Entry<String, Tag> entry : original.getTagsUnsafe().entrySet()) {
            String key = entry.getKey();
            if (CLIENT_TAGS.contains(key) || ADDITIONAL_CLIENT_TAGS.contains(key)) {
                if ("display".equals(key) && entry.getValue() instanceof CompoundTag display) {
                    CompoundTag visible = new CompoundTag();
                    for (Map.Entry<String, Tag> displayEntry : display.getTagsUnsafe().entrySet()) {
                        if (DISPLAY_TAGS.contains(displayEntry.getKey())) {
                            visible.put(displayEntry.getKey(), displayEntry.getValue().copy());
                        } else {
                            removed = true;
                        }
                    }
                    network.putCompound(key, visible);
                } else if ("chargedItem".equals(key) && entry.getValue() instanceof CompoundTag stack) {
                    Projection nested = projectStoredItem(stack);
                    network.putCompound(key, nested.tag());
                    removed |= nested.removed();
                } else if ("ChargedProjectiles".equals(key) && entry.getValue() instanceof ListTag<?> projectiles) {
                    ListTag<Tag> visible = new ListTag<>(projectiles.getName());
                    for (Tag projectile : projectiles.getAllUnsafe()) {
                        if (projectile instanceof CompoundTag stack) {
                            Projection nested = projectStoredItem(stack);
                            visible.add(nested.tag());
                            removed |= nested.removed();
                        } else {
                            visible.add(projectile.copy());
                        }
                    }
                    network.putList(key, visible);
                } else {
                    network.put(key, entry.getValue().copy());
                }
            } else if (!SERVER_DATA_HASH.equals(key)) {
                removed = true;
            }
        }
        // 客户端不能因隐藏数据被剥离而把两件服务端语义不同的物品堆叠。
        if (removed) {
            network.putLong(SERVER_DATA_HASH, hash(original, bundle));
        } else if (original.contains(SERVER_DATA_HASH)) {
            network.put(SERVER_DATA_HASH, original.get(SERVER_DATA_HASH).copy());
        }
        return new Projection(network, removed);
    }

    private static Projection projectStoredItem(CompoundTag stack) {
        CompoundTag projected = stack.clone();
        if (!(stack.get("tag") instanceof CompoundTag itemTag)) {
            return new Projection(projected, false);
        }
        // 只投影嵌套物品的 tag，保留原生 Name、Count、Damage 等存储字段及其类型。
        Projection nested = projectTag(itemTag, false);
        projected.putCompound("tag", nested.tag());
        return new Projection(projected, nested.removed());
    }

    private static boolean isDepthSafe(Tag tag, int remainingDepth) {
        if (tag instanceof CompoundTag compound) {
            if (--remainingDepth < 0) {
                return false;
            }
            for (Tag child : compound.getAllTags()) {
                if (!isDepthSafe(child, remainingDepth)) {
                    return false;
                }
            }
        } else if (tag instanceof ListTag<?> list) {
            if (--remainingDepth < 0) {
                return false;
            }
            for (Tag child : list.getAllUnsafe()) {
                if (!isDepthSafe(child, remainingDepth)) {
                    return false;
                }
            }
        }
        return true;
    }

    private record Projection(CompoundTag tag, boolean removed) {
    }

    public static boolean matches(Item serverItem, Item clientItem, boolean checkCount) {
        if (serverItem == null || clientItem == null) {
            return serverItem == clientItem;
        }
        Item projected = project(serverItem);
        if (serverItem.isBundle()) {
            projected = withoutDynamicId(projected);
            clientItem = withoutDynamicId(clientItem);
        }
        return checkCount ? projected.equalsExact(clientItem) : projected.equals(clientItem, true, true, true);
    }

    private static Item withoutDynamicId(Item item) {
        if (!item.hasCompoundTag() || !item.getNamedTag().contains("bundle_id")) {
            return item;
        }
        Item copy = item.clone();
        CompoundTag tag = copy.getNamedTag().clone();
        tag.remove("bundle_id");
        copy.setNamedTag(tag);
        return copy;
    }

    private static long hash(CompoundTag original, boolean bundle) {
        Map<String, Tag> values = new TreeMap<>();
        for (Map.Entry<String, Tag> entry : original.getTagsUnsafe().entrySet()) {
            if (!SERVER_DATA_HASH.equals(entry.getKey()) && !(bundle && ItemBundle.DYNAMIC_ID_TAG.equals(entry.getKey()))) {
                values.put(entry.getKey(), canonical(entry.getValue()));
            }
        }
        try {
            return Hash.xxh64(NBTIO.write(new CompoundTag(values), ByteOrder.LITTLE_ENDIAN));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to serialize item data for network identity", exception);
        }
    }

    private static Tag canonical(Tag source) {
        if (source instanceof CompoundTag compound) {
            Map<String, Tag> values = new TreeMap<>();
            for (Map.Entry<String, Tag> entry : compound.getTagsUnsafe().entrySet()) {
                values.put(entry.getKey(), canonical(entry.getValue()));
            }
            return new CompoundTag(source.getName(), values);
        }
        if (source instanceof ListTag<?> list) {
            ListTag<Tag> result = new ListTag<>(source.getName());
            for (Tag value : list.getAll()) {
                result.add(canonical(value));
            }
            return result;
        }
        // 写出基础标签不会修改其数据，地图图片等大数组无需为计算摘要再复制。
        return source;
    }

    private NetworkItemHelper() {
    }

    private static final Set<String> CLIENT_TAGS = Set.of(
            "display",
            "ench",
            "Damage",
            "RepairCost",
            "Unbreakable",
            "customColor",
            "map_uuid",
            "map_name_index",
            "map_display_players",
            "map_is_init",
            "map_scale",
            "map_is_scaling",
            "map_regenerate",
            "photo_uuid",
            "photo_name",
            "photo_owner",
            "bundle_id",
            "bundle_weight",
            "Patterns",
            "Base",
            "Type",
            "Trim",
            "sherds",
            "SkullOwner",
            "SkullType",
            "Rot",
            "pages",
            "title",
            "author",
            "generation",
            "resolved",
            "xuid",
            "photoName",
            "Fireworks",
            "FireworksItem",
            "chargedItem",
            "ChargedProjectiles",
            "Potion",
            "CustomPotionColor",
            "customPotionEffects",
            "trackingHandle",
            "LodestonePos",
            "LodestoneDimension",
            "minecraft:item_lock",
            "minecraft:keep_on_death",
            "CanPlaceOn",
            "CanDestroy"/*,
            // netease tags:
            "modvalue",
            "ModExtendValue",
            "ShowInHand",
            "ModId",
            "ModItemId",
            "ItemExtraID",
            "ModMaxDamage",
            "ModProtection",
            "ItemCustomTips",
            "ModMaxStackSize",
            "ModAttackDamage",
            "ModTierLevel",
            "ModTierSpeed"
            */
    );
}
