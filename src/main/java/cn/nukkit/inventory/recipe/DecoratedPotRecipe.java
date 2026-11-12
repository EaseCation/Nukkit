package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.transaction.FakeBlockTransactionHelper;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.Items;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DecoratedPotRecipe extends MultiRecipe {
    public DecoratedPotRecipe(UUID uuid) {
        super(uuid, RecipeTag.CRAFTING_TABLE, Item.get(ItemBlockID.DECORATED_POT));
    }

    @Override
    public int getGridIngredientCount() {
        return 4;
    }

    @Override
    public boolean supportsLegacyMatching() {
        return true;
    }

    @Override
    public boolean requiresCraftingTable() {
        return true;
    }

    @Override
    @Nullable
    public Item getResultForGrid(Item[] grid) {
        if (grid.length != 9) {
            return null;
        }
        for (int slot = 0; slot < 9; slot++) {
            boolean ingredient = slot == 1 || slot == 3 || slot == 5 || slot == 7;
            if (ingredient == grid[slot].isNull()) {
                return null;
            }
        }
        return super.getResultForGrid(grid);
    }

    @Override
    public List<Item> getResultsForGrid(Item[] grid) {
        Item result = getResultForGrid(grid);
        return result == null ? List.of() : List.of(result);
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid) {
        if (grid == null || Arrays.stream(grid).allMatch(Item::isNull)) {
            return getResults(inputs, repetitions);
        }
        if (grid.length != 9) {
            return List.of();
        }
        List<Item> ordered = new ArrayList<>(4);
        for (int slot = 0; slot < 9; slot++) {
            boolean ingredient = slot == 1 || slot == 3 || slot == 5 || slot == 7;
            if (ingredient ? grid[slot].getCount() < repetitions : !grid[slot].isNull()) {
                return List.of();
            }
            if (ingredient) {
                ordered.add(grid[slot].clone(repetitions));
            }
        }
        return FakeBlockTransactionHelper.matchesItems(inputs, ordered) ? getResults(ordered, repetitions) : List.of();
    }

    @Override
    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid) {
        List<Item> expected = getResults(inputs, repetitions, grid);
        return !expected.isEmpty() && FakeBlockTransactionHelper.matchesItems(expected, outputs);
    }

    @Override
    public boolean match(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, int multiplier) {
        if (!extraOutputList.isEmpty()) {
            return false;
        }
        if (multiplier < 1) {
            return false;
        }
        if (primaryOutput.getCount() != multiplier) {
            return false;
        }
        CompoundTag nbt = primaryOutput.getNamedTag();
        if (nbt == null || nbt.size() != 1) {
            return false;
        }
        //  A
        // B C => ABCD
        //  D
        Tag sherds = nbt.get("sherds");
        if (!(sherds instanceof ListTag<?> list) || list.type != Tag.TAG_String || list.size() != 4) {
            return false;
        }
        List<String> stringList = new ArrayList<>(4 * multiplier);
        for (int repetition = 0; repetition < multiplier; repetition++) {
            for (Object value : list.parseValue()) {
                stringList.add((String) value);
            }
        }
        int expected = 4 * multiplier;
        int total = 0;
        for (Item item : inputList) {
            int id = item.getId();
            if (id != Item.BRICK && !item.isPotterySherd()) {
                return false;
            }
            int count = item.getCount();
            total += count;
            if (total > expected) {
                return false;
            }
            String name = Items.getFullNameById(id);
            if (name == null) {
                return false;
            }
            for (int i = 0; i < count; i++) {
                if (!stringList.remove(name)) {
                    return false;
                }
            }
        }
        if (total != expected) {
            return false;
        }
        return stringList.isEmpty();
    }

    @Override
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        if (repetitions < 1 || repetitions > 256) {
            return null;
        }
        List<String> sides = new ArrayList<>(4);
        for (Item input : inputs) {
            if (input.isNull()) {
                continue;
            }
            if (input.getId() != Item.BRICK && !input.isPotterySherd()
                    || input.getCount() % repetitions != 0) {
                return null;
            }
            String name = Items.getFullNameById(input.getId());
            if (name == null) {
                return null;
            }
            int count = input.getCount() / repetitions;
            if (count > 4 - sides.size()) {
                return null;
            }
            for (int index = 0; index < count; index++) {
                sides.add(name);
            }
        }
        if (sides.size() != 4) {
            return null;
        }
        ListTag<StringTag> sherds = new ListTag<>("sherds");
        for (String side : sides) {
            sherds.add(new StringTag("", side));
        }
        Item output = getResult();
        output.setCount(repetitions);
        output.setNamedTag(new CompoundTag().putList(sherds));
        return output;
    }
}
