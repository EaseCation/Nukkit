package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.List;

public class LoomTransaction extends InventoryTransaction {
    private final FakeBlockTransactionContext context;
    @Nullable
    private final LoomInventory inventory;
    private final Item inputBannerItem;
    private final Item dyeItem;
    private final Item patternItem;
    private final String patternId;
    private final int repetitions;

    public LoomTransaction(Player source, String patternId, int repetitions, Item banner, Item dye, Item pattern) {
        super(source);
        inventory = source.getCurrentWindow() instanceof LoomInventory current ? current : null;
        context = new FakeBlockTransactionContext(inventory);
        this.patternId = patternId;
        this.repetitions = repetitions;
        inputBannerItem = banner.clone();
        dyeItem = dye.clone();
        patternItem = pattern.clone();
    }

    @Override
    public boolean canExecute() {
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator() || actions.isEmpty()
                || inventory == null || source.getCurrentWindow() != inventory
                || repetitions < 1 || repetitions > 256
                || inputBannerItem.getCount() < repetitions || dyeItem.getCount() < repetitions) {
            return false;
        }
        Item expected = LoomInventory.getOutput(patternId, inputBannerItem, dyeItem, patternItem);
        if (expected == null) {
            return false;
        }
        expected.setCount(repetitions);
        List<Item> inputs = new ObjectArrayList<>();
        List<ObjectBooleanPair<Item>> outputs = new ObjectArrayList<>();
        if (!matchItems(outputs, inputs)) {
            return false;
        }
        Item banner = inputBannerItem.clone();
        banner.setCount(repetitions);
        Item dye = dyeItem.clone();
        dye.setCount(repetitions);
        return FakeBlockTransactionHelper.matches(inputs, outputs, List.of(banner, dye), List.of(expected));
    }

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
