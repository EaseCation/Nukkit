package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.BigCraftingGrid;
import cn.nukkit.inventory.CraftingRecipe;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.UiContainerSlots;
import cn.nukkit.scheduler.Task;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected List<Item> inputs;

    protected List<Item> secondaryOutputs;

    protected Item primaryOutput;

    protected CraftingRecipe recipe;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;

        this.inputs = new ObjectArrayList<>();

        this.secondaryOutputs = new ObjectArrayList<>();

        init(source, actions);
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

    @Override
    public boolean canExecute() {
        recipe = source.getServer().getCraftingManager().matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs, source.recipeTag);
        return this.recipe != null && super.canExecute();
    }

    @Override
    protected boolean callExecuteEvent() {
        CraftItemEvent ev;

        this.source.getServer().getPluginManager().callEvent(ev = new CraftItemEvent(this));
        return !ev.isCancelled();
    }

    @Override
    protected void sendInventories() {
        super.sendInventories();

        if (source.craftingType == Player.CRAFTING_SMALL) {
            return; // Already closed
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