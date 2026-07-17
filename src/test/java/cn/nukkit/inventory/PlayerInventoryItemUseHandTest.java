package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemShield;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerInventoryItemUseHandTest {

    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
    }

    @Test
    void keepsNativeBedrockReadsOnTheSelectedMainHandSlot() {
        Player player = mock(Player.class);
        when(player.isOffhandItemInteraction()).thenReturn(false);
        PlayerInventory inventory = new PlayerInventory(player);
        Item mainHand = Item.get(Item.STONE);
        inventory.itemInHandIndex = 4;
        inventory.slots.put(4, mainHand);

        assertTrue(mainHand.equalsExact(inventory.getItemInHand()));
    }

    @Test
    void keepsNativeBedrockWritesOnTheSelectedMainHandSlot() {
        Player player = mock(Player.class);
        when(player.isOffhandItemInteraction()).thenReturn(false);
        RecordingPlayerInventory inventory = new RecordingPlayerInventory(player);
        Item replacement = Item.get(Item.DIRT);
        inventory.itemInHandIndex = 6;

        assertTrue(inventory.setItemInHand(replacement));
        assertEquals(6, inventory.lastSetIndex);
        assertTrue(replacement.equalsExact(inventory.lastSetItem));
    }

    @Test
    void redirectsOnlyExplicitOffhandReadsAndWrites() {
        Player player = mock(Player.class);
        PlayerOffhandInventory offhandInventory = mock(PlayerOffhandInventory.class);
        Item offhand = new ItemShield();
        Item replacement = new ItemBow();
        when(player.isOffhandItemInteraction()).thenReturn(true);
        when(player.getOffhandInventory()).thenReturn(offhandInventory);
        when(offhandInventory.getItem()).thenReturn(offhand);
        when(offhandInventory.setItem(0, replacement)).thenReturn(true);
        PlayerInventory inventory = new PlayerInventory(player);

        assertTrue(offhand.equalsExact(inventory.getItemInHand()));
        assertTrue(inventory.setItemInHand(replacement));
        verify(offhandInventory).setItem(0, replacement);
    }

    private static final class RecordingPlayerInventory extends PlayerInventory {

        private int lastSetIndex = -1;
        private Item lastSetItem;

        private RecordingPlayerInventory(Player player) {
            super(player);
        }

        @Override
        public boolean setItem(int index, Item item, boolean send) {
            this.lastSetIndex = index;
            this.lastSetItem = item;
            return true;
        }
    }
}
