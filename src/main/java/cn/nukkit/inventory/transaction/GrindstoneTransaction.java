package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.GrindstoneEvent;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.FakeBlockRecipeResult;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GrindstoneTransaction extends InventoryTransaction {
    private final FakeBlockTransactionContext context;
    @Nullable
    private final GrindstoneInventory inventory;
    private final Item inputItem;
    private final Item materialItem;
    @Nullable
    private FakeBlockRecipeResult result;
    private int experience;

    public GrindstoneTransaction(Player source, Item input, Item material) {
        super(source);
        inventory = source.getCurrentWindow() instanceof GrindstoneInventory current ? current : null;
        context = new FakeBlockTransactionContext(inventory);
        inputItem = input.clone();
        materialItem = material.clone();
    }

    @Override
    public boolean canExecute() {
        result = null;
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator()
                || inventory == null || source.getCurrentWindow() != inventory || actions.isEmpty()) {
            return false;
        }
        FakeBlockRecipeResult expected = GrindstoneInventory.calculateResult(inputItem, materialItem,
                source.isNetEaseClient());
        if (expected == null) {
            return false;
        }
        List<Item> inputs = new ObjectArrayList<>();
        List<ObjectBooleanPair<Item>> outputs = new ObjectArrayList<>();
        if (!matchItems(outputs, inputs)) {
            return false;
        }
        Item input = inputItem.clone();
        input.setCount(expected.inputCount());
        Item material = materialItem.clone();
        material.setCount(expected.materialCount());
        if (!FakeBlockTransactionHelper.matches(inputs, outputs, List.of(input, material), List.of(expected.output()))) {
            return false;
        }
        result = expected;
        return true;
    }

    @Override
    protected boolean validateAuthoritativeContext() {
        return source.getCurrentWindow() == inventory && context.isCurrent();
    }

    @Override
    protected boolean callExecuteEvent() {
        if (inventory == null || result == null) {
            return false;
        }
        int power = result.experience();
        int minimum = (power + 1) / 2;
        int reward = power == 0 ? 0 : ThreadLocalRandom.current().nextInt(minimum, power + 1);
        GrindstoneEvent event = new GrindstoneEvent(inventory, inputItem.clone(), result.output(), materialItem.clone(), reward, source);
        source.getServer().getPluginManager().callEvent(event);
        experience = Math.max(0, event.getExperienceDropped());
        return !event.isCancelled();
    }

    @Override
    protected void onAuthoritativeCommit() {
        if (experience > 0) {
            source.level.dropExpOrb(source, experience);
        }
    }
}
