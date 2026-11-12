package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.CartographyTableInventory;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.inventory.FakeBlockRecipeResult;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CartographyTransaction extends InventoryTransaction {
    private final FakeBlockTransactionContext context;
    @Nullable
    private final CartographyTableInventory inventory;
    @Nullable
    private final Recipe recipe;
    @Nullable
    private final String newCustomName;
    private final Item input;
    private final Item material;
    @Nullable
    private final FakeBlockRecipeResult result;

    public CartographyTransaction(Player source, @Nullable Recipe recipe, @Nullable String newCustomName, Item input, Item material) {
        this(source, recipe, newCustomName, input, material, CartographyTableInventory.getResult(input, material, recipe, newCustomName));
    }

    public CartographyTransaction(Player source, @Nullable Recipe recipe, @Nullable String newCustomName, Item input, Item material, @Nullable FakeBlockRecipeResult result) {
        super(source);
        this.inventory = source.getCurrentWindow() instanceof CartographyTableInventory window ? window : null;
        this.context = new FakeBlockTransactionContext(inventory);
        this.recipe = recipe;
        this.newCustomName = newCustomName;
        this.input = input.clone();
        this.material = material.clone();
        this.result = result;
    }

    @Override
    public boolean canExecute() {
        if (!validateAuthoritativeContext() || actions.isEmpty()) {
            return false;
        }
        if (result == null || !CartographyTableInventory.matchesResult(input, material, recipe, newCustomName, result)) {
            return false;
        }
        List<Item> inputs = new ArrayList<>();
        List<ObjectBooleanPair<Item>> outputs = new ArrayList<>();
        if (!matchItems(outputs, inputs)) {
            return false;
        }
        Item consumedInput = input.clone();
        consumedInput.setCount(result.inputCount());
        Item consumedMaterial = material.clone();
        consumedMaterial.setCount(result.materialCount());
        return FakeBlockTransactionHelper.matches(inputs, outputs, List.of(consumedInput, consumedMaterial), List.of(result.output()));
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        return source.isServerAuthoritativeInventoryEnabled() && !source.isSpectator()
                && inventory != null && source.getCurrentWindow() == inventory && context.isCurrent();
    }
}
