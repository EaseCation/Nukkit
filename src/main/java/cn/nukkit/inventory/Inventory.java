package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.types.ItemStackRequest;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Inventory {

    int MAX_STACK = 64;

    int getSize();

    int getMaxStackSize();

    void setMaxStackSize(int size);

    String getName();

    String getTitle();

    Item getItem(int index);

    default boolean setItem(int index, Item item) {
        return setItem(index, item, true);
    }

    boolean setItem(int index, Item item, boolean send);

    Item[] addItem(Item... slots);

    /** 从指定槽位开始堆叠和插入物品，返回未装入的物品。 */
    Item[] addItemFrom(int startSlot, Item... slots);

    boolean canAddItem(Item item);

    Item[] removeItem(Item... slots);

    Int2ObjectMap<Item> getContents();

    Int2ObjectMap<Item> getContentsUnsafe();

    void setContents(Map<Integer, Item> items);

    void sendContents(Player player);

    void sendContents(Player... players);

    void sendContents(Collection<Player> players);

    void sendSlot(int index, Player player);

    void sendSlot(int index, Player... players);

    void sendSlot(int index, Collection<Player> players);

    boolean contains(Item item);

    Int2ObjectMap<Item> all(Item item);

    default int first(Item item) {
        return first(item, false);
    }

    int first(Item item, boolean exact);

    int firstEmpty(Item item);

    void decreaseCount(int slot);

    void remove(Item item);

    default boolean clear(int index) {
        return clear(index, true);
    }

    boolean clear(int index, boolean send);

    void clearAll();

    boolean isFull();

    boolean isEmpty();

    int getFreeSpace(Item item);

    Set<Player> getViewers();

    InventoryType getType();

    InventoryHolder getHolder();

    /**
     * 已卸载或死亡的真实容器不能继续接受旧窗口请求，个人库存和虚拟菜单保持原有生命周期。
     */
    default boolean isHolderValid() {
        InventoryHolder holder = getHolder();
        if (holder instanceof Entity entity && !(entity instanceof Player)) {
            return !entity.isClosed() && entity.isAlive();
        }
        return !(holder instanceof BlockEntity blockEntity) || !blockEntity.isClosed();
    }

    @Nullable
    default DataPacket createContainerOpenPacket(Player player, int windowId) {
        return null;
    }

    void onOpen(Player who);

    boolean open(Player who);

    void close(Player who);

    void onClose(Player who);

    void onSlotChange(int index, @Nullable Item before, @Nullable Item after, boolean send);

    default Item split(int index, int count) {
        Item item = getItem(index);
        Item split = item.split(count);
        setItem(index, item);
        return split;
    }

    default void grow(int index, int count) {
        Item item = getItem(index);
        item.grow(count);
        setItem(index, item);
    }

    default void shrink(int index, int count) {
        Item item = getItem(index);
        item.shrink(count);
        setItem(index, item);
    }

    default void pop(int index) {
        Item item = getItem(index);
        item.pop();
        setItem(index, item);
    }

    Item peek(Item item);

    default boolean slotExists(int slot) {
        return slot >= 0 && slot < getSize();
    }

    /**
     * 权威窗口发送打开包前准备客户端所需的方块表现；返回 true 时由实现调用完成回调。
     */
    default boolean prepareAuthoritativeOpen(Player player, Runnable ready) {
        return false;
    }

    /**
     * 纯表现容器可消费菜单请求；返回 true 时原物品移动不会执行，由处理方恢复客户端预测。
     * 核心仅返回失败响应，避免处理方已同步后再次重发全部库存。
     */
    default boolean handleAuthoritativeItemStackRequest(Player player, ItemStackRequest request) {
        return false;
    }
}
