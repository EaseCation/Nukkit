package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import static cn.nukkit.network.protocol.types.UiContainerSlots.COUNT;

public class PlayerUIInventory extends BaseInventory {
    private final Player player;

    private final PlayerCursorInventory cursorInventory;
    private final CraftingGrid craftingGrid;
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

    public BigCraftingGrid getBigCraftingGrid() {
        return bigCraftingGrid;
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("UI size is immutable");
    }

    @Override
    public void sendSlot(int index, Player... target) {
        Item item = this.getItem(index);

        for (Player p : target) {
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
        Item[] slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            slots[i] = this.getItem(i);
        }

        for (Player p : target) {
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
