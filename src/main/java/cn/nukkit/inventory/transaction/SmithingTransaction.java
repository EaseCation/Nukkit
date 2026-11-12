package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.SmithingRecipe;
import cn.nukkit.inventory.SmithingTableInventory;
import cn.nukkit.inventory.recipe.RecipeIngredient;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SmithingTransaction extends InventoryTransaction {
    private final FakeBlockTransactionContext context;
    protected final Item baseItem;
    protected final Item materialItem;
    protected final Item templateItem;
    @Nullable
    protected Item outputItem;
    protected final SmithingRecipe smithingRecipe;
    protected final int repetitions;
    @Nullable
    private final SmithingTableInventory inventory;

    protected SmithingTransaction(Player source, SmithingRecipe recipe, int repetitions, Item base, Item material, Item template) {
        super(source);
        inventory = source.getCurrentWindow() instanceof SmithingTableInventory current ? current : null;
        context = new FakeBlockTransactionContext(inventory);
        smithingRecipe = recipe;
        this.repetitions = repetitions;
        baseItem = base.clone();
        materialItem = material.clone();
        templateItem = template.clone();
    }

    @Override
    public boolean canExecute() {
        outputItem = null;
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator() || actions.isEmpty()
                || inventory == null || source.getCurrentWindow() != inventory || repetitions < 1 || repetitions > 256
                || smithingRecipe.getTag() != RecipeTag.SMITHING_TABLE
                || !matchesIngredient(smithingRecipe.getBase(), baseItem)
                || !matchesIngredient(smithingRecipe.getIngredient(), materialItem)
                || !matchesIngredient(smithingRecipe.getTemplate(), templateItem)) {
            return false;
        }
        Item expected = createOutput();
        if (expected == null || expected.isNull()) {
            return false;
        }
        long count = (long) expected.getCount() * repetitions;
        if (count > Integer.MAX_VALUE) {
            return false;
        }
        expected.setCount((int) count);
        List<Item> inputs = new ObjectArrayList<>();
        List<ObjectBooleanPair<Item>> outputs = new ObjectArrayList<>();
        if (!matchItems(outputs, inputs)) {
            return false;
        }
        List<Item> consumed = List.of(consumed(baseItem, smithingRecipe.getBase()),
                consumed(materialItem, smithingRecipe.getIngredient()), consumed(templateItem, smithingRecipe.getTemplate()));
        if (!FakeBlockTransactionHelper.matches(inputs, outputs, consumed, List.of(expected))) {
            return false;
        }
        outputItem = expected;
        return true;
    }

    private boolean matchesIngredient(RecipeIngredient ingredient, Item item) {
        long count = (long) ingredient.getCount() * repetitions;
        return count == 0 ? item.isNull() : count > 0 && count <= item.getCount() && ingredient.accepts(item);
    }

    private Item consumed(Item item, RecipeIngredient ingredient) {
        Item consumed = item.clone();
        consumed.setCount(ingredient.getCount() * repetitions);
        return consumed;
    }

    @Nullable
    protected abstract Item createOutput();

    @Override
    protected boolean validateAuthoritativeContext() {
        return source.getCurrentWindow() == inventory && context.isCurrent();
    }

    @Override
    protected boolean callExecuteEvent() {
        //TODO
        return super.callExecuteEvent();
    }
}
