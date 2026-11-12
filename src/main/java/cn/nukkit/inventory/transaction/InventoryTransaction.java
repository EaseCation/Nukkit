package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PreparedInventorySlotChange;
import cn.nukkit.inventory.transaction.action.CreativeInventoryAction;
import cn.nukkit.inventory.transaction.action.CreateItemAction;
import cn.nukkit.inventory.transaction.action.DestroyItemAction;
import cn.nukkit.inventory.transaction.action.DropItemAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.PayItemAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * This is the basic type for an inventory transaction. This is used for moving items between inventories, dropping
 * items and more. It allows transactions with multiple inputs and outputs.
 * <p>
 * Validation **does not** depend on ordering. This means that the actions can appear in any order and still be valid.
 * The only validity requirement for this transaction type is that the balance of items must add up to zero. This means:
 * - No new outputs without matching input amounts
 * - No inputs without matching output amounts
 * - No userdata changes (item state, NBT, etc)
 * <p>
 * A transaction is composed of "actions", which are pairs of inputs and outputs which target a specific itemstack in
 * a specific location. There are multiple types of inventory actions which might be involved in a transaction.
 *
 * @see InventoryAction
 *
 * @author CreeperFace
 */
@Log4j2
public class InventoryTransaction {

    protected boolean hasExecuted;

    private boolean authoritativeExecuting;
    private boolean authoritativeActionsChanged;

    protected Player source;

    protected Set<Inventory> inventories = new ObjectOpenHashSet<>();

    protected List<InventoryAction> actions = new ObjectArrayList<>();

    public InventoryTransaction(Player source) {
        this(source, Collections.emptyList(), false);
    }

    public InventoryTransaction(Player source, List<InventoryAction> actions) {
        this(source, actions, true);
    }

    public InventoryTransaction(Player source, List<InventoryAction> actions, boolean init) {
        this.source = source;

        if (init) {
            init(actions);
        }
    }

    protected void init(Player source, List<InventoryAction> actions) {
        this.source = source;
        init(actions);
    }

    protected void init(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            this.addAction(action);
        }
    }

    public Player getSource() {
        return source;
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
        if (authoritativeExecuting) {
            authoritativeActionsChanged = true;
            return;
        }
        if (source.isServerAuthoritativeInventoryEnabled()) {
            actions.add(action);
            action.onAddToTransaction(this);
            return;
        }

        if (action instanceof SlotChangeAction) {
            SlotChangeAction slotChangeAction = (SlotChangeAction) action;

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

    protected boolean matchItems(List<ObjectBooleanPair<Item>> needItems, List<Item> haveItems) {
        needItems.clear();
        haveItems.clear();

        for (InventoryAction action : this.actions) {
            if (!action.getTargetItemUnsafe().isNull()) {
                needItems.add(ObjectBooleanPair.of(action.getTargetItem(), action.hasComponents()));
            }

            if (!action.isValid(this.source)) {
                return false;
            }

            if (!action.getSourceItemUnsafe().isNull()) {
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

        return true;
    }

    protected void sendInventories() {
        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction sca = (SlotChangeAction) action;

                sca.getInventory().sendSlot(sca.getSlot(), this.source);
            }
        }
    }

    /**
     * Iterates over SlotChangeActions in this transaction and compacts any which refer to the same inventorySlot in the same
     * inventory so they can be correctly handled.
     * <p>
     * Under normal circumstances, the same inventorySlot would never be changed more than once in a single transaction. However,
     * due to the way things like the crafting grid are "implemented" in MCPE 1.2 (a.k.a. hacked-in), we may get
     * multiple inventorySlot changes referring to the same inventorySlot in a single transaction. These multiples are not even guaranteed
     * to be in the correct order (inventorySlot splitting in the crafting grid for example, causes the actions to be sent in the
     * wrong order), so this method also tries to chain them into order.
     * </p>
     *
     * @return successful
     */
    protected boolean squashDuplicateSlotChanges() {
        Int2ObjectMap<List<SlotChangeAction>> slotChanges = new Int2ObjectOpenHashMap<>();

        for (InventoryAction action : this.actions) {
            if (action instanceof SlotChangeAction) {
                int hash = Objects.hash(((SlotChangeAction) action).getInventory(), ((SlotChangeAction) action).getSlot());

                List<SlotChangeAction> list = slotChanges.get(hash);
                if (list == null) {
                    list = new ArrayList<>();
                }

                list.add((SlotChangeAction) action);

                slotChanges.put(hash, list);
            }
        }

        for (Int2ObjectMap.Entry<List<SlotChangeAction>> entry : new ArrayList<>(slotChanges.int2ObjectEntrySet())) {
            int hash = entry.getIntKey();
            List<SlotChangeAction> list = entry.getValue();

            if (list.size() == 1) { //No need to compact inventorySlot changes if there is only one on this inventorySlot
                slotChanges.remove(hash);
                continue;
            }

            List<SlotChangeAction> originalList = new ArrayList<>(list);

            SlotChangeAction originalAction = null;
            Item lastTargetItem = null;

            for (int i = 0; i < list.size(); i++) {
                SlotChangeAction action = list.get(i);

                if (action.isValid(this.source)) {
                    originalAction = action;
                    lastTargetItem = action.getTargetItem();
                    list.remove(i);
                    break;
                }
            }

            if (originalAction == null) {
                return false; //Couldn't find any actions that had a source-item matching the current inventory inventorySlot
            }

            int sortedThisLoop;

            do {
                sortedThisLoop = 0;
                for (int i = 0; i < list.size(); i++) {
                    SlotChangeAction action = list.get(i);

                    Item actionSource = action.getSourceItem();
                    if (actionSource.equalsExact(lastTargetItem)) {
                        lastTargetItem = action.getTargetItem();
                        list.remove(i);
                        sortedThisLoop++;
                    }
                    else if (actionSource.equals(lastTargetItem)) {
                        lastTargetItem.count -= actionSource.count;
                        list.remove(i);
                        if (lastTargetItem.count == 0) sortedThisLoop++;
                    }
                }
            } while (sortedThisLoop > 0);

            if (!list.isEmpty()) { //couldn't chain all the actions together
                log.debug("Failed to compact {} actions for {}", originalList.size(), this.source.getName());
                return false;
            }

            for (SlotChangeAction action : originalList) {
                this.actions.remove(action);
            }

            this.addAction(new SlotChangeAction(originalAction.getInventory(), originalAction.getSlot(), originalAction.getSourceItem(), lastTargetItem));

            log.debug("Successfully compacted {} actions for {}", originalList.size(), this.source.getName());
        }

        return true;
    }

    public boolean canExecute() {
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return canExecuteAuthoritative();
        }
        return !this.actions.isEmpty() && (/*uiTransaction ||*/ matchItems());
    }

    private boolean canExecuteAuthoritative() {
//        if (!this.squashDuplicateSlotChanges()) {
//            return false;
//        }

        if (source.isSpectator() || actions.isEmpty()) {
            return false;
        }

        List<ObjectBooleanPair<Item>> needItems = new ObjectArrayList<>();
        List<Item> haveItems = new ObjectArrayList<>();
        if (!matchItems(needItems, haveItems)) {
            return false;
        }
        if (!haveItems.isEmpty()) {
            return false;
        }
        return needItems.isEmpty();
    }

    protected boolean callExecuteEvent() {
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return callExecuteEventAuthoritative();
        }
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

    private boolean callExecuteEventAuthoritative() {
        InventoryTransactionEvent event = new InventoryTransactionEvent(this);
        source.getServer().getPluginManager().callEvent(event);
        SlotChangeAction from = null;
        SlotChangeAction to = null;
        for (InventoryAction action : actions) {
            if (!(action instanceof SlotChangeAction slotChange)) {
                continue;
            }
            if (from == null) {
                from = slotChange;
            } else {
                to = slotChange;
            }
        }
        if (from != null && to != null) {
            if (from.getTargetItemUnsafe().getCount() > from.getSourceItemUnsafe().getCount()) {
                from = to;
            }
            InventoryClickEvent click = new InventoryClickEvent(source, from.getInventory(), from.getSlot(),
                    from.getSourceItem(), from.getTargetItem());
            source.getServer().getPluginManager().callEvent(click);
            return !event.isCancelled() && !click.isCancelled();
        }
        return !event.isCancelled();
    }

    public boolean execute() {
        if (source.isServerAuthoritativeInventoryEnabled()) {
            return executeAuthoritative();
        }
        if (this.hasExecuted() || !this.canExecute()) {
            //HACK: legacy book edit mess
            if (source.isSurvivalLike() && actions.size() == /*3*/2) {
                for (InventoryAction action : actions) {
                    if (action instanceof CreativeInventoryAction creative && creative.isBookEdit()) {
                        return true;
                    }
                }
            }

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

//            if (!action.onPreExecuteNoSync(this.source)) {
//                return true;
//            }
        }

        boolean allActionsSucceeded = true;
        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                allActionsSucceeded = false;
                action.onExecuteFail(this.source);
            }
        }

        this.hasExecuted = true;
        if (allActionsSucceeded) {
            onSuccessfulExecute();
        }
        return true;
    }

    private boolean executeAuthoritative() {
        if (hasExecuted || authoritativeExecuting) {
            return false;
        }
        authoritativeExecuting = true;
        authoritativeActionsChanged = false;
        List<PreparedInventorySlotChange> slots = new ObjectArrayList<>();
        List<DropItemAction.PreparedDrop> drops = new ObjectArrayList<>();
        List<AuthoritativeActionSnapshot> snapshots = new ObjectArrayList<>();
        Inventory initialWindow = source.getCurrentWindow();
        Level initialLevel = source.getLevel();
        long initialMovementEpoch = source.getMovementEpoch();
        boolean initiallyAlive = source.isAlive();
        try {
            if (!canExecute()) {
                return false;
            }
            for (InventoryAction action : actions) {
                if (action instanceof SlotChangeAction slotChange) {
                    if (!(slotChange.getInventory() instanceof BaseInventory inventory)) {
                        return false;
                    }
                    PreparedInventorySlotChange slot = inventory.captureAuthoritativeSlotChange(
                            slotChange.getSlot(), action.getSourceItemUnsafe(), action.getTargetItemUnsafe());
                    if (slot == null) {
                        return false;
                    }
                    for (PreparedInventorySlotChange existing : slots) {
                        if (existing.isSameSlot(slot)) {
                            return false;
                        }
                    }
                    slots.add(slot);
                } else if (action.getClass() != DropItemAction.class && action.getClass() != CreateItemAction.class
                        && action.getClass() != DestroyItemAction.class && action.getClass() != PayItemAction.class) {
                    return false;
                }
                snapshots.add(new AuthoritativeActionSnapshot(action, action.getSourceItem(), action.getTargetItem()));
            }

            if (!callExecuteEvent()) {
                return false;
            }
            for (AuthoritativeActionSnapshot snapshot : snapshots) {
                if (!snapshot.action().onPreExecute(source)) {
                    return false;
                }
            }
            if (!isAuthoritativePlanCurrent(snapshots, slots, initialWindow, initialLevel, initialMovementEpoch, initiallyAlive)) {
                return false;
            }
            if (!applyAuthoritativeTargetOverrides(snapshots, slots)) {
                return false;
            }
            for (PreparedInventorySlotChange slot : slots) {
                if (!slot.prepare()) {
                    return false;
                }
            }
            if (!isAuthoritativePlanCurrent(snapshots, slots, initialWindow, initialLevel, initialMovementEpoch, initiallyAlive)) {
                return false;
            }

            // 提前取得全部掉落资源，期间不创建实体，也不向插件暴露提交后的槽。
            for (AuthoritativeActionSnapshot snapshot : snapshots) {
                if (snapshot.action() instanceof DropItemAction drop) {
                    DropItemAction.PreparedDrop prepared = drop.prepareAuthoritative(source);
                    if (prepared == null) {
                        return false;
                    }
                    drops.add(prepared);
                }
            }
            if (!isAuthoritativePlanCurrent(snapshots, slots, initialWindow, initialLevel, initialMovementEpoch, initiallyAlive)) {
                return false;
            }
            // 本阶段不调用插件和槽通知，避免观察到半个事务或在第二槽取消后丢失物品。
            for (PreparedInventorySlotChange slot : slots) {
                if (!slot.commit()) {
                    return false;
                }
            }
            hasExecuted = true;

            // 槽全部提交后先锁定终态，后续结算和掉落回调不能再回滚或重入执行。
            try {
                onAuthoritativeCommit();
            } catch (RuntimeException exception) {
                log.error("Failed to complete authoritative transaction side effects for {}", source.getName(), exception);
            }
            for (DropItemAction.PreparedDrop drop : drops) {
                try {
                    drop.spawn();
                } catch (RuntimeException exception) {
                    log.error("Failed to spawn committed inventory drop for {}", source.getName(), exception);
                }
            }
            for (PreparedInventorySlotChange slot : slots) {
                try {
                    slot.publish();
                } catch (RuntimeException exception) {
                    log.error("Failed to publish authoritative inventory change for {}", source.getName(), exception);
                }
            }
            for (AuthoritativeActionSnapshot snapshot : snapshots) {
                try {
                    snapshot.action().onExecuteSuccess(source);
                } catch (RuntimeException exception) {
                    log.error("Failed to complete inventory action callback for {}", source.getName(), exception);
                }
            }
            try {
                onSuccessfulExecute();
            } catch (RuntimeException exception) {
                log.error("Failed to notify successful inventory transaction for {}", source.getName(), exception);
            }
            return true;
        } finally {
            try {
                if (!hasExecuted) {
                    for (int i = slots.size() - 1; i >= 0; i--) {
                        slots.get(i).rollback();
                    }
                    sendInventories();
                }
            } finally {
                authoritativeExecuting = false;
            }
        }
    }

    private boolean isAuthoritativePlanCurrent(List<AuthoritativeActionSnapshot> snapshots,
                                                List<PreparedInventorySlotChange> slots, Inventory initialWindow,
                                                Level initialLevel, long initialMovementEpoch, boolean initiallyAlive) {
        if (!isAuthoritativeContextCurrent(initialWindow, initialLevel, initialMovementEpoch, initiallyAlive)
                || !areAuthoritativeActionsUnchanged(snapshots) || !validateAuthoritativeContext()) {
            return false;
        }
        for (AuthoritativeActionSnapshot snapshot : snapshots) {
            InventoryAction action = snapshot.action();
            if (!action.isValid(source)
                    || action instanceof DropItemAction && (!source.spawned || !source.isAlive())) {
                return false;
            }
            if (action instanceof SlotChangeAction slotChange) {
                Item target = getAuthoritativeTargetItem(slotChange);
                if (target == null || !snapshot.target().equalsExact(target)
                        && !new SlotChangeAction(slotChange.getInventory(), slotChange.getSlot(), snapshot.source(), target).isValid(source)) {
                    return false;
                }
            }
        }
        for (PreparedInventorySlotChange slot : slots) {
            if (!slot.isCurrent()) {
                return false;
            }
        }
        return true;
    }

    private boolean applyAuthoritativeTargetOverrides(List<AuthoritativeActionSnapshot> snapshots, List<PreparedInventorySlotChange> slots) {
        int slotIndex = 0;
        for (AuthoritativeActionSnapshot snapshot : snapshots) {
            if (!(snapshot.action() instanceof SlotChangeAction action)) {
                continue;
            }
            Item target = getAuthoritativeTargetItem(action);
            if (target == null) {
                return false;
            }
            if (!snapshot.target().equalsExact(target)) {
                if (!new SlotChangeAction(action.getInventory(), action.getSlot(), snapshot.source(), target).isValid(source)) {
                    return false;
                }
                PreparedInventorySlotChange replacement = slots.get(slotIndex).withTarget(target);
                if (replacement == null) {
                    return false;
                }
                slots.set(slotIndex, replacement);
            }
            slotIndex++;
        }
        return true;
    }

    /**
     * 服务端事件可批准不同产物；原客户端动作仍用于守恒和来源复核。
     */
    protected Item getAuthoritativeTargetItem(SlotChangeAction action) {
        return action.getTargetItemUnsafe();
    }

    private boolean isAuthoritativeContextCurrent(Inventory initialWindow, Level initialLevel,
                                                   long initialMovementEpoch, boolean initiallyAlive) {
        return !source.isClosed() && !source.isSpectator() && source.getCurrentWindow() == initialWindow
                && source.getLevel() == initialLevel && source.getMovementEpoch() == initialMovementEpoch
                && source.isAlive() == initiallyAlive;
    }

    private boolean areAuthoritativeActionsUnchanged(List<AuthoritativeActionSnapshot> snapshots) {
        if (authoritativeActionsChanged || actions.size() != snapshots.size()) {
            return false;
        }
        for (int i = 0; i < snapshots.size(); i++) {
            AuthoritativeActionSnapshot snapshot = snapshots.get(i);
            InventoryAction action = snapshot.action();
            if (actions.get(i) != action || !snapshot.source().equalsExact(action.getSourceItemUnsafe())
                    || !snapshot.target().equalsExact(action.getTargetItemUnsafe())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 事件回调之后复核经验、工作站等上下文，不能在此产生副作用。
     */
    protected boolean validateAuthoritativeContext() {
        return true;
    }

    /**
     * 所有槽提交成功后执行一次，专供新事务结算经验和工作站副作用。
     */
    protected void onAuthoritativeCommit() {
    }

    /**
     * 两种库存模式全部动作成功后通知业务；旧模式的部分失败不会触发此回调。
     */
    protected void onSuccessfulExecute() {
    }

    private record AuthoritativeActionSnapshot(InventoryAction action, Item source, Item target) {
    }

    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}
