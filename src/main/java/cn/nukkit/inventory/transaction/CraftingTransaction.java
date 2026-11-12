package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.CraftItemCompleteEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.inventory.recipe.RecipeMatcher;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.UiContainerSlots;
import cn.nukkit.scheduler.Task;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This transaction type is specialized for crafting validation. It shares most of the same semantics of the base
 * inventory transaction type, but the requirement for validity is slightly different.
 * <p>
 * It is expected that the actions in this transaction type will produce an **unbalanced result**, i.e. some inputs won't
 * have corresponding outputs, and vice versa. The reason why is because the unmatched inputs are recipe inputs, and
 * the unmatched outputs are recipe results.
 * <p>
 * Therefore, the validity requirement is that the imbalance of the transaction should match the expected inputs and
 * outputs of a registered crafting recipe.
 * <p>
 * This transaction allows multiple repetitions of the same recipe to be crafted in a single batch. In the case of batch
 * crafting, the number of unmatched inputs and outputs must be exactly divisible by the expected recipe ingredients and
 * results, with no remainder. Any leftovers are expected to be emitted back to the crafting grid.
 *
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected final int gridSize;
    protected final RecipeTag recipeTag;

    @Nullable
    private final CraftingGrid craftingGrid;
    private final int craftingGridSlots;
    @Nullable
    private final Inventory craftingWindow;
    private final int craftingWindowSlots;

    protected List<Item> inputs;
    protected List<ObjectBooleanPair<Item>> outputs;

    protected List<Item> secondaryOutputs;

    protected Item primaryOutput;

    protected CraftingRecipe recipe;

    protected int repetitions;

    @Nullable
    private CraftItemEvent craftingEvent;

    public CraftingTransaction(Player source, CraftingRecipe recipe, int repetitions) {
        this(source, Collections.emptyList());
        this.recipe = recipe;
        this.repetitions = repetitions;
    }

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.craftingGrid = source.getCraftingGrid();
        this.craftingGridSlots = craftingGrid == null ? 0 : craftingGrid.getSize();
        this.craftingWindow = source.isServerAuthoritativeInventoryEnabled() ? source.getCurrentWindow() : null;
        this.craftingWindowSlots = craftingWindow == null ? 0 : craftingWindow.getSize();
        this.gridSize = source.isServerAuthoritativeInventoryEnabled()
                ? craftingWindow instanceof CraftingInventory ? 3 : 2
                : craftingGrid instanceof BigCraftingGrid ? 3 : 2;
        this.recipeTag = source.recipeTag;

        this.inputs = new ObjectArrayList<>();
        this.outputs = new ObjectArrayList<>();
        this.secondaryOutputs = new ObjectArrayList<>();

        this.repetitions = 1;

        init(actions);
    }

    public void setInput(Item item) {
        if (inputs.size() < gridSize * gridSize) {
            for (Item existingInput : this.inputs) {
                if (existingInput.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingInput.setCount(existingInput.getCount() + item.getCount());
                    return;
                }
            }
            inputs.add(item.clone());
        } else {
            throw new RuntimeException("Input list is full can't add " + item);
        }
    }

    public List<Item> getInputList() {
        return inputs;
    }

    public void setExtraOutput(Item item) {
        if (secondaryOutputs.size() < gridSize * gridSize) {
            secondaryOutputs.add(item.clone());
        } else {
            throw new RuntimeException("Output list is full can't add " + item);
        }
    }

    public Item getPrimaryOutput() {
        return primaryOutput;
    }

    public void setPrimaryOutput(Item item) {
        if (primaryOutput == null) {
            primaryOutput = item.clone();
        } else if (!primaryOutput.equals(item)) {
            throw new RuntimeException("Primary result item has already been set and does not match the current item (expected " + primaryOutput + ", got " + item + ")");
        }
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public int getRepetitions() {
        return repetitions;
    }

    protected static boolean matchIngredients(List<Item> providedItems, List<RecipeIngredient> recipeIngredients, int expectedIterations) {
        return !providedItems.isEmpty() && !recipeIngredients.isEmpty()
                && RecipeMatcher.matchIngredients(providedItems, recipeIngredients, expectedIterations);
    }

    protected static int matchOutputs(List<ObjectBooleanPair<Item>> txItems, List<Item> recipeItems) {
        if (recipeItems.isEmpty() || txItems.isEmpty()) {
            return -1;
        }

        int iterations = 0;
        boolean[] matchedRecipeItems = new boolean[recipeItems.size()];
        boolean[] matchedTransactionItems = new boolean[txItems.size()];
        for (int index = 0; index < recipeItems.size(); index++) {
            Item recipeItem = recipeItems.get(index);
            if (matchedRecipeItems[index] || recipeItem.isNull()) {
                continue;
            }
            long needCount = recipeItem.getCount();
            for (int otherIndex = index + 1; otherIndex < recipeItems.size(); otherIndex++) {
                Item otherItem = recipeItems.get(otherIndex);
                if (!matchedRecipeItems[otherIndex] && !otherItem.isNull() && otherItem.equals(recipeItem)) {
                    matchedRecipeItems[otherIndex] = true;
                    needCount += otherItem.getCount();
                }
            }

            long haveCount = 0;
            for (int transactionIndex = 0; transactionIndex < txItems.size(); transactionIndex++) {
                Item transactionItem = txItems.get(transactionIndex).left();
                if (!matchedTransactionItems[transactionIndex] && !transactionItem.isNull() && transactionItem.equals(recipeItem)) {
                    matchedTransactionItems[transactionIndex] = true;
                    haveCount += transactionItem.getCount();
                }
            }
            if (haveCount < needCount || haveCount % needCount != 0) {
                // wrong count for this output, should divide exactly
                return -1;
            }
            long multiplier = haveCount / needCount;
            if (multiplier > Integer.MAX_VALUE || iterations != 0 && multiplier != iterations) {
                // wrong count for this output, should match previous outputs
                // The multiplier must also fit in an int.
                return -1;
            }
            iterations = (int) multiplier;
        }
        for (int index = 0; index < txItems.size(); index++) {
            if (!matchedTransactionItems[index] && !txItems.get(index).left().isNull()) {
                return -1;
            }
        }
        return iterations > 0 ? iterations : -1;
    }

    @Override
    public boolean canExecute() {
        if (this.source.isSpectator() || !isCraftingContextCurrent()) {
            return false;
        }
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return canExecuteAuthoritativeCrafting();
        }

        recipe = source.getServer().getCraftingManager().matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs, recipeTag);
        if (this.recipe == null || !isRecipeAvailable(recipe) || !super.canExecute()) {
            return false;
        }
        int resultCount = recipe.getResultUnsafe().getCount();
        if (primaryOutput != null && resultCount > 0 && primaryOutput.getCount() % resultCount == 0) {
            repetitions = primaryOutput.getCount() / resultCount;
        }
        return true;
    }

    private boolean canExecuteAuthoritativeCrafting() {
        if (actions.isEmpty() || !matchItems(outputs, inputs) || outputs.isEmpty()) {
            return false;
        }
        if (recipe == null) {
            CraftingRecipe matchedRecipe = source.getServer().getCraftingManager().matchRecipeByOutputs(
                    outputs.getFirst().left(), candidate -> matchAuthoritativeRecipe(candidate) > 0);
            if (matchedRecipe == null) {
                return false;
            }
            int matchedRepetitions = matchAuthoritativeRecipe(matchedRecipe);
            if (matchedRepetitions <= 0) {
                return false;
            }
            recipe = matchedRecipe;
            repetitions = matchedRepetitions;
            return true;
        }
        return repetitions > 0 && matchAuthoritativeRecipe(recipe) == repetitions;
    }

    private int matchAuthoritativeRecipe(CraftingRecipe candidate) {
        if (!isRecipeAvailable(candidate)) {
            return -1;
        }
        if (candidate instanceof MultiRecipe multiRecipe) {
            if (!multiRecipe.isSupported() || outputs.size() != 1) {
                return -1;
            }
            Item output = outputs.getFirst().left();
            Item expected = candidate.getResultUnsafe();
            if (expected.isNull() || output.asHash() != expected.asHash()
                    || output.getCount() % expected.getCount() != 0) {
                return -1;
            }
            int matchedRepetitions = output.getCount() / expected.getCount();
            if (gridSize < 3 && multiRecipe.requiresCraftingTable(inputs, matchedRepetitions)) {
                return -1;
            }
            return multiRecipe.match(inputs, output, Collections.emptyList(), matchedRepetitions)
                    ? matchedRepetitions : -1;
        }
        if (candidate instanceof ShapelessUserDataRecipe userDataRecipe) {
            long outputCount = 0;
            for (ObjectBooleanPair<Item> output : outputs) {
                if (output.left().asHash() == candidate.getResultUnsafe().asHash()) {
                    outputCount += output.left().getCount();
                }
            }
            int resultCount = candidate.getResultUnsafe().getCount();
            if (resultCount <= 0 || outputCount <= 0 || outputCount % resultCount != 0
                    || outputCount / resultCount > Integer.MAX_VALUE) {
                return -1;
            }
            int matchedRepetitions = (int) (outputCount / resultCount);
            Item result = userDataRecipe.getResult(inputs, matchedRepetitions);
            if (result == null) {
                return -1;
            }
            List<Item> results = new ArrayList<>();
            results.add(result);
            for (Item extra : userDataRecipe.getExtraResults()) {
                long count = (long) extra.getCount() * matchedRepetitions;
                if (count <= 0 || count > Integer.MAX_VALUE) {
                    return -1;
                }
                Item output = extra.clone();
                output.setCount((int) count);
                results.add(output);
            }
            return matchOutputs(outputs, results) == 1 ? matchedRepetitions : -1;
        }
        // compute number of times recipe was crafted
        int matchedRepetitions = matchOutputs(outputs, candidate.getAllResults());
        // assert that repetitions x recipe ingredients should be consumed
        return matchedRepetitions > 0 && matchIngredients(inputs, candidate.getIngredientsAggregate(), matchedRepetitions)
                ? matchedRepetitions : -1;
    }

    private boolean isCraftingContextCurrent() {
        if (source.recipeTag != recipeTag || source.getCraftingGrid() != craftingGrid) {
            return false;
        }
        if (craftingGrid != null && (craftingGrid.getSize() != craftingGridSlots
                || craftingGridSlots != (craftingGrid instanceof BigCraftingGrid ? BigCraftingGrid.SIZE : CraftingGrid.SIZE))) {
            return false;
        }
        if (!source.isServerAuthoritativeInventoryEnabled()) {
            return true;
        }
        Inventory window = source.getCurrentWindow();
        if (window != craftingWindow || window != null && window.getSize() != craftingWindowSlots
                || window instanceof CraftingInventory && craftingWindowSlots != CraftingInventory.SIZE) {
            return false;
        }
        RecipeTag currentTag = window instanceof PlayerUIComponent component ? component.getRecipeTag() : RecipeTag.CRAFTING_TABLE;
        return currentTag == recipeTag;
    }

    private boolean isRecipeAvailable(CraftingRecipe candidate) {
        return candidate.getTag() == recipeTag && (!candidate.requiresCraftingTable() || gridSize == 3);
    }

    protected boolean requiresCraftingTable() {
        return recipe instanceof MultiRecipe multiRecipe
                ? multiRecipe.requiresCraftingTable(inputs, repetitions) : recipe.requiresCraftingTable();
    }

    private boolean isSelectedRecipeAvailable() {
        return recipe != null && isRecipeAvailable(recipe) && (gridSize == 3 || !requiresCraftingTable());
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        // 事件后的复核只检查合成环境，不能重新生成动态配方结果或地图 ID。
        return isCraftingContextCurrent() && isSelectedRecipeAvailable();
    }

    @Override
    protected boolean callExecuteEvent() {
        Item[] results;
        if (source.isServerAuthoritativeInventoryEnabled()) {
            results = outputs.stream().map(Pair::left).toArray(Item[]::new);
        } else {
            List<Item> legacyResults = new ArrayList<>(1 + secondaryOutputs.size());
            legacyResults.add(primaryOutput);
            legacyResults.addAll(secondaryOutputs);
            results = legacyResults.toArray(Item[]::new);
        }
        return callCraftItemEvent(inputs.toArray(Item[]::new), results)
                && isCraftingContextCurrent() && isSelectedRecipeAvailable();
    }

    protected final boolean callCraftItemEvent(Item[] eventInputs, Item[] eventOutputs) {
        craftingEvent = new CraftItemEvent(this, recipe, repetitions, eventInputs, eventOutputs);
        source.getServer().getPluginManager().callEvent(craftingEvent);
        return !craftingEvent.isCancelled();
    }

    @Override
    protected void onSuccessfulExecute() {
        if (craftingEvent != null) {
            source.getServer().getPluginManager().callEvent(new CraftItemCompleteEvent(craftingEvent));
        }
    }

    @Override
    protected void sendInventories() {
        super.sendInventories();

        if (source.craftingType == Player.CRAFTING_SMALL) {
            return; // Already closed
        }

        if (source.isServerAuthoritativeInventoryEnabled()) {
            return;
        }

        /*
         * TODO: HACK!
         * we can't resend the contents of the crafting window, so we force the client to close it instead.
         * So people don't whine about messy desync issues when someone cancels CraftItemEvent, or when a crafting
         * transaction goes wrong.
         */
        source.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                if (source.isOnline() && source.isAlive()) {
                    ContainerClosePacket pk = new ContainerClosePacket();
                    pk.windowId = ContainerIds.NONE;
                    pk.wasServerInitiated = true;
                    source.dataPacket(pk);
                }
            }
        }, 20);

        this.source.resetCraftingGridType();
    }

    public static boolean checkForCraftingPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (slotChangeAction.getInventory().getType() == InventoryType.UI && slotChangeAction.getSlot() == UiContainerSlots.CREATED_ITEM_OUTPUT &&
                        !slotChangeAction.getSourceItem().equals(slotChangeAction.getTargetItem())) {
                    return true;
                }
            }
        }
        return false;
    }
}
