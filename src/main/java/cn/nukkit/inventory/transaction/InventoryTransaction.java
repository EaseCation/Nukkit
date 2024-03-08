package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.*;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * @author CreeperFace
 */
public class InventoryTransaction {

    private long creationTime;
    protected boolean hasExecuted;

    protected Player source;

    protected Set<Inventory> inventories = new ObjectOpenHashSet<>();

    protected List<InventoryAction> actions = new ObjectArrayList<>();

    protected boolean uiTransaction;

    public InventoryTransaction(Player source, List<InventoryAction> actions) {
        this(source, actions, true);
    }

    public InventoryTransaction(Player source, List<InventoryAction> actions, boolean init) {
        if (init) {
            init(source, actions);
        }
    }

    protected void init(Player source, List<InventoryAction> actions) {
        creationTime = System.currentTimeMillis();
        this.source = source;

        for (InventoryAction action : actions) {
            this.addAction(action);
        }
    }

    public Player getSource() {
        return source;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public List<InventoryAction> getActionList() {
        return actions;
    }

    public Set<InventoryAction> getActions() {
        return new ObjectOpenHashSet<>(actions);
    }

    public void addAction(InventoryAction action) {
        if (action instanceof SlotChangeAction) {
            SlotChangeAction slotChangeAction = (SlotChangeAction) action;

            if (slotChangeAction.getInventory() instanceof PlayerUIInventory
                    && slotChangeAction.getSlot() != CURSOR && slotChangeAction.getSlot() != CREATED_ITEM_OUTPUT) {
                uiTransaction = true;
            }

            ListIterator<InventoryAction> iterator = this.actions.listIterator();
            while (iterator.hasNext()) {
                InventoryAction existingAction = iterator.next();
                if (existingAction instanceof SlotChangeAction) {
                    SlotChangeAction existingSlotChangeAction = (SlotChangeAction) existingAction;
                    if (existingSlotChangeAction.getInventory() != slotChangeAction.getInventory()
                            || existingSlotChangeAction.getSlot() != slotChangeAction.getSlot()) {
                        continue;
                    }

                    Item changeSource = slotChangeAction.getSourceItemUnsafe();
                    Item existingTarget = existingSlotChangeAction.getTargetItemUnsafe();
                    if (changeSource.equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        iterator.set(new SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSlotChangeAction.getSourceItem(), slotChangeAction.getTargetItem()));
                        action.onAddToTransaction(this);
                        return;
                    }

                    Item existingSource = existingSlotChangeAction.getSourceItemUnsafe();
                    Item changeTarget = slotChangeAction.getTargetItemUnsafe();
                    if (changeSource.equals(existingSource, existingSource.hasMeta(), existingSource.hasCompoundTag())
                            && changeTarget.equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        int targetCount = changeTarget.getCount();

                        existingSource = existingSlotChangeAction.getSourceItem();
                        existingSource.setCount(existingSource.getCount() + changeSource.getCount());

                        existingTarget = existingSlotChangeAction.getTargetItem();
                        existingTarget.setCount(existingTarget.getCount() + targetCount);

                        iterator.set(new SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSource, existingTarget));
                        return;
                    }
                }
            }
        }

        this.actions.add(action);
        action.onAddToTransaction(this);
    }

    /**
     * This method should not be used by plugins, it's used to add tracked inventories for InventoryActions
     * involving inventories.
     *
     * @param inventory to add
     */
    public void addInventory(Inventory inventory) {
        this.inventories.add(inventory);
    }

    protected boolean matchItems() {
        List<ObjectBooleanPair<Item>> needItems = new ObjectArrayList<>();
        List<Item> haveItems = new ObjectArrayList<>();

        for (InventoryAction action : this.actions) {
            if (action.getTargetItemUnsafe().getId() != Item.AIR) {
                needItems.add(ObjectBooleanPair.of(action.getTargetItem(), action.hasComponents()));
            }

            if (!action.isValid(this.source)) {
                return false;
            }

            if (action.getSourceItemUnsafe().getId() != Item.AIR) {
                haveItems.add(action.getSourceItem());
            }
        }

        Iterator<ObjectBooleanPair<Item>> needItemIterator = needItems.iterator();
        while (needItemIterator.hasNext()) {
            ObjectBooleanPair<Item> needItemEntry = needItemIterator.next();
            Item needItem = needItemEntry.left();
            Iterator<Item> haveItemIterator = haveItems.iterator();
            while (haveItemIterator.hasNext()) {
                Item haveItem = haveItemIterator.next();
                if (needItem.equals(haveItem, true, true, needItemEntry.rightBoolean())) {
                    int amount = Math.min(haveItem.getCount(), needItem.getCount());
                    needItem.setCount(needItem.getCount() - amount);
                    haveItem.setCount(haveItem.getCount() - amount);
                    if (haveItem.getCount() == 0) {
                        haveItemIterator.remove();
                    }
                    if (needItem.getCount() == 0) {
                        needItemIterator.remove();
                        break;
                    }
                }
            }
        }

        return haveItems.isEmpty() && needItems.isEmpty();
    }

    protected void sendInventories() {
        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction sca = (SlotChangeAction) action;

                sca.getInventory().sendSlot(sca.getSlot(), this.source);
            }
        }
    }

    public boolean canExecute() {
        return !this.actions.isEmpty() && (uiTransaction || matchItems());
    }

    protected boolean callExecuteEvent() {
        InventoryTransactionEvent ev = new InventoryTransactionEvent(this);
        this.source.getServer().getPluginManager().callEvent(ev);

        SlotChangeAction from = null;
        SlotChangeAction to = null;
        Player who = null;

        for (InventoryAction action : this.actions) {
            if (!(action instanceof SlotChangeAction)) {
                continue;
            }
            SlotChangeAction slotChange = (SlotChangeAction) action;

            if (slotChange.getInventory().getHolder() instanceof Player) {
                who = (Player) slotChange.getInventory().getHolder();
            }

            if (from == null) {
                from = slotChange;
            } else {
                to = slotChange;
            }
        }

        if (who != null && to != null) {
            Item targetItem = from.getTargetItem();
            Item sourceItem = from.getSourceItem();

            if (targetItem.getCount() > sourceItem.getCount()) {
                from = to;
            }

            InventoryClickEvent ev2 = new InventoryClickEvent(who, from.getInventory(), from.getSlot(), sourceItem, targetItem);
            this.source.getServer().getPluginManager().callEvent(ev2);
            if (ev2.isCancelled()) {
                return false;
            }
        }

        return !ev.isCancelled();
    }

    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.sendInventories();
            return false;
        }

        if (!callExecuteEvent()) {
            this.sendInventories();
            return true;
        }

        for (InventoryAction action : this.actions) {
            if (!action.onPreExecute(this.source)) {
                this.sendInventories();
                return true;
            }
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }

        this.hasExecuted = true;
        return true;
    }

    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}