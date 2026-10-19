package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.Items;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 验证真实库存写回及槽位包投递，不代表已验证完整的客户端放置流程。
 */
class PredictedBlockInventorySyncTest {
    private Player owner;
    private PlayerInventory inventory;
    private PluginManager plugins;
    private MockedStatic<Server> servers;
    private List<InventorySlotPacket> ownerPackets;
    private List<InventorySlotPacket> viewerPackets;

    @BeforeAll
    static void initializeItems() {
        Block.init();
        Item.init();
    }

    @BeforeEach
    void prepare() {
        owner = mock(Player.class);
        owner.spawned = true;
        when(owner.getViewers()).thenReturn(Map.of());
        inventory = new PlayerInventory(owner);
        inventory.slots.put(0, wool(3));

        Player viewer = mock(Player.class);
        when(viewer.getWindowId(inventory)).thenReturn(1);
        inventory.getViewers().add(owner);
        inventory.getViewers().add(viewer);
        ownerPackets = new ArrayList<>();
        viewerPackets = new ArrayList<>();
        capturePackets(owner, ownerPackets);
        capturePackets(viewer, viewerPackets);

        plugins = mock(PluginManager.class);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(plugins);
        servers = mockStatic(Server.class);
        servers.when(Server::getInstance).thenReturn(server);
    }

    @AfterEach
    void closeServerEnvironment() {
        servers.close();
    }

    @Test
    void confirmedPredictionWritesRemainingBlocksAndUpdatesOnlyOtherViewers() {
        assertTrue(inventory.captureHeldItem().setItem(wool(2), owner));

        assertEquals(ItemID.WHITE_WOOL, inventory.getItem(0).getId());
        assertEquals(2, inventory.getItem(0).getCount());
        assertTrue(ownerPackets.isEmpty());
        assertSlotPacket(viewerPackets, 1, wool(2));
    }

    @Test
    void heldItemWriteWithConfirmedPredictionAlsoUpdatesOnlyOtherViewers() {
        assertTrue(inventory.setItemInHand(wool(2), owner));

        assertEquals(ItemID.WHITE_WOOL, inventory.getItem(0).getId());
        assertEquals(2, inventory.getItem(0).getCount());
        assertTrue(ownerPackets.isEmpty());
        assertSlotPacket(viewerPackets, 1, wool(2));
    }

    @Test
    void confirmedLastBlockConsumptionClearsSlotAndUpdatesOnlyOtherViewers() {
        inventory.slots.put(0, wool(1));
        assertTrue(inventory.captureHeldItem().setItem(Items.air(), owner));

        assertTrue(inventory.getItem(0).isNull());
        assertTrue(ownerPackets.isEmpty());
        assertSlotPacket(viewerPackets, 1, Items.air());
    }

    @Test
    void writeWithoutConfirmedPredictionStillSynchronizesOwner() {
        assertTrue(inventory.captureHeldItem().setItem(wool(2)));

        assertEquals(2, inventory.getItem(0).getCount());
        assertSlotPacket(ownerPackets, ContainerIds.INVENTORY, wool(2));
        assertSlotPacket(viewerPackets, 1, wool(2));
    }

    @Test
    void eventChangingConsumptionToTwoBlocksSynchronizesActualRemainder() {
        doAnswer(call -> {
            ((EntityInventoryChangeEvent) call.getArgument(0)).setNewItem(wool(1));
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));

        assertTrue(inventory.captureHeldItem().setItem(wool(2), owner));

        assertEquals(1, inventory.getItem(0).getCount());
        assertSlotPacket(ownerPackets, ContainerIds.INVENTORY, wool(1));
        assertSlotPacket(viewerPackets, 1, wool(1));
    }

    @Test
    void eventChangingBlockNbtSynchronizesEvenWhenCountDecreasesByOne() {
        Item renamed = wool(2).setCustomName("Changed by inventory event");
        doAnswer(call -> {
            ((EntityInventoryChangeEvent) call.getArgument(0)).setNewItem(renamed);
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));

        assertTrue(inventory.captureHeldItem().setItem(wool(2), owner));

        assertEquals(2, inventory.getItem(0).getCount());
        assertEquals("Changed by inventory event", inventory.getItem(0).getCustomName());
        assertSlotPacket(ownerPackets, ContainerIds.INVENTORY, renamed);
        assertSlotPacket(viewerPackets, 1, renamed);
    }

    @Test
    void cancelledConsumptionKeepsInventoryAndSendsCorrection() {
        doAnswer(call -> {
            ((EntityInventoryChangeEvent) call.getArgument(0)).setCancelled();
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));

        assertFalse(inventory.captureHeldItem().setItem(wool(2), owner));

        assertEquals(3, inventory.getItem(0).getCount());
        assertSlotPacket(ownerPackets, ContainerIds.INVENTORY, wool(3));
        assertSlotPacket(viewerPackets, 1, wool(3));
    }

    @Test
    void eventReplacingLastBlockWithAnotherItemSynchronizesReplacement() {
        inventory.slots.put(0, wool(1));
        Item replacement = Item.get(ItemID.GOLD_BLOCK, 0, 2);
        doAnswer(call -> {
            ((EntityInventoryChangeEvent) call.getArgument(0)).setNewItem(replacement);
            return null;
        }).when(plugins).callEvent(any(EntityInventoryChangeEvent.class));

        assertTrue(inventory.captureHeldItem().setItem(Items.air(), owner));

        assertEquals(ItemID.GOLD_BLOCK, inventory.getItem(0).getId());
        assertEquals(2, inventory.getItem(0).getCount());
        assertSlotPacket(ownerPackets, ContainerIds.INVENTORY, replacement);
        assertSlotPacket(viewerPackets, 1, replacement);
    }

    private static Item wool(int count) {
        return Item.get(ItemID.WHITE_WOOL, 0, count);
    }

    private static void capturePackets(Player recipient, List<InventorySlotPacket> packets) {
        doAnswer(call -> {
            packets.add(call.getArgument(0));
            return true;
        }).when(recipient).dataPacket(any(InventorySlotPacket.class));
    }

    private static void assertSlotPacket(List<InventorySlotPacket> packets, int inventoryId, Item expected) {
        assertEquals(1, packets.size());
        InventorySlotPacket packet = packets.getFirst();
        assertEquals(inventoryId, packet.inventoryId);
        assertEquals(0, packet.slot);
        assertTrue(expected.equalsExact(packet.item), "Slot packet must contain the actual authoritative item");
    }
}
