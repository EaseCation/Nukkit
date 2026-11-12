package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTags;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import javax.annotation.Nullable;

public final class FakeBlockRecipeHelper {
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_SURVIVAL_COST = 39;

    private FakeBlockRecipeHelper() {
    }

    /**
     * 权威工作站同步客户端预测使用的两个名称字段，未提交改名时保留原始数据。
     */
    public static void applyCustomName(Item output, @Nullable String name) {
        if (name == null) {
            return;
        }
        CompoundTag tag = output.getOrCreateNamedTag();
        Tag displayTag = tag.get("display");
        if (name.isEmpty()) {
            if (displayTag instanceof CompoundTag display) {
                display.remove("Name").remove("FilteredName");
                if (display.isEmpty()) {
                    tag.remove("display");
                }
                output.setNamedTag(tag);
            }
            return;
        }
        CompoundTag display = displayTag instanceof CompoundTag compound ? compound : new CompoundTag("display");
        display.putString("Name", name).putString("FilteredName", name);
        output.setNamedTag(tag.putCompound("display", display));
    }

    @Nullable
    public static FakeBlockRecipeResult anvil(Item input, Item material, @Nullable String name, boolean creative) {
        if (input.isNull() || input.getCount() > input.getMaxStackSize()
                || name != null && name.length() > MAX_NAME_LENGTH
                || !hasValidRepairCost(input) || !material.isNull()
                && (material.getCount() > material.getMaxStackSize() || !hasValidRepairCost(material))) {
            return null;
        }
        Item output = input.clone();
        long operationCost = 0;
        long priorCost = input.getRepairCost();
        int materialCount = 0;
        if (!material.isNull()) {
            boolean enchantedBook = material.is(ItemID.ENCHANTED_BOOK) && material.hasEnchantments();
            if (input.getCount() != 1 && !enchantedBook) {
                return null;
            }
            priorCost += material.getRepairCost();
            if (input.getMaxDurability() > 0 && isRepairMaterial(input, material)) {
                int damage = input.getDamage();
                int repaired = Math.min(damage, input.getMaxDurability() / 4);
                if (repaired <= 0) {
                    return null;
                }
                while (repaired > 0 && materialCount < material.getCount()) {
                    damage -= repaired;
                    materialCount++;
                    repaired = Math.min(damage, input.getMaxDurability() / 4);
                }
                output.setDamage(damage);
                operationCost += materialCount;
            } else {
                if (!enchantedBook && (input.getMaxDurability() <= 0 || input.getId() != material.getId())) {
                    return null;
                }
                materialCount = 1;
                if (!enchantedBook) {
                    int damage = Math.max(0, input.getDamage() - input.getMaxDurability()
                            + material.getDamage() - input.getMaxDurability() * 12 / 100);
                    if (damage < output.getDamage()) {
                        output.setDamage(damage);
                        operationCost += 2;
                    }
                }
                boolean compatible = false;
                boolean incompatible = false;
                for (Enchantment enchantment : material.getEnchantments()) {
                    int oldLevel = output.getEnchantmentLevel(enchantment.getId());
                    int level = oldLevel == enchantment.getLevel() ? oldLevel + 1 : Math.max(oldLevel, enchantment.getLevel());
                    boolean allowed = enchantment.canEnchant(input) || input.is(ItemID.ENCHANTED_BOOK) || creative;
                    for (Enchantment existing : output.getEnchantments()) {
                        if (existing.getId() != enchantment.getId() && !enchantment.isCompatibleWith(existing)) {
                            allowed = false;
                            operationCost++;
                        }
                    }
                    if (!allowed) {
                        incompatible = true;
                        continue;
                    }
                    compatible = true;
                    level = Math.min(level, enchantment.getMaxLevel());
                    output.addEnchantment(enchantment.clone().setLevel(level, false));
                    int rarityCost = switch (enchantment.getRarity()) {
                        case COMMON -> 1;
                        case UNCOMMON -> 2;
                        case RARE -> 4;
                        case VERY_RARE -> 8;
                    };
                    if (enchantedBook) {
                        rarityCost = Math.max(1, rarityCost / 2);
                    }
                    operationCost += (long) rarityCost * Math.max(0, level - oldLevel);
                }
                if (incompatible && !compatible) {
                    return null;
                }
                if (input.getCount() > 1 && operationCost > 0) {
                    operationCost = MAX_SURVIVAL_COST + 1;
                }
            }
        }
        int renameCost = 0;
        if (name != null && !name.equals(input.getCustomName())) {
            applyCustomName(output, name);
            renameCost = 1;
            operationCost++;
        }
        if (operationCost == 0) {
            return null;
        }
        long cost = priorCost + operationCost;
        if (renameCost == operationCost && renameCost > 0) {
            cost = Math.min(cost, MAX_SURVIVAL_COST);
        }
        if (cost > Integer.MAX_VALUE || !creative && cost > MAX_SURVIVAL_COST) {
            return null;
        }
        long nextPriorCost = Math.max(input.getRepairCost(), material.isNull() ? 0 : material.getRepairCost());
        if (renameCost == 0 || operationCost != renameCost) {
            nextPriorCost = nextPriorCost * 2 + 1;
        }
        if (nextPriorCost > Integer.MAX_VALUE) {
            return null;
        }
        output.setRepairCost((int) nextPriorCost);
        return new FakeBlockRecipeResult(output, input.getCount(), materialCount, creative ? 0 : (int) cost, 0);
    }

    @Nullable
    public static FakeBlockRecipeResult grindstone(Item input, Item material) {
        return grindstone(input, material, false);
    }

    @Nullable
    public static FakeBlockRecipeResult grindstone(Item input, Item material, boolean netEase) {
        if (input.isNull() && material.isNull() || !input.isNull() && input.getCount() != 1
                || !material.isNull() && material.getCount() != 1) {
            return null;
        }
        Item base = input.isNull() ? material : input;
        Item second = input.isNull() ? input : material;
        if (!acceptsGrindstoneItem(base) || !second.isNull() && !acceptsGrindstoneItem(second)) {
            return null;
        }
        if (!second.isNull() && (base.getId() != second.getId() || base.getMaxDurability() <= 0)) {
            return null;
        }
        Item output = base.clone();
        if (!second.isNull()) {
            output.setDamage(Math.max(0, base.getDamage() - base.getMaxDurability()
                    + second.getDamage() - base.getMaxDurability() * 5 / 100));
        }
        Int2ObjectMap<CompoundTag> curses = new Int2ObjectLinkedOpenHashMap<>();
        int experience = 0;
        for (Item item : new Item[]{base, second}) {
            CompoundTag itemTag = item.getNamedTag();
            if (itemTag == null) {
                continue;
            }
            for (CompoundTag entry : itemTag.getList("ench", CompoundTag.class).getAllUnsafe()) {
                int id = entry.getShort("id");
                int level = entry.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantment(id);
                if (enchantment == null || level <= 0) {
                    continue;
                }
                enchantment.setLevel(level, false);
                if (enchantment.isCurse()) {
                    // 原生按 id 和 level 去重，保留首次出现的条目及其网易附加数据。
                    curses.putIfAbsent(id << 16 | level & 0xffff, entry);
                } else {
                    experience += Math.max(0, enchantment.getMinEnchantAbility(enchantment.getValidLevel()));
                }
            }
        }
        if (curses.size() > 2) {
            // 目标原生在三条时读取未初始化成本表项，超过三条时明确无产物。
            // 服务端对这些异常等级组合统一拒绝，不猜测或复刻未定义的 RepairCost。
            return null;
        }
        output.removeEnchantments();
        if (base.is(ItemID.ENCHANTED_BOOK) && curses.isEmpty()) {
            output = Item.get(ItemID.BOOK);
            output.setCompoundTag(base.getCompoundTag());
            output.setCanPlaceOnBlocks(base.getCanPlaceOnBlocks());
            output.setCanDestroyBlocks(base.getCanDestroyBlocks());
            output.removeEnchantments();
        }
        ListTag<CompoundTag> retained = new ListTag<>();
        IntSet retainedIds = new IntOpenHashSet();
        for (CompoundTag curse : curses.values()) {
            int id = curse.getShort("id");
            int level = curse.getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantment(id);
            if ((!output.is(ItemID.ENCHANTED_BOOK) && !enchantment.canEnchant(output))
                    || level < enchantment.getMinLevel() || level > enchantment.getMaxLevel() || !retainedIds.add(id)) {
                continue;
            }
            CompoundTag entry = new CompoundTag().putShort("id", id).putShort("lvl", level);
            if (netEase) {
                entry.putString("modEnchant", curse.getString("modEnchant"));
            }
            retained.add(entry);
        }
        if (!retained.isEmpty()) {
            output.setNamedTag(output.getOrCreateNamedTag().putList("ench", retained));
        }
        // 原生成本取收集到的不同诅咒条目数，不能改成成功加回后的条目数。
        int repairCost = (1 << curses.size()) - 1;
        // 原生砂轮始终写入显式的 RepairCost，数值为 0 时也不能删掉该标签。
        output.setNamedTag(output.getOrCreateNamedTag().putInt("RepairCost", repairCost));
        if (second.isNull() && output.equalsExact(base)) {
            return null;
        }
        return new FakeBlockRecipeResult(output, input.isNull() ? 0 : 1, material.isNull() ? 0 : 1, 0, experience);
    }

    private static boolean acceptsGrindstoneItem(Item item) {
        return item.hasEnchantments() || !item.isUnbreakable() && item.getMaxDurability() > 0;
    }

    private static boolean hasValidRepairCost(Item item) {
        CompoundTag nbt = item.getNamedTag();
        Tag tag = nbt == null ? null : nbt.get("RepairCost");
        return tag == null || tag instanceof IntTag repairCost && repairCost.data >= 0;
    }

    private static boolean isRepairMaterial(Item input, Item material) {
        int inputId = input.getId();
        int materialId = material.getId();

        switch (inputId) {
            case ItemID.ELYTRA -> {
                return materialId == ItemID.PHANTOM_MEMBRANE;
            }
            case ItemID.TURTLE_HELMET -> {
                return materialId == ItemID.TURTLE_SCUTE;
            }
            case ItemID.SHIELD -> {
                return material.hasItemTag(ItemTags.PLANKS);
            }
            case ItemID.WOLF_ARMOR -> {
                return materialId == ItemID.ARMADILLO_SCUTE;
            }
            case ItemID.MACE -> {
                return materialId == ItemID.BREEZE_ROD;
            }
        }
        if (input instanceof ItemTool tool) {
            return switch (tool.getTier()) {
                case ItemTool.TIER_WOODEN -> material.hasItemTag(ItemTags.PLANKS);
                case ItemTool.TIER_STONE -> materialId == ItemID.COBBLESTONE || materialId == ItemID.BLACKSTONE || materialId == ItemID.COBBLED_DEEPSLATE;
                case ItemTool.TIER_COPPER -> materialId == ItemID.COPPER_INGOT;
                case ItemTool.TIER_IRON -> materialId == ItemID.IRON_INGOT;
                case ItemTool.TIER_GOLD -> materialId == ItemID.GOLD_INGOT;
                case ItemTool.TIER_DIAMOND -> materialId == ItemID.DIAMOND;
                case ItemTool.TIER_NETHERITE -> materialId == ItemID.NETHERITE_INGOT;
                default -> false;
            };
        } else if (input instanceof ItemArmor armor) {
            return switch (armor.getTier()) {
                case ItemArmor.TIER_LEATHER -> materialId == ItemID.LEATHER;
                case ItemArmor.TIER_COPPER -> materialId == ItemID.COPPER_INGOT;
                case ItemArmor.TIER_CHAIN, ItemArmor.TIER_IRON -> materialId == ItemID.IRON_INGOT;
                case ItemArmor.TIER_GOLD -> materialId == ItemID.GOLD_INGOT;
                case ItemArmor.TIER_DIAMOND -> materialId == ItemID.DIAMOND;
                case ItemArmor.TIER_NETHERITE -> materialId == ItemID.NETHERITE_INGOT;
                default -> false;
            };
        }
        return false;
    }
}
