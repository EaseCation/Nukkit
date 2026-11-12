package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.FakeBlockRecipeHelper;
import cn.nukkit.inventory.transaction.FakeBlockTransactionHelper;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEmptyMap;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemMap;
import cn.nukkit.item.ItemBlockID;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public final class MapRecipe extends MultiRecipe {
    private final Operation operation;

    public MapRecipe(UUID uuid, RecipeTag tag, Operation operation) {
        super(uuid, tag, Item.get(ItemID.FILLED_MAP));
        this.operation = operation;
    }

    @Override
    public int getGridIngredientCount() {
        if (operation == Operation.EXTEND && getTag() == RecipeTag.CRAFTING_TABLE) {
            return 9;
        }
        return operation == Operation.CLONE && getTag() == RecipeTag.CRAFTING_TABLE ? 0 : 2;
    }

    @Override
    public boolean requiresCraftingTable() {
        return operation == Operation.EXTEND && getTag() == RecipeTag.CRAFTING_TABLE;
    }

    @Nullable
    public Item getResult(Item input, Item material, int repetitions) {
        int requiredMaterial = operation == Operation.EXTEND && getTag() == RecipeTag.CRAFTING_TABLE ? 8 : 1;
        if (repetitions < 1 || repetitions > 256 || input.getCount() < repetitions || material.getCount() < requiredMaterial * repetitions) {
            return null;
        }
        return createOutput(input.clone(repetitions), material.clone(requiredMaterial * repetitions), repetitions, null);
    }

    public boolean matchesResult(Item input, Item material, Item output, @Nullable String name) {
        int materialCount = operation == Operation.EXTEND && getTag() == RecipeTag.CRAFTING_TABLE ? 8 : 1;
        if (input.isNull() || material.getCount() < materialCount) {
            return false;
        }
        Long id = operation == Operation.LOCK || operation == Operation.EXTEND
                ? output instanceof ItemMap map ? map.getMapId() : 0L : null;
        Item expected = createOutput(input.clone(1), material.clone(materialCount), 1, id);
        if (expected == null) {
            return false;
        }
        FakeBlockRecipeHelper.applyCustomName(expected, name);
        return expected.equalsExact(output);
    }

    @Nullable
    private Item createOutput(Item input, Item material, int repetitions, @Nullable Long newMapId) {
        if (repetitions < 1 || repetitions > 256 || input.getCount() != repetitions) {
            return null;
        }
        Item output = input.clone();
        switch (operation) {
            case CLONE -> {
                if (!(input instanceof ItemMap) || material.getId() != ItemID.EMPTY_MAP
                        || material.getCount() % repetitions != 0 || material.getCount() < repetitions
                        || material.getCount() > repetitions * (getTag() == RecipeTag.CRAFTING_TABLE ? 8 : 1)) {
                    return null;
                }
                output.setCount(repetitions + material.getCount());
            }
            case UPGRADE -> {
                if (!input.isMap() || material.getId() != ItemID.COMPASS || material.getCount() != repetitions) {
                    return null;
                }
                if (input instanceof ItemMap map) {
                    if (map.isMapDisplayPlayers() || map.getDamage() == ItemMap.LOCKED_MAP) {
                        return null;
                    }
                    output.setDamage(ItemMap.ENHANCED_MAP);
                    ((ItemMap) output).setMapDisplayPlayers(true);
                } else {
                    if (input.getDamage() == ItemEmptyMap.ENHANCED_EMPTY_MAP) {
                        return null;
                    }
                    output.setDamage(ItemEmptyMap.ENHANCED_EMPTY_MAP);
                }
                output.setCount(repetitions);
            }
            case LOCK -> {
                if (!(input instanceof ItemMap) || input.getDamage() == ItemMap.LOCKED_MAP
                        || material.getId() != ItemBlockID.GLASS_PANE || material.getCount() != repetitions) {
                    return null;
                }
                output.setDamage(ItemMap.LOCKED_MAP);
                output.setCount(repetitions);
            }
            case EXTEND -> {
                int papers = getTag() == RecipeTag.CRAFTING_TABLE ? 8 : 1;
                if (!(input instanceof ItemMap map) || map.getMapScale() >= ItemMap.MAX_SCALE
                        || map.getDamage() >= ItemMap.OCEAN_EXPLORER_MAP
                        || material.getId() != ItemID.PAPER || material.getCount() != papers * repetitions) {
                    return null;
                }
                ((ItemMap) output).setMapScale(map.getMapScale() + 1);
            }
        }
        if (operation == Operation.LOCK || operation == Operation.EXTEND) {
            long id = newMapId == null ? ItemMap.getNewMapId() : newMapId;
            if (id == 0 || id == ((ItemMap) input).getMapId()) {
                return null;
            }
            ((ItemMap) output).setMapId(id);
            output.setNamedTag(output.getOrCreateNamedTag().putBoolean("map_is_init", true));
        }
        return output;
    }

    @Override
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        return outputFromInputs(inputs, repetitions, null);
    }

    @Nullable
    private Item outputFromInputs(List<Item> inputs, int repetitions, @Nullable Long mapId) {
        Item input = null;
        Item material = null;
        for (Item item : inputs) {
            if (item.isNull()) {
                continue;
            }
            if (item instanceof ItemMap || operation == Operation.UPGRADE && item.getId() == ItemID.EMPTY_MAP) {
                if (input != null && !input.equals(item, true, true, true)) {
                    return null;
                }
                input = input == null ? item.clone() : input.clone(input.getCount() + item.getCount());
            } else {
                if (material != null && !material.equals(item, true, true, true)) {
                    return null;
                }
                material = material == null ? item.clone() : material.clone(material.getCount() + item.getCount());
            }
        }
        return input == null || material == null ? null : createOutput(input, material, repetitions, mapId);
    }

    @Override
    public List<Item> getResultsForGrid(Item[] grid) {
        if (grid.length != 9) {
            return List.of();
        }
        if (operation == Operation.EXTEND && getTag() == RecipeTag.CRAFTING_TABLE) {
            for (int slot = 0; slot < 9; slot++) {
                if (slot == 4 ? !(grid[slot] instanceof ItemMap) : grid[slot].getId() != ItemID.PAPER) {
                    return List.of();
                }
            }
        }
        return super.getResultsForGrid(grid);
    }

    @Override
    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid) {
        return matchesLayout(inputs, repetitions, grid) ? getResults(inputs, repetitions) : List.of();
    }

    @Override
    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid) {
        return matchesLayout(inputs, repetitions, grid) && super.match(inputs, outputs, repetitions, grid);
    }

    private boolean matchesLayout(List<Item> inputs, int repetitions, @Nullable Item[] grid) {
        if (operation != Operation.EXTEND || getTag() != RecipeTag.CRAFTING_TABLE || grid == null
                || Arrays.stream(grid).allMatch(Item::isNull)) {
            return true;
        }
        if (grid.length != 9) {
            return false;
        }
        List<Item> expected = new ArrayList<>(9);
        for (int slot = 0; slot < 9; slot++) {
            Item item = grid[slot];
            if (item.getCount() < repetitions || (slot == 4 ? !(item instanceof ItemMap) : item.getId() != ItemID.PAPER)) {
                return false;
            }
            expected.add(item.clone(repetitions));
        }
        return FakeBlockTransactionHelper.matchesItems(inputs, expected);
    }

    @Override
    public boolean match(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList, int multiplier) {
        if (!extraOutputList.isEmpty()) {
            return false;
        }
        Long mapId = operation == Operation.LOCK || operation == Operation.EXTEND
                ? primaryOutput instanceof ItemMap map ? map.getMapId() : 0L : null;
        Item output = outputFromInputs(inputList, multiplier, mapId);
        return output != null && output.equalsExact(primaryOutput);
    }

    public enum Operation {
        EXTEND,
        LOCK,
        CLONE,
        UPGRADE,
    }
}
