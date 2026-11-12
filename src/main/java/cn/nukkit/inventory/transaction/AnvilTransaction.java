package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.FakeBlockRecipeResult;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.LevelEventPacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnvilTransaction extends InventoryTransaction {
    private final FakeBlockTransactionContext context;
    @Nullable
    private final AnvilInventory inventory;
    private final Item inputItem;
    private final Item materialItem;
    @Nullable
    private final String filterString;
    @Nullable
    private FakeBlockRecipeResult result;
    @Nullable
    private Integer eventCost;

    public AnvilTransaction(Player source, @Nullable String filterString, Item input, Item material) {
        super(source);
        inventory = source.getCurrentWindow() instanceof AnvilInventory current ? current : null;
        context = new FakeBlockTransactionContext(inventory);
        inputItem = input.clone();
        materialItem = material.clone();
        this.filterString = filterString;
    }

    @Override
    public boolean canExecute() {
        result = null;
        if (!source.isServerAuthoritativeInventoryEnabled() || source.isSpectator()
                || inventory == null || source.getCurrentWindow() != inventory || actions.isEmpty()) {
            return false;
        }
        FakeBlockRecipeResult expected = AnvilInventory.calculateResult(inputItem, materialItem, filterString, source.isCreative());
        if (expected == null) {
            return false;
        }
        int cost = eventCost == null ? expected.cost() : eventCost;
        if (cost < 0 || !source.isCreative() && cost > source.getExperienceLevel()) {
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
        return source.getCurrentWindow() == inventory && context.isCurrent()
                && getCost() >= 0 && (source.isCreative() || source.getExperienceLevel() >= getCost());
    }

    @Override
    protected boolean callExecuteEvent() {
        if (inventory == null || result == null) {
            return false;
        }
        RepairItemEvent event = new RepairItemEvent(inventory, inputItem.clone(), result.output(), materialItem.clone(), result.cost(), source);
        source.getServer().getPluginManager().callEvent(event);
        eventCost = event.getCost();
        return !event.isCancelled() && eventCost >= 0
                && (source.isCreative() || eventCost <= source.getExperienceLevel());
    }

    @Override
    protected void onAuthoritativeCommit() {
        if (inventory == null || result == null) {
            return;
        }
        int cost = getCost();
        inventory.setCost(cost);
        boolean broken = false;
        if (!source.isCreative()) {
            source.setExperience(source.getExperience(), source.getExperienceLevel() - cost, true);
            if (ThreadLocalRandom.current().nextInt(25) < 3) {
                Block block = inventory.getHolder().level.getBlock(inventory.getHolder());
                if (block instanceof BlockAnvil anvil) {
                    int nextId = anvil.getDamagedBlockId();
                    block.level.setBlock(block, Block.get(nextId), true);
                    broken = nextId == Block.AIR;
                }
            }
        }
        inventory.getHolder().level.addLevelEvent(inventory.getHolder(), broken
                ? LevelEventPacket.EVENT_SOUND_ANVIL_BREAK : LevelEventPacket.EVENT_SOUND_ANVIL_USE);
    }

    public Item getInputItem() {
        return inputItem.clone();
    }

    public Item getMaterialItem() {
        return materialItem.clone();
    }

    public Item getOutputItem() {
        return result == null ? Items.air() : result.output();
    }

    public Item getTemplateItem() {
        return Items.air();
    }

    public int getCost() {
        return eventCost != null ? eventCost : result == null ? 0 : result.cost();
    }

    public void setCost(int cost) {
        eventCost = cost;
    }
}
