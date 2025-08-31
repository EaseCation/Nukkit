package cn.nukkit.event.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

public class ContainerMoveItemEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Position container;

    private Item item;

    private final Action action;

    public ContainerMoveItemEvent(BlockEntity container, Inventory inventory, Item item, Action action) {
        this((Position) container, inventory, item, action);
    }

    public ContainerMoveItemEvent(Block container, Inventory inventory, Item item, Action action) {
        this((Position) container, inventory, item, action);
    }

    private ContainerMoveItemEvent(Position container, Inventory inventory, Item item, Action action) {
        super(inventory);
        this.container = container;
        this.item = item;
        this.action = action;
    }

    /**
     * @return BlockEntity or Block
     */
    public Position getContainer() {
        return container;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        PULL,
        PUSH,
    }
}
