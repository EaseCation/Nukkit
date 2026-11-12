package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.PreparedInventorySlotChange;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityArmorChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item oldItem;
    private Item newItem;
    private final int slot;
    @Nullable
    private final PreparedInventorySlotChange authoritativeSlotChange;

    public EntityArmorChangeEvent(Entity entity, Item oldItem, Item newItem, int slot) {
        this(entity, oldItem, newItem, slot, null);
    }

    public EntityArmorChangeEvent(Entity entity, Item oldItem, Item newItem, int slot, @Nullable PreparedInventorySlotChange authoritativeSlotChange) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
        this.authoritativeSlotChange = authoritativeSlotChange;
    }

    /**
     * 权威预检查携带实际护甲槽上下文；普通 setter 为 null，延后处理须确认该提交仍有效。
     */
    @Nullable
    public PreparedInventorySlotChange getAuthoritativeSlotChange() {
        return authoritativeSlotChange;
    }

    public int getSlot() {
        return slot;
    }

    public Item getNewItem() {
        return newItem;
    }

    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }

    public Item getOldItem() {
        return oldItem;
    }
}
