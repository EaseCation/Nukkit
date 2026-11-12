package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.List;

import static cn.nukkit.network.protocol.types.UiContainerSlots.COUNT;

public class PlayerUIInventory extends BaseInventory {
    private final Player player;

    private final PlayerCursorInventory cursorInventory;
    private final CraftingGrid craftingGrid;
    @Deprecated
    private final BigCraftingGrid bigCraftingGrid;

    public PlayerUIInventory(Player player) {
        super(player, InventoryType.UI, new Int2ObjectOpenHashMap<>(COUNT), COUNT);
        this.player = player;

        this.cursorInventory = new PlayerCursorInventory(this);
        this.craftingGrid = new CraftingGrid(this);
        this.bigCraftingGrid = new BigCraftingGrid(this);
    }

    public PlayerCursorInventory getCursorInventory() {
        return cursorInventory;
    }

    public CraftingGrid getCraftingGrid() {
        return craftingGrid;
    }

    /**
     * 返回个人临时物品实际所在的库存；列表是快照，槽位属于各自库存而非旧 UI 的全局偏移。
     */
    public List<Inventory> getTemporaryInventories() {
        if (!player.isServerAuthoritativeInventoryEnabled()) {
            return List.of(this);
        }
        Inventory current = player.getCurrentWindow();
        if (current instanceof FakeBlockUIComponent component && component.playerUI == this) {
            return List.of(cursorInventory, craftingGrid, component);
        }
        return List.of(cursorInventory, craftingGrid);
    }

    @Override
    public void clearAll() {
        if (!player.isServerAuthoritativeInventoryEnabled()) {
            super.clearAll();
            return;
        }
        // 不关闭窗口，避免关闭时把刚清理的临时物品返还主背包；每个槽仍经过正常事件。
        for (Inventory inventory : getTemporaryInventories()) {
            inventory.clearAll();
        }
        player.clearPendingInventoryRecovery();
    }

    @Deprecated
    public BigCraftingGrid getBigCraftingGrid() {
        return bigCraftingGrid;
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("UI size is immutable");
    }

    @Override
    public void sendSlot(int index, Player... target) {
        if (player.isServerAuthoritativeInventoryEnabled()) {
            // 旧 UI 槽偏移不能直接用于独立组件，刷新真实临时库存以保持兼容。
            sendContents(target);
            return;
        }

        Item item = this.getItem(index);

        for (Player p : target) {
            if (p.isServerAuthoritativeInventoryEnabled()) {
                continue;
            }
            if (p == this.getHolder()) {
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = ContainerIds.UI;
                p.dataPacket(pk);
            } else {
                int id = p.getWindowId(this);
                if (id == ContainerIds.NONE) {
                    this.close(p);
                    continue;
                }
                InventorySlotPacket pk = new InventorySlotPacket();
                pk.slot = index;
                pk.item = item;
                pk.inventoryId = id;
                p.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendContents(Player... target) {
        if (player.isServerAuthoritativeInventoryEnabled()) {
            for (Inventory inventory : getTemporaryInventories()) {
                inventory.sendContents(target);
            }
            return;
        }

        Item[] slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            slots[i] = this.getItem(i);
        }

        for (Player p : target) {
            if (p.isServerAuthoritativeInventoryEnabled()) {
                continue;
            }
            if (p == this.getHolder()) {
                InventoryContentPacket pk = new InventoryContentPacket();
                pk.slots = slots;
                pk.inventoryId = ContainerIds.UI;
                p.dataPacket(pk);
            } else {
                int id = p.getWindowId(this);
                if (id == ContainerIds.NONE) {
                    this.close(p);
                    continue;
                }
                InventoryContentPacket pk = new InventoryContentPacket();
                pk.slots = slots;
                pk.inventoryId = id;
                p.dataPacket(pk);
            }
        }
    }

    @Override
    public int getSize() {
        return COUNT;
    }

    @Override
    public Player getHolder() {
        return player;
    }
}
