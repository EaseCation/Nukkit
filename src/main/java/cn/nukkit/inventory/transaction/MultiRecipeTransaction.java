package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MultiRecipeTransaction extends CraftingTransaction {
    private final MultiRecipe multiRecipe;
    private final List<Item> consumedInputs;
    private final List<Item> computedResults;
    @Nullable
    private final Item[] grid;
    @Nullable
    private final Inventory gridInventory;
    @Nullable
    private final FakeBlockTransactionContext gridContext;

    public MultiRecipeTransaction(Player source, MultiRecipe recipe, int repetitions, List<Item> inputs) {
        this(source, recipe, repetitions, inputs, recipe.getResults(inputs, repetitions, null, source.isNetEaseClient()), null);
    }

    public MultiRecipeTransaction(Player source, MultiRecipe recipe, int repetitions, List<Item> inputs, List<Item> results) {
        this(source, recipe, repetitions, inputs, results, null);
    }

    public MultiRecipeTransaction(Player source, MultiRecipe recipe, int repetitions, List<Item> inputs, List<Item> results, @Nullable Item[] grid) {
        this(source, recipe, repetitions, inputs, results, grid, null);
    }

    public MultiRecipeTransaction(Player source, MultiRecipe recipe, int repetitions, List<Item> inputs, List<Item> results, @Nullable Item[] grid, @Nullable Inventory gridInventory) {
        super(source, recipe, repetitions);
        this.multiRecipe = recipe;
        this.consumedInputs = inputs.stream().map(Item::clone).toList();
        this.computedResults = results.stream().map(Item::clone).toList();
        this.grid = grid == null ? null : Arrays.stream(grid).map(Item::clone).toArray(Item[]::new);
        this.gridInventory = gridInventory;
        this.gridContext = gridInventory == null ? null : new FakeBlockTransactionContext(gridInventory);
    }

    @Override
    public boolean canExecute() {
        if (!validateAuthoritativeContext() || actions.isEmpty()
                || !multiRecipe.isSupported() || repetitions < 1 || repetitions > 256
                || !matchItems(outputs, inputs)) {
            return false;
        }
        List<Item> results = computedResults;
        return !results.isEmpty() && multiRecipe.match(consumedInputs, results, repetitions, grid, source.isNetEaseClient())
                && FakeBlockTransactionHelper.matches(inputs, outputs, consumedInputs, results);
    }

    @Override
    protected boolean requiresCraftingTable() {
        return multiRecipe.requiresCraftingTable(consumedInputs, repetitions);
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        return source.isServerAuthoritativeInventoryEnabled() && !source.isSpectator() && super.validateAuthoritativeContext()
                && (gridContext == null || gridContext.isCurrent()
                && (source.getCraftingGrid() == gridInventory || source.getCurrentWindow() == gridInventory));
    }

    @Override
    protected boolean callExecuteEvent() {
        List<Item> outputs = computedResults.stream().map(Item::clone).toList();
        if (outputs.isEmpty()) {
            return false;
        }
        return callCraftItemEvent(consumedInputs.toArray(Item[]::new), outputs.toArray(Item[]::new));
    }
}
