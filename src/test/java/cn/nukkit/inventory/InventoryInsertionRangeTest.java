package cn.nukkit.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class InventoryInsertionRangeTest {
    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
    }

    @Test
    void reservedSlotIsExcludedFromBothStackingAndEmptySlotInsertion() {
        BaseInventory inventory = new BaseInventory(null, InventoryType.CHEST) { };
        inventory.setItem(0, item(10));
        inventory.setItem(1, item(60));
        Item incoming = item(10);
        assertEquals(0, inventory.addItemFrom(1, incoming).length);
        assertEquals(10, inventory.getItem(0).getCount());
        assertEquals(64, inventory.getItem(1).getCount());
        assertEquals(6, inventory.getItem(2).getCount());
        assertEquals(10, incoming.getCount());

        inventory.clear(0);
        assertEquals(0, inventory.addItemFrom(1, item(64)).length);
        assertTrue(inventory.getItem(0).isNull());
        assertEquals(6, inventory.getItem(3).getCount());
    }

    @Test
    void fullUsableRangeReturnsOverflowEvenWhenReservedSlotIsEmpty() {
        BaseInventory inventory = new BaseInventory(null, InventoryType.CHEST) { };
        for (int slot = 1; slot < inventory.getSize(); slot++) inventory.setItem(slot, item(64));
        Item[] overflow = inventory.addItemFrom(1, item(7));
        assertEquals(1, overflow.length);
        assertEquals(7, overflow[0].getCount());
        assertTrue(inventory.getItem(0).isNull());
        inventory.setItem(12, item(62));
        assertEquals(5, inventory.addItemFrom(1, item(7))[0].getCount());
        assertEquals(64, inventory.getItem(12).getCount());
    }

    @Test
    void ordinaryInsertionStillUsesSlotZero() {
        BaseInventory inventory = new BaseInventory(null, InventoryType.CHEST) { };
        assertEquals(0, inventory.addItem(item(3)).length);
        assertEquals(3, inventory.getItem(0).getCount());
    }

    private Item item(int count) {
        return new Item(ItemID.DIAMOND, 0, count);
    }
}
