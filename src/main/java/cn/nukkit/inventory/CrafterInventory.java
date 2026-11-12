package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.event.Event;
import cn.nukkit.event.inventory.CrafterCraftEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.recipe.CraftingGridMatcher;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

@Log4j2
public class CrafterInventory extends ContainerInventory {
    private boolean crafting;
    public CrafterInventory(BlockEntityCrafter blockEntity) {
        super(blockEntity, InventoryType.CRAFTER);
    }

    @Override
    public BlockEntityCrafter getHolder() {
        return (BlockEntityCrafter) super.getHolder();
    }

    @Override
    public int firstEmpty(Item item) {
        for (int i = 0; i < this.size; ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            if (this.getItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean canAddItem(Item item) {
        return getFreeSpace(item) >= item.getCount();
    }

    @Override
    public Item[] addItemFrom(int startSlot, Item... slots) {
        List<Item> remaining = new ObjectArrayList<>();
        for (Item offered : slots) {
            if (offered.isNull()) {
                continue;
            }
            Item item = offered.clone();
            while (item.getCount() > 0) {
                int slot = findLeastSlot(item, Math.max(0, startSlot));
                if (slot < 0) {
                    break;
                }
                Item before = getItem(slot);
                Item after = item.clone(before.getCount() + 1);
                if (!setItem(slot, after)) {
                    break;
                }
                item.setCount(item.getCount() - 1);
            }
            if (!item.isNull()) {
                remaining.add(item);
            }
        }
        return remaining.toArray(Item[]::new);
    }

    private int findLeastSlot(Item target, int startSlot) {
        int selected = -1;
        int count = Integer.MAX_VALUE;
        int limit = Math.min(target.getMaxStackSize(), getMaxStackSize());
        for (int slot = startSlot; slot < getSize(); slot++) {
            if (getHolder().isSlotDisabled(slot)) {
                continue;
            }
            Item existing = getItem(slot);
            if ((existing.isNull() || existing.equals(target, true, true, true))
                    && existing.getCount() < limit && existing.getCount() < count) {
                selected = slot;
                count = existing.getCount();
            }
        }
        return selected;
    }

    @Override
    public boolean isFull() {
        for (int slot = 0; slot < getSize(); slot++) {
            if (getHolder().isSlotDisabled(slot)) {
                continue;
            }
            Item item = getItem(slot);
            if (item.isNull() || item.getCount() < Math.min(item.getMaxStackSize(), getMaxStackSize())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        if (this.getMaxStackSize() <= 0) {
            return false;
        }

        for (int i = 0; i < this.getSize(); ++i) {
            if (getHolder().isSlotDisabled(i)) {
                continue;
            }
            Item item = this.getItem(i);
            if (!item.isNull()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getFreeSpace(Item item) {
        int maximum = Math.min(item.getMaxStackSize(), getMaxStackSize());
        int space = 0;
        for (int slot = 0; slot < getSize(); slot++) {
            if (getHolder().isSlotDisabled(slot)) {
                continue;
            }
            Item existing = getItem(slot);
            if (existing.isNull()) {
                space += maximum;
            } else if (existing.equals(item, true, true, true)) {
                space += Math.max(0, maximum - existing.getCount());
            }
        }
        return space;
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return (item.isNull() || !getHolder().isSlotDisabled(index)) && super.setItem(index, item, send);
    }

    @Override
    @Nullable
    public PreparedInventorySlotChange captureAuthoritativeSlotChange(int index, Item source, Item target) {
        return !target.isNull() && getHolder().isSlotDisabled(index)
                ? null : super.captureAuthoritativeSlotChange(index, source, target);
    }

    private boolean beginCrafting() {
        if (crafting) {
            return false;
        }
        crafting = true;
        return true;
    }

    /**
     * prepareDrop 仅预取掉落资源，资源不可用时返回 null；返回的操作只在全部槽提交后执行。
     */
    public boolean craft(CraftingGridMatch match, @Nullable BaseInventory target, BlockFace facing, Consumer<Event> events, Function<Item, Runnable> prepareDrop, BooleanSupplier contextValid) {
        if (!this.isHolderValid() || target != null && !target.isHolderValid() || !this.beginCrafting()) {
            return false;
        }
        List<PreparedInventorySlotChange> changes = new ArrayList<>();
        List<Runnable> drops = new ArrayList<>();
        boolean committed = false;
        try {
            int disabled = this.getHolder().getDisabledSlots();
            Item[] before = contents(this);
            int[] consumed = match.consumed();
            if (!CraftingGridMatcher.verify(match, before, disabled)) {
                return false;
            }
            if (consumed.length != before.length) {
                return false;
            }
            for (int slot = 0; slot < consumed.length; slot++) {
                if (consumed[slot] < 0 || before[slot].getCount() < consumed[slot]
                        || consumed[slot] > 0 && (disabled & 1 << slot) != 0) {
                    return false;
                }
                if (consumed[slot] == 0) {
                    continue;
                }
                Item after = before[slot].clone(before[slot].getCount() - consumed[slot]);
                PreparedInventorySlotChange change = this.captureAuthoritativeSlotChange(slot, before[slot], after);
                if (change == null) {
                    return false;
                }
                changes.add(change);
            }
            if (changes.isEmpty()) {
                return false;
            }
            Item[] targetBefore = target == null || target == this ? new Item[0] : contents(target);
            Item[] targetAfter = new Item[targetBefore.length];
            for (int slot = 0; slot < targetBefore.length; slot++) {
                targetAfter[slot] = targetBefore[slot].clone();
            }
            List<Item> overflow = new ArrayList<>();
            List<Item> results = match.results();
            CrafterCraftEvent craftEvent = new CrafterCraftEvent(this, match.recipe(), results);
            events.accept(craftEvent);
            if (craftEvent.isCancelled()) {
                return false;
            }
            for (Item result : results) {
                if (result.isNull() || result.getCount() < 0 || result.getMaxStackSize() <= 0) {
                    return false;
                }
                int inserted = insert(target, targetAfter, facing, result);
                if (inserted > 0) {
                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(this, target, this.getHolder(),
                            result.clone(inserted), InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    events.accept(event);
                    if (event.isCancelled() || !result.clone(inserted).equalsExact(event.getItem())) {
                        return false;
                    }
                }
                int remaining = result.getCount() - inserted;
                while (remaining > 0) {
                    Item stack = result.clone(Math.min(result.getMaxStackSize(), remaining));
                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(this, null, this.getHolder(),
                            stack.clone(), InventoryMoveItemEvent.Action.DISPENSE);
                    events.accept(event);
                    if (event.isCancelled() || !stack.equalsExact(event.getItem())) {
                        return false;
                    }
                    overflow.add(stack);
                    remaining -= stack.getCount();
                }
            }
            for (int slot = 0; slot < targetAfter.length; slot++) {
                if (!targetBefore[slot].equalsExact(targetAfter[slot])) {
                    PreparedInventorySlotChange change = target.captureAuthoritativeSlotChange(slot, targetBefore[slot], targetAfter[slot]);
                    if (change == null) {
                        return false;
                    }
                    changes.add(change);
                }
            }
            if (!unchanged(before, disabled, target, targetBefore, contextValid)) {
                return false;
            }
            for (PreparedInventorySlotChange change : changes) {
                if (!change.prepare()) {
                    return false;
                }
            }
            if (!unchanged(before, disabled, target, targetBefore, contextValid)) {
                return false;
            }
            // 预取全部掉落资源，期间不能生成实体或触发实体事件。
            for (Item stack : overflow) {
                Runnable drop = prepareDrop.apply(stack);
                if (drop == null) {
                    return false;
                }
                drops.add(drop);
            }
            if (!unchanged(before, disabled, target, targetBefore, contextValid)) {
                return false;
            }
            for (PreparedInventorySlotChange change : changes) {
                if (!change.isCurrent()) {
                    return false;
                }
            }
            for (PreparedInventorySlotChange change : changes) {
                if (!change.commit()) {
                    return false;
                }
            }
            // 提交之后插件可搬走产物或关闭实体，不能再回滚原料并复制已发布的产物。
            committed = true;
            for (Runnable drop : drops) {
                try {
                    drop.run();
                } catch (RuntimeException exception) {
                    log.error("Failed to spawn committed crafter output", exception);
                }
            }
            for (PreparedInventorySlotChange change : changes) {
                try {
                    change.publish();
                } catch (RuntimeException exception) {
                    log.error("Failed to publish crafter inventory change", exception);
                }
            }
            this.sendContents(this.getViewers());
            if (target != null && target != this) {
                target.sendContents(target.getViewers());
            }
            return true;
        } finally {
            try {
                if (!committed) {
                    for (int index = changes.size() - 1; index >= 0; index--) {
                        changes.get(index).rollback();
                    }
                }
            } finally {
                crafting = false;
            }
        }
    }

    private static Item[] contents(BaseInventory inventory) {
        Item[] contents = new Item[inventory.getSize()];
        for (int slot = 0; slot < contents.length; slot++) {
            contents[slot] = inventory.getItem(slot);
        }
        return contents;
    }

    private boolean unchanged(Item[] before, int disabled, @Nullable BaseInventory target, Item[] targetBefore, BooleanSupplier valid) {
        if (!valid.getAsBoolean() || !this.isHolderValid() || this.getSize() != before.length
                || this.getHolder().getDisabledSlots() != disabled
                || target != null && (!target.isHolderValid() || target != this && target.getSize() != targetBefore.length)) {
            return false;
        }
        for (int slot = 0; slot < before.length; slot++) {
            if (!before[slot].equalsExact(this.getItem(slot))) {
                return false;
            }
        }
        for (int slot = 0; slot < targetBefore.length; slot++) {
            if (!targetBefore[slot].equalsExact(target.getItem(slot))) {
                return false;
            }
        }
        return true;
    }

    private static int insert(@Nullable BaseInventory target, Item[] slots, BlockFace facing, Item result) {
        if (target == null) {
            return 0;
        }
        int remaining = result.getCount();
        if (target instanceof CrafterInventory) {
            while (remaining > 0) {
                int chosen = -1;
                int count = Integer.MAX_VALUE;
                for (int slot = 0; slot < slots.length; slot++) {
                    Item existing = slots[slot];
                    if (canInsert(target, facing, slot, result)
                            && (existing.isNull() || existing.equals(result, true, true, true))
                            && existing.getCount() < Math.min(result.getMaxStackSize(), target.getMaxStackSize())
                            && existing.getCount() < count) {
                        chosen = slot;
                        count = existing.getCount();
                    }
                }
                if (chosen < 0) {
                    break;
                }
                slots[chosen] = result.clone(count + 1);
                remaining--;
            }
            return result.getCount() - remaining;
        }
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = 0; slot < slots.length && remaining > 0; slot++) {
                if (!canInsert(target, facing, slot, result)) {
                    continue;
                }
                Item existing = slots[slot];
                if (pass == 0 ? existing.isNull() || !existing.equals(result, true, true, true) : !existing.isNull()) {
                    continue;
                }
                int limit = Math.min(result.getMaxStackSize(), target.getMaxStackSize());
                if (target instanceof BrewingInventory && slot >= 1 && slot <= 3) {
                    limit = 1;
                }
                int added = Math.min(remaining, limit - existing.getCount());
                if (added > 0) {
                    slots[slot] = result.clone(existing.getCount() + added);
                    remaining -= added;
                }
            }
        }
        return result.getCount() - remaining;
    }

    private static boolean canInsert(BaseInventory target, BlockFace facing, int slot, Item item) {
        if (target instanceof CrafterInventory crafter && crafter.getHolder().isSlotDisabled(slot)) {
            return false;
        }
        if (target instanceof FurnaceInventory) {
            return facing == BlockFace.DOWN ? slot == 0 : slot == 1 && item.getFuelTime() > 0;
        }
        if (target instanceof BrewingInventory) {
            return facing == BlockFace.DOWN ? slot == 0 && item.isBrewingIngredient()
                    : slot == 4 && item.is(ItemID.BLAZE_POWDER)
                    || slot >= 1 && slot <= 3 && (item.isPotion() || item.is(ItemID.GLASS_BOTTLE));
        }
        return true;
    }
}
