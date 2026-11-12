package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class RepairItemRecipe extends MultiRecipe {
    public RepairItemRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.IRON_SWORD));
    }

    @Override
    public int getGridIngredientCount() {
        return 2;
    }

    @Override
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        return getResult(inputs, repetitions, false);
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid, boolean netEase) {
        Item result = getResult(inputs, repetitions, netEase);
        return result == null ? List.of() : List.of(result);
    }

    @Override
    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid, boolean netEase) {
        Item expected = getResult(inputs, repetitions, netEase);
        return expected != null && outputs.size() == 1 && expected.equalsExact(outputs.getFirst());
    }

    @Nullable
    private Item getResult(List<Item> inputs, int repetitions, boolean netEase) {
        if (repetitions < 1 || repetitions > 256 || inputs.size() != 2) {
            return null;
        }
        Item first = inputs.getFirst();
        Item second = inputs.getLast();
        int durability = first.getMaxDurability();
        if (durability <= 0 || first.getId() != second.getId() || first.getCount() != repetitions
                || second.getCount() != repetitions || first.getMaxStackSize() != 1 || second.getMaxDurability() != durability) {
            return null;
        }
        int damage = Math.max(0, first.getDamage() + second.getDamage() - durability - durability * 5 / 100);
        Item result = Item.get(first.getId(), damage, repetitions);
        Int2ObjectMap<CompoundTag> curses = new Int2ObjectLinkedOpenHashMap<>();
        for (Item input : inputs) {
            CompoundTag inputTag = input.getNamedTag();
            if (inputTag == null) {
                continue;
            }
            for (CompoundTag entry : inputTag.getList("ench", CompoundTag.class).getAllUnsafe()) {
                int id = entry.getShort("id");
                int level = entry.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantment(id);
                if (enchantment == null || !enchantment.isCurse() || !enchantment.canEnchant(result) || level < enchantment.getMinLevel() || level > enchantment.getMaxLevel()) {
                    continue;
                }
                CompoundTag previous = curses.get(id);
                if (previous == null || previous.getShort("lvl") < level) {
                    CompoundTag curse = new CompoundTag().putShort("id", id).putShort("lvl", level);
                    if (netEase) {
                        curse.putString("modEnchant", entry.getString("modEnchant"));
                    }
                    curses.put(id, curse);
                }
            }
        }
        if (!curses.isEmpty()) {
            ListTag<CompoundTag> enchantments = new ListTag<>();
            for (CompoundTag curse : curses.values()) {
                enchantments.add(curse);
            }
            result.setNamedTag(new CompoundTag().putList("ench", enchantments));
        }
        return result;
    }
}
