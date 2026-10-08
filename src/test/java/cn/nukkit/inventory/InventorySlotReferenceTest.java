package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventorySlotReferenceTest {
    private Player player;
    private PlayerInventory inventory;
    private PluginManager plugins;
    private Server server;

    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
    }

    @BeforeEach
    void prepare() {
        player = mock(Player.class);
        when(player.getViewers()).thenReturn(Map.of());
        inventory = new PlayerInventory(player);
        inventory.slots.put(0, new Item(ItemID.SNOWBALL, 0, 8));
        plugins = mock(PluginManager.class);
        server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(plugins);
    }

    @Test
    void writesConsumptionToOriginalSlotAfterSelectionChanges() {
        InventorySlotReference source = inventory.captureHeldItem();
        inventory.setHeldItemIndex(1, false);
        assertFalse(source.isSelectedBy(inventory));
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            servers.when(Server::getInstance).thenReturn(server);
            assertTrue(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        }
        assertEquals(7, inventory.getItem(0).getCount());
        assertTrue(inventory.getItem(1).isNull());
        assertFalse(source.isCurrent());
        assertFalse(source.setItem(new Item(ItemID.SNOWBALL, 0, 6)));
    }

    @Test
    void equalValueReplacementIsAnotherItemAndCannotReceiveOldConsumption() {
        InventorySlotReference source = inventory.captureHeldItem();
        inventory.slots.put(0, inventory.getItem(0));
        assertFalse(source.isCurrent());
        assertFalse(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        assertEquals(8, inventory.getItem(0).getCount());
    }

    @Test
    void inPlaceMutationAndRemovalCannotBeOverwrittenOrResurrected() {
        InventorySlotReference source = inventory.captureHeldItem();
        inventory.slots.get(0).setCount(3);
        assertFalse(source.isCurrent());
        assertFalse(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        inventory.slots.remove(0);
        assertFalse(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        assertTrue(inventory.getItem(0).isNull());
    }

    @Test
    void respectsInventoryEventCancellation() {
        InventorySlotReference source = inventory.captureHeldItem();
        doAnswer(call -> {
            ((EntityInventoryChangeEvent) call.getArgument(0)).setCancelled();
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            servers.when(Server::getInstance).thenReturn(server);
            assertFalse(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        }
        assertEquals(8, inventory.getItem(0).getCount());
    }

    @Test
    void nestedInventoryEventReplacementWinsOverOuterWrite() {
        verifyNestedReplacement(new Item(ItemID.SNOWBALL, 0, 7));
    }

    @Test
    void lastItemConsumptionCannotClearAnItemReplacedDuringTheClearEvent() {
        verifyNestedReplacement(new Item(ItemID.AIR));
    }

    private void verifyNestedReplacement(Item replacement) {
        InventorySlotReference source = inventory.captureHeldItem();
        AtomicBoolean nested = new AtomicBoolean();
        doAnswer(call -> {
            if (nested.compareAndSet(false, true)) {
                inventory.setItem(0, new Item(ItemID.ENDER_PEARL, 0, 2));
            }
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            servers.when(Server::getInstance).thenReturn(server);
            assertFalse(source.setItem(replacement));
        }
        assertEquals(ItemID.ENDER_PEARL, inventory.getItem(0).getId());
        assertEquals(2, inventory.getItem(0).getCount());
    }

    @Test
    void capturedOffhandDoesNotFollowLaterMainHandRouting() {
        PlayerOffhandInventory offhand = new PlayerOffhandInventory(player);
        offhand.slots.put(0, new Item(ItemID.SNOWBALL, 0, 8));
        when(player.getOffhandInventory()).thenReturn(offhand);
        when(player.isOffhandItemInteraction()).thenReturn(true);
        InventorySlotReference source = inventory.captureHeldItem();
        when(player.isOffhandItemInteraction()).thenReturn(false);
        assertFalse(source.isSelectedBy(inventory));
        try (MockedStatic<Server> servers = mockStatic(Server.class)) {
            servers.when(Server::getInstance).thenReturn(server);
            assertTrue(source.setItem(new Item(ItemID.SNOWBALL, 0, 7)));
        }
        assertEquals(7, offhand.getItem().getCount());
        assertEquals(8, inventory.getItem(0).getCount());
    }
}
