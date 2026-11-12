package cn.nukkit.inventory;

import cn.nukkit.inventory.recipe.DecoratedPotRecipe;
import cn.nukkit.inventory.recipe.BannerDuplicateRecipe;
import cn.nukkit.inventory.recipe.BannerAddPatternRecipe;
import cn.nukkit.inventory.recipe.BookCloningRecipe;
import cn.nukkit.inventory.recipe.FireworksRecipe;
import cn.nukkit.inventory.recipe.MapRecipe;
import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.inventory.recipe.RepairItemRecipe;
import cn.nukkit.inventory.recipe.ShieldRecipe;
import cn.nukkit.inventory.recipe.UnlockingContext;
import cn.nukkit.item.Item;
import cn.nukkit.inventory.transaction.FakeBlockTransactionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class MultiRecipe implements CraftingRecipe {
    private final UUID id;
    private final RecipeTag tag;
    @Nullable
    private final Item result;
    private int networkId;

    public MultiRecipe(UUID id) {
        this(id, RecipeTag.CRAFTING_TABLE, null);
    }

    protected MultiRecipe(UUID id, RecipeTag tag, @Nullable Item result) {
        this.id = id;
        this.tag = tag;
        this.result = result == null ? null : result.clone();
    }

    public static MultiRecipe create(UUID id) {
        return switch (id.toString()) {
            case "00000000-0000-0000-0000-000000000001" -> new RepairItemRecipe(id);
            case "d392b075-4ba1-40ae-8789-af868d56f6ce" -> new MapRecipe(id, RecipeTag.CRAFTING_TABLE, MapRecipe.Operation.EXTEND);
            case "8b36268c-1829-483c-a0f1-993b7156a8f2" -> new MapRecipe(id, RecipeTag.CARTOGRAPHY_TABLE, MapRecipe.Operation.EXTEND);
            case "602234e4-cac1-4353-8bb7-b1ebff70024b" -> new MapRecipe(id, RecipeTag.CARTOGRAPHY_TABLE, MapRecipe.Operation.LOCK);
            case "85939755-ba10-4d9d-a4cc-efb7a8e943c4" -> new MapRecipe(id, RecipeTag.CRAFTING_TABLE, MapRecipe.Operation.CLONE);
            case "442d85ed-8272-4543-a6f1-418f90ded05d" -> new MapRecipe(id, RecipeTag.CARTOGRAPHY_TABLE, MapRecipe.Operation.CLONE);
            case "aecd2294-4b94-434b-8667-4499bb2c9327" -> new MapRecipe(id, RecipeTag.CRAFTING_TABLE, MapRecipe.Operation.UPGRADE);
            case "98c84b38-1085-46bd-b1ce-dd38c159e6cc" -> new MapRecipe(id, RecipeTag.CARTOGRAPHY_TABLE, MapRecipe.Operation.UPGRADE);
            case "d1ca6b84-338e-4f2f-9c6b-76cc8b4bd98d" -> new BookCloningRecipe(id);
            case "b5c5d105-75a2-4076-af2b-923ea2bf4bf0" -> new BannerDuplicateRecipe(id);
            case "d81aaeaf-e172-4440-9225-868df030d27b" -> new BannerAddPatternRecipe(id);
            case "00000000-0000-0000-0000-000000000002" -> new FireworksRecipe(id);
            case "685a742a-c42e-4a4e-88ea-5eb83fc98e5b" -> new DecoratedPotRecipe(id);
            case "00000000-0000-0000-0000-0000000000c8" -> new ShieldRecipe(id);
            default -> new MultiRecipe(id);
        };
    }

    public boolean isSupported() {
        return result != null;
    }

    public boolean supportsLegacyMatching() {
        return false;
    }

    @Override
    public Item getResult() {
        return getResultUnsafe().clone();
    }

    @Override
    public Item getResultUnsafe() {
        if (result == null) {
            throw new UnsupportedOperationException("Multi recipe has no server-side implementation: " + id);
        }
        return result;
    }

    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        return null;
    }

    public List<Item> getResults(List<Item> inputs, int repetitions) {
        Item output = getResult(inputs, repetitions);
        return output == null ? List.of() : List.of(output);
    }

    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid) {
        return getResults(inputs, repetitions);
    }

    public List<Item> getResults(List<Item> inputs, int repetitions, @Nullable Item[] grid, boolean netEase) {
        return getResults(inputs, repetitions, grid);
    }

    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid) {
        return !outputs.isEmpty() && match(inputs, outputs.getFirst(), outputs.subList(1, outputs.size()), repetitions);
    }

    public boolean match(List<Item> inputs, List<Item> outputs, int repetitions, @Nullable Item[] grid, boolean netEase) {
        return match(inputs, outputs, repetitions, grid);
    }

    public int getGridIngredientCount() {
        return 0;
    }

    @Nullable
    public Item getResultForGrid(Item[] grid) {
        if (grid.length != 9) {
            return null;
        }
        List<Item> inputs = new ArrayList<>();
        for (Item item : grid) {
            if (!item.isNull()) {
                inputs.add(item.clone(1));
            }
        }
        return getResult(inputs, 1);
    }

    public List<Item> getResultsForGrid(Item[] grid) {
        if (grid.length != 9) {
            return List.of();
        }
        List<Item> inputs = new ArrayList<>();
        for (Item item : grid) {
            if (!item.isNull()) {
                inputs.add(item.clone(1));
            }
        }
        return getResults(inputs, 1);
    }

    public boolean match(List<Item> inputs, Item primaryOutput, List<Item> extraOutputs, int repetitions) {
        List<Item> expected = getResults(inputs, repetitions);
        if (expected.isEmpty()) {
            return false;
        }
        List<Item> outputs = new ArrayList<>(1 + extraOutputs.size());
        outputs.add(primaryOutput);
        outputs.addAll(extraOutputs);
        return FakeBlockTransactionHelper.matchesItems(expected, outputs);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerMultiRecipe(this);
        manager.registerNetworkRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.MULTI;
    }

    @Override
    public RecipeTag getTag() {
        return tag;
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        if (!this.id.equals(id)) {
            throw new IllegalArgumentException("Multi recipe UUID is fixed");
        }
    }

    @Override
    public String getRecipeId() {
        return id.toString();
    }

    @Override
    public boolean requiresCraftingTable() {
        return false;
    }

    public boolean requiresCraftingTable(List<Item> inputs, int repetitions) {
        if (requiresCraftingTable() || repetitions <= 0) {
            return true;
        }
        long units = 0;
        for (Item item : inputs) {
            if (!item.isNull()) {
                units += item.getCount();
            }
        }
        return units > 4L * repetitions;
    }

    @Override
    public List<Item> getExtraResults() {
        return List.of();
    }

    @Override
    public List<Item> getAllResults() {
        return isSupported() ? List.of(getResult()) : List.of();
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean matchItems(List<Item> inputs, List<Item> extraOutputs, int multiplier) {
        return false;
    }

    @Override
    public List<RecipeIngredient> getIngredientsAggregate() {
        return List.of();
    }

    @Override
    public UnlockingContext getUnlockingContext() {
        return UnlockingContext.ALWAYS_UNLOCKED;
    }

    @Override
    public List<RecipeIngredient> getUnlockingIngredientList() {
        return List.of();
    }
}
