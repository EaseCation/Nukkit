package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.item.Items;

import javax.annotation.Nullable;

/**
 * 服务端权威事务使用的槽快照，事件预检查、写入和通知分阶段完成。
 */
public final class PreparedInventorySlotChange {
    private final BaseInventory inventory;
    private final int slot;
    @Nullable
    private final Item original;
    private final Item before;
    private final Item target;
    @Nullable
    private Item committedItem;
    private boolean committed;

    PreparedInventorySlotChange(BaseInventory inventory, int slot, Item target) {
        this.inventory = inventory;
        this.slot = slot;
        original = inventory.slots.get(slot);
        before = original == null ? Items.air() : original.clone();
        this.target = target.clone();
    }

    private PreparedInventorySlotChange(PreparedInventorySlotChange source, Item target) {
        inventory = source.inventory;
        slot = source.slot;
        original = source.original;
        before = source.before;
        this.target = target.clone();
    }

    /**
     * 仅替换事件批准的目标，保留事件发生前捕获的原槽身份。
     */
    @Nullable
    public PreparedInventorySlotChange withTarget(Item target) {
        return !committed && isCurrent() ? new PreparedInventorySlotChange(this, target) : null;
    }

    public boolean isSameSlot(PreparedInventorySlotChange other) {
        return inventory == other.inventory && slot == other.slot;
    }

    public BaseInventory getInventory() {
        return inventory;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isCurrent() {
        Item current = inventory.slots.get(slot);
        return inventory.slotExists(slot) && current == original
                && (current == null || before.equalsExact(current));
    }

    public boolean prepare() {
        return isCurrent() && inventory.prepareAuthoritativeSlotChange(slot, before, target, this) && isCurrent();
    }

    public boolean commit() {
        if (committed || !isCurrent()) {
            return false;
        }
        committedItem = target.isNull() ? null : target.clone();
        if (committedItem == null) {
            inventory.slots.remove(slot);
        } else {
            inventory.slots.put(slot, committedItem);
        }
        committed = true;
        return true;
    }

    public boolean isCommittedCurrent() {
        return committed && inventory.slots.get(slot) == committedItem
                && (committedItem == null || target.equalsExact(committedItem));
    }

    /**
     * 仅撤回本事务仍持有的写入，保留插件或其他动作替换的新值。
     */
    public void rollback() {
        if (!committed || inventory.slots.get(slot) != committedItem
                || committedItem != null && !target.equalsExact(committedItem)) {
            return;
        }
        if (original == null) {
            inventory.slots.remove(slot);
        } else {
            inventory.slots.put(slot, original);
        }
        committed = false;
    }

    public void publish() {
        if (isCommittedCurrent()) {
            inventory.publishAuthoritativeSlotChange(slot, before, committedItem);
        }
    }
}
